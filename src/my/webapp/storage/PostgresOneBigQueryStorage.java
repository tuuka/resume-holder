package my.webapp.storage;

import my.webapp.model.*;
import my.webapp.util.DateUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/* При сохранении резюме генерируется и выполняется всего
 один сложный SQL-запрос, типа такого: */
/*
with
    resume_ins as (
        insert into resume (uuid, full_name) values ('uuid5', 'name5')
            --on conflict do nothing
            returning uuid)

    , contacts_data (cont_type, cont_value) as (
        values  ('MOBILE', '+123456789'),
                ('HOME',   '+987654321')
    )

   , contact_insert as (
    insert into contact (cont_type, cont_value, resume_uuid)
        (select cont_type, cont_value,
                uuid from contacts_data, resume_ins)
        returning cont_id
    )

   , sec_data (sec_type) as (values ('PERSONAL')
                                   ,('OBJECTIVE')
                                   ,('ACHIEVEMENT')
                                   ,('QUALIFICATIONS')
                                   ,('EXPERIENCE')
                                   ,('EDUCATION'))

   , section_insert as (
    insert into section (sec_type, resume_uuid)
        (select sec_type,
                uuid from sec_data, resume_ins)
        returning sec_type, sec_id)

   , text_data (text_value, sec_type) as (
   values ('PERSONAL_data', 'PERSONAL')
        , ('OBJECTIVE_data1', 'OBJECTIVE')
        , ('OBJECTIVE_data2', 'OBJECTIVE')
        , ('QUALIFICATIONS_data', 'QUALIFICATIONS'))

   , org_data (org_title, org_url, sec_type) as (
    values ('exporg1', 'www.exporg1.com', 'EXPERIENCE')
         , ('exporg2', 'www.exporg2.com', 'EXPERIENCE')
         , ('exporg3', 'www.exporg3.com', 'EXPERIENCE')
         , ('eduorg1', 'www.eduorg1.com', 'EDUCATION'))

    , text_insert as (
    insert into texts (texts_value, section_id)
        select text_value , sec_id
        from text_data, section_insert
        where section_insert.sec_type = text_data.sec_type)

    , org_insert as (
    insert into organization (org_title, org_url, section_id)
        select org_title, org_url, sec_id
        from org_data, section_insert
        where section_insert.sec_type = org_data.sec_type
    returning org_title, org_url, org_id)

    , pos_data (pos_title, pos_description, start_date, end_date, org_title, org_url) as(
        values
                ('worker1', 'worker1descr', '01/2000', '02/2001', 'exporg1', 'www.exporg1.com')
                ,('worker2', 'worker1descr', '02/2001', '02/2002', 'exporg2', 'www.exporg2.com')
                ,('student3', 'student1descr', '03/2002', '02/2003', 'eduorg1', 'www.eduorg1.com'))

insert into position (pos_title, pos_description, start_date, end_date, organization_id)
    select pos_title, pos_description, start_date, end_date, org_id
    from pos_data, org_insert
    where org_insert.org_title = pos_data.org_title and
          org_insert.org_url = pos_data.org_url
*/

public class PostgresOneBigQueryStorage extends PostgresTransactionalStorage {

    public PostgresOneBigQueryStorage(String DBUrl, String user, String password) {
        super(DBUrl, user, password);
    }

    @Override
    public void save(Resume resume) {
        LOGGER.info("Saving resume with Uuid=" + resume.getUuid());
        // sorting to maintain identity when deserializing
        resume.sort();

        List<String> parameters = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder(
                "WITH\n" +
                        "resume_ins AS (\n" +
                        "INSERT INTO RESUME (uuid, full_name) VALUES (?, ?)\n" +
                        "RETURNING *)"
        );
        parameters.add(resume.getUuid());
        parameters.add(resume.getFullName());
        int contactsSize = resume.getContacts().size();
        if (contactsSize > 0) {
            queryBuilder.append("\n, contacts_data (cont_type, cont_value) AS (\n VALUES ");
            for (Map.Entry<ContactType, String> entry : resume.getContacts().entrySet()) {
                queryBuilder.append("(?, ?)");
                parameters.add(entry.getKey().toString());
                parameters.add(entry.getValue());
                if (--contactsSize > 0) queryBuilder.append(",");
            }
            queryBuilder.append(")\n" +
                    ", contact_insert AS (\n" +
                    "  INSERT INTO contact (cont_type, cont_value, resume_uuid)\n" +
                    "     (select cont_type, cont_value, uuid\n" +
                    "          from contacts_data, resume_ins)\n" +
                    "  RETURNING cont_id)"
            );
        }

        if (resume.getSections().size() > 0) {
            StringBuilder sectionDataBuilder = new StringBuilder();
            StringBuilder orgDataBuilder = new StringBuilder();
            StringBuilder textDataBuilder = new StringBuilder();
            StringBuilder posDataBuilder = new StringBuilder();
            List<String> orgParameters = new ArrayList<>();
            List<String> textParameters = new ArrayList<>();
            List<String> sectionParameters = new ArrayList<>();
            List<String> posParameters = new ArrayList<>();
            String textBuilderInitString = "\n, text_data (text_value, sec_type) AS (VALUES ";
            String posBuilderInitString = "\n, pos_data (pos_title, pos_description, " +
                    "start_date, end_date, org_title, org_url) AS (VALUES ";

            for (Map.Entry<SectionType, Section> entry : resume.getSections().entrySet()) {
                helper.addToBuilder(sectionDataBuilder,
                        "\n, sec_data (sec_type) AS (VALUES ",
                        "(?)");
                sectionParameters.add(entry.getKey().toString());
                SectionType secType = entry.getKey();
                switch (secType) {
                    case PERSONAL:
                    case OBJECTIVE:
                        helper.addToBuilder(textDataBuilder,
                                textBuilderInitString,
                                "(?, ?)");
                        textParameters.add(((TextSection) entry.getValue()).getContent());
                        textParameters.add(secType.toString());
                        break;
                    case ACHIEVEMENT:
                    case QUALIFICATIONS:
                        for (String text : ((ListSection) entry.getValue()).getItems()) {
                            helper.addToBuilder(textDataBuilder,
                                    textBuilderInitString,
                                    "(?, ?)");
                            textParameters.add(text);
                            textParameters.add(secType.toString());
                        }
                        break;
                    case EXPERIENCE:
                    case EDUCATION:
                        List<Organization> organizationList =
                                ((OrganizationSection) entry.getValue())
                                        .getOrganizations();
                        for (Organization o : organizationList) {
                            helper.addToBuilder(orgDataBuilder,
                                    "\n, org_data (org_title, org_url, sec_type) AS (VALUES ",
                                    "(?, ?, ?)");
                            orgParameters.add(o.getHomePage().getName());
                            orgParameters.add(o.getHomePage().getUrl());
                            orgParameters.add(secType.toString());

                            for (Organization.Position p : o.getPositions()) {
                                helper.addToBuilder(posDataBuilder,
                                        posBuilderInitString,
                                        "(?, ?, ?, ?, ?, ?)");
                                posParameters.add(p.getTitle());
                                posParameters.add(p.getDescription());
                                posParameters.add(DateUtil.format(p.getStartDate()));
                                posParameters.add(DateUtil.format(p.getEndDate()));
                                posParameters.add(o.getHomePage().getName());
                                posParameters.add(o.getHomePage().getUrl());
                            }
                        }
                        break;
                }
            }
            sectionDataBuilder.append(")\n" +
                    ", section_insert AS (\n" +
                    "    INSERT INTO SECTION (sec_type, resume_uuid)\n" +
                    "        (select sec_type,\n" +
                    "                uuid FROM sec_data, resume_ins)\n" +
                    "    RETURNING sec_type, sec_id)");
            textDataBuilder.append(")\n" +
                    ", text_insert AS (\n" +
                    "    INSERT INTO texts (texts_value, section_id)\n" +
                    "        SELECT text_value , sec_id\n" +
                    "        FROM text_data, section_insert\n" +
                    "        WHERE section_insert.sec_type = text_data.sec_type)");
            orgDataBuilder.append(")\n" +
                    ", org_insert AS (\n" +
                    "    INSERT INTO organization (org_title, org_url, section_id)\n" +
                    "        SELECT org_title, org_url, sec_id\n" +
                    "        FROM org_data, section_insert\n" +
                    "        WHERE section_insert.sec_type = org_data.sec_type\n" +
                    "    RETURNING org_title, org_url, org_id)");
            posDataBuilder.append(")\n" +
                    ", pos_insert AS (\n" +
                    "    INSERT INTO position (pos_title, pos_description, start_date, end_date, organization_id)\n" +
                    "       SELECT pos_title, pos_description, start_date, end_date, org_id\n" +
                    "       FROM pos_data, org_insert\n" +
                    "       WHERE org_insert.org_title = pos_data.org_title AND\n" +
                    "         org_insert.org_url = pos_data.org_url\n" +
                    "   RETURNING *)");

            queryBuilder.append(sectionDataBuilder.toString());
            parameters.addAll(sectionParameters);
            queryBuilder.append(textDataBuilder.toString());
            parameters.addAll(textParameters);
            queryBuilder.append(orgDataBuilder.toString());
            parameters.addAll(orgParameters);
            queryBuilder.append(posDataBuilder.toString());
            parameters.addAll(posParameters);
        }
        queryBuilder.append(
                "SELECT uuid FROM resume_ins"
        );
        helper.connectAndExecute(queryBuilder.toString(),
                statement -> {
                    for (int i = 0; i < parameters.size(); i++)
                        statement.setString(i + 1, parameters.get(i));
                    ResultSet rs = statement.executeQuery();
                    if (!rs.next()) {
                        LOGGER.warning("Error when saving resume uuid = "
                                + resume.getUuid());
                        throw new SQLException(
                                "Error when saving resume uuid = "
                                        + resume.getUuid());
                    }
                    return rs;
                });
    }


    @Override
    public Resume get(String uuid) {
        return helper.connectAndExecute(
                "SELECT R.uuid, R.full_name, c.cont_type,\n" +
                        "       c.cont_value, s.sec_type, t.texts_value,\n" +
                        "       org_title, o.org_url, p.start_date, p.end_date,\n" +
                        "       p.pos_title, p.pos_description FROM resume R\n" +
                        "JOIN contact c on R.uuid = c.resume_uuid\n" +
                        "JOIN section s on R.uuid = s.resume_uuid\n" +
                        "LEFT JOIN texts t on s.sec_id = t.section_id\n" +
                        "LEFT JOIN organization o on s.sec_id = o.section_id\n" +
                        "LEFT JOIN position p on o.org_id = p.organization_id\n" +
                        "WHERE uuid=?",
                ps -> {
                    ps.setString(1, uuid);
                    List<Resume> result = getResumesFromResultSet(ps.executeQuery());
                    if (result.size() == 0) throw new SQLException(
                            "Can't find resume uuid=" + uuid);
                    return result.get(0);
                }
        );
    }
}
