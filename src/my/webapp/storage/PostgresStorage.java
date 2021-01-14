package my.webapp.storage;

import my.webapp.exception.StorageException;
import my.webapp.exception.StorageResumeNotFoundException;
import my.webapp.model.*;
import my.webapp.util.DateUtil;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;


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

public class PostgresStorage implements Storage {

    public interface SQLConnectionFactory {
        Connection getConnection() throws SQLException;
    }

    public final SQLConnectionFactory connectionFactory;

    public PostgresStorage(String DBUrl, String user, String password) {
        connectionFactory = () -> DriverManager.getConnection(DBUrl, user, password);
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
                addToBuilder(sectionDataBuilder,
                        "\n, sec_data (sec_type) AS (VALUES ",
                        "(?)");
                sectionParameters.add(entry.getKey().toString());
                SectionType secType = entry.getKey();
                switch (secType) {
                    case PERSONAL:
                    case OBJECTIVE:
                        addToBuilder(textDataBuilder,
                                textBuilderInitString,
                                "(?, ?)");
                        textParameters.add(((TextSection) entry.getValue()).getContent());
                        textParameters.add(secType.toString());
                        break;
                    case ACHIEVEMENT:
                    case QUALIFICATIONS:
                        for (String text : ((ListSection) entry.getValue()).getItems()) {
                            addToBuilder(textDataBuilder,
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
                            addToBuilder(orgDataBuilder,
                                    "\n, org_data (org_title, org_url, sec_type) AS (VALUES ",
                                    "(?, ?, ?)");
                            orgParameters.add(o.getHomePage().getName());
                            orgParameters.add(o.getHomePage().getUrl());
                            orgParameters.add(secType.toString());

                            for (Organization.Position p : o.getPositions()) {
                                addToBuilder(posDataBuilder,
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
        execute(statement -> {
            for (int i = 0; i < parameters.size(); i++)
                statement.setString(i + 1, parameters.get(i));
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                LOGGER.warning("Error when saving resume uuid = "
                        + resume.getUuid());
                throw new StorageException(
                        "Error when saving resume uuid = "
                                + resume.getUuid());
            }
            return null;
        }, queryBuilder.toString());
    }

    @Override
    public void update(Resume resume) {
        delete(resume.getUuid());
        save(resume);
    }

    @Override
    public Resume get(String uuid) {
        Resume r =
                execute(statement -> {
                            statement.setString(1, uuid);
                            ResultSet rs = statement.executeQuery();
                            if (!rs.next()) {
                                LOGGER.warning("Error when getting resume uuid = "
                                        + uuid);
                                throw new StorageResumeNotFoundException(
                                        "Error when getting resume uuid = "
                                                + uuid);
                            }
                            return new Resume(uuid, rs.getString("full_name"));
                        },
                        "SELECT * FROM resume WHERE uuid=?");

        /* getting contacts */
        execute(statement -> {
                    statement.setString(1, uuid);
                    ResultSet rs = statement.executeQuery();
                    while (rs.next()) {
                        r.setContact(ContactType.valueOf(
                                rs.getString("cont_type")),
                                rs.getString("cont_value"));
                    }
                    return null;
                },
                "SELECT cont_type, cont_value " +
                        "FROM resume " +
                        "RIGHT JOIN contact c on resume.uuid = c.resume_uuid " +
                        "WHERE resume.uuid=?");

        /* getting sections */
        execute(statement -> {
            statement.setString(1, uuid);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                SectionType sec_type = SectionType.valueOf(
                        rs.getString("sec_type"));
                switch (sec_type) {
                    case PERSONAL:
                    case OBJECTIVE:
                        r.setSection(sec_type,
                                new TextSection(rs.getString("texts_value")));
                        break;
                    case ACHIEVEMENT:
                    case QUALIFICATIONS:
                        ListSection list_sec = Optional.ofNullable(
                                (ListSection) r.getSection(sec_type))
                                .orElseGet(() -> {
                                    ListSection ls = new ListSection(new ArrayList<>());
                                    r.setSection(sec_type, ls);
                                    return ls;
                                });
                        list_sec.addItem(rs.getString("texts_value"));
                        break;
                    case EXPERIENCE:
                    case EDUCATION:
                        OrganizationSection org_sec = Optional.ofNullable(
                                (OrganizationSection) r.getSection(sec_type))
                                .orElseGet(() -> {
                                    OrganizationSection os = new OrganizationSection(new ArrayList<>());
                                    r.setSection(sec_type, os);
                                    return os;
                                });
                        String org_title = rs.getString("org_title");
                        String org_url = rs.getString("org_url");
                        Organization organization = org_sec.getOrganizations()
                                .stream()
                                .filter(o ->
                                        o.getHomePage().getName().equals(org_title) &&
                                                o.getHomePage().getUrl().equals(org_url))
                                .findFirst()
                                .orElseGet(() -> {
                                    Organization o = new Organization(org_title, org_url);
                                    org_sec.addOrganization(o);
                                    return o;
                                });
                        organization.addPosition(
                                rs.getString("start_date"),
                                rs.getString("end_date"),
                                rs.getString("pos_title"),
                                rs.getString("pos_description"));
                }
            }
            return null;
        }, "SELECT sec_type ,texts_value, org_title, org_url " +
                ",pos_title, pos_description,start_date, end_date " +
                "FROM resume " +
                "RIGHT JOIN section on resume.uuid = section.resume_uuid " +
                "LEFT JOIN organization on section.sec_id = organization.section_id " +
                "LEFT JOIN position on organization.org_id = position.organization_id " +
                "LEFT JOIN texts on section.sec_id = texts.section_id " +
                "WHERE resume.uuid=?");
        r.sort();
        return r;
    }

    @Override
    public void delete(String uuid) {
        LOGGER.info("Deleting resume uuid =" + uuid);
        execute(statement -> {
            statement.setString(1, uuid);
            if (statement.executeUpdate() == 0) {
                LOGGER.warning("Can't delete resume uuid=" + uuid);
                throw new StorageResumeNotFoundException(
                        "Can't delete resume uuid=" + uuid);
            }
            return null;
        }, "DELETE FROM resume WHERE resume.uuid=?");
    }

    @Override
    public int size() {
        return execute(statement -> {
                    ResultSet rs = statement.executeQuery();
                    return rs.next() ? rs.getInt(1) : 0;
                },
                "SELECT count(*) FROM resume");
    }

    @Override
    public void clear() {
        LOGGER.info("Clearing all resumes.");
        execute(PreparedStatement::execute, "DELETE FROM resume");
    }

    @Override
    public Resume[] getAll() {
        return execute(statement -> {
            ResultSet rs = statement.executeQuery();
            List<Resume> resumes = new ArrayList<>();
            while (rs.next()) {
                resumes.add(get(rs.getString("uuid")));
            }
            return resumes.toArray(new Resume[0]);
        }, "SELECT uuid FROM resume");
    }

    @Override
    public Resume[] getAllToPosition(int pos) {
        if (pos > size()) pos = size();
        return Arrays.copyOfRange(getAll(), 0, pos);
    }

    private <T> T execute(
            executeFunction<T> func,
            String sql) {
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            return func.apply(statement);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new StorageException(e.getMessage(), e);
        }
    }

//    private List<Map<String, String>> convertRsIntoListOfMap(ResultSet rs) throws SQLException {
//        List<Map<String, String>> results = new ArrayList<>();
//        ResultSetMetaData meta = rs.getMetaData();
//        int numColumns = meta.getColumnCount();
//        while (rs.next()) {
//            Map<String, String> row = new HashMap<>();
//            for (int i = 1; i <= numColumns; ++i) {
//                row.put(meta.getColumnName(i),
//                        rs.getString(i));
//            }
//            results.add(row);
//        }
//        return results;
//    }

    private void addToBuilder(StringBuilder sb,
                              String InitString,
                              String params) {
        if (sb.length() == 0) sb.append(InitString);
        else sb.append(", ");
        sb.append(params);
    }

    private interface executeFunction<R> {
        R apply(java.sql.PreparedStatement statement)
                throws SQLException;
    }
}
