package my.webapp.storage;

import my.webapp.exception.StorageException;
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
                        "\tINSERT INTO RESUME (uuid, full_name) VALUES (?, ?)\n" +
                        "\tRETURNING *)"
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
        connectAndExecuteQuery(conn -> {
                    PreparedStatement statement = conn.prepareStatement(
                            queryBuilder.toString(), Statement.RETURN_GENERATED_KEYS);
                    for (int i = 0; i < parameters.size(); i++)
                        statement.setString(i + 1, parameters.get(i));
                    ResultSet rs = statement.executeQuery();
                    rs.next();
                    String savedUuid = rs.getString(1);
                    if (!resume.getUuid().equals(savedUuid))
                        throw new StorageException("Error when saving resume uuid = " + resume.getUuid());
                    return rs;
                }
        );
    }

    @Override
    public void update(Resume resume) {
        delete(resume.getUuid());
        save(resume);
    }

    @Override
    public Resume get(String uuid) {
        List<Map<String, Object>> mapList =
                selectWhereFieldEqualsArg(
                        "SELECT * FROM resume WHERE", "uuid", uuid);
        if (mapList.size() == 0)
            throw new StorageException("Can not read resume with uuid=" + uuid);
        Resume r = new Resume((String) mapList.get(0).get("uuid"),
                (String) mapList.get(0).get("full_name"));
        /* getting contacts */
        mapList = selectWhereFieldEqualsArg(
                "SELECT cont_type, cont_value " +
                        "FROM resume " +
                        "RIGHT JOIN contact c on resume.uuid = c.resume_uuid " +
                        "WHERE", "resume.uuid", uuid);
        mapList.forEach(contact -> r.setContact(
                ContactType.valueOf((String) contact.get("cont_type")),
                (String) contact.get("cont_value")));
        /* getting sections */
        mapList = selectWhereFieldEqualsArg(
                "select sec_type " +
                        ",texts_value " +
                        ",org_title, org_url " +
                        ",pos_title, pos_description,start_date, end_date " +
                        "from resume " +
                        "right join section on resume.uuid = section.resume_uuid " +
                        "left join organization on section.sec_id = organization.section_id " +
                        "left join position on organization.org_id = position.organization_id " +
                        "left join texts on section.sec_id = texts.section_id " +
                        "where", "resume.uuid", uuid
        );
        mapList.forEach(row -> {
            SectionType sec_type = SectionType.valueOf(
                    (String) row.get("sec_type"));
            switch (sec_type) {
                case PERSONAL:
                case OBJECTIVE:
                    r.setSection(sec_type,
                            new TextSection((String) row.get("texts_value")));
                    break;
                case ACHIEVEMENT:
                case QUALIFICATIONS:
                    ListSection list_sec = Optional.ofNullable(
                            (ListSection) r.getSection(sec_type))
                            .orElseGet(() -> {
                                r.setSection(sec_type, new ListSection(new ArrayList<>()));
                                return (ListSection) r.getSection(sec_type);
                            });
                    list_sec.addItem((String) row.get("texts_value"));
                    break;
                case EXPERIENCE:
                case EDUCATION:
                    OrganizationSection org_sec = Optional.ofNullable(
                            (OrganizationSection) r.getSection(sec_type))
                            .orElseGet(() -> {
                                r.setSection(sec_type, new OrganizationSection(new ArrayList<>()));
                                return (OrganizationSection) r.getSection(sec_type);
                            });
                    String org_title = (String) row.get("org_title");
                    String org_url = (String) row.get("org_url");
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
                            (String) row.get("start_date"),
                            (String) row.get("end_date"),
                            (String) row.get("pos_title"),
                            (String) row.get("pos_description"));
            }
        });
        r.sort();
        return r;
    }

    @Override
    public void delete(String uuid) {
        LOGGER.info("Deleting resume uuid =" + uuid);
        connectAndExecuteUpdate(conn -> {
            PreparedStatement statement = conn.prepareStatement(
                    "DELETE FROM resume WHERE resume.uuid=?");
            statement.setString(1, uuid);
            if (statement.executeUpdate() == 0)
                throw new StorageException("Can't delete resume uuid=" + uuid);
        });
    }

    @Override
    public int size() {
        return connectAndExecuteQuery(conn -> {
            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM resume");
            return statement.executeQuery();
        }).size();
    }

    @Override
    public void clear() {
        LOGGER.info("Clearing all resumes.");
        connectAndExecuteUpdate(conn -> {
            PreparedStatement statement = conn.prepareStatement(
                    "DELETE FROM resume");
            statement.execute();
        });
    }

    @Override
    public Resume[] getAll() {
        List<Map<String, Object>> rMapList = connectAndExecuteQuery(conn -> {
            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM resume");
            return statement.executeQuery();
        });
        List<Resume> resumes = new ArrayList<>();
        for (Map<String, Object> rMap : rMapList) {
            resumes.add(this.get((String) rMap.get("uuid")));
        }
        return resumes.toArray(new Resume[0]);
    }

    @Override
    public Resume[] getAllToPosition(int pos) {
        if (pos > size()) pos = size();
        return Arrays.copyOfRange(getAll(), 0, pos);
    }

    private void connectAndExecuteUpdate(executeUpdateFunction<Connection> func) {
        try (Connection conn = connectionFactory.getConnection()) {
            func.apply(conn);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new StorageException(e.getMessage(), e);
        }
    }

    private List<Map<String, Object>> connectAndExecuteQuery(
            executeQueryFunction<Connection, ResultSet> func) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection conn = connectionFactory.getConnection()) {
            ResultSet rs = func.apply(conn);
            if (rs != null) {
                ResultSetMetaData meta = rs.getMetaData();
                int numColumns = meta.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= numColumns; ++i) {
                        String name = meta.getColumnName(i);
                        row.put(name, rs.getObject(i));
                    }
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new StorageException(e.getMessage(), e);
        }
        return results;
    }

    private List<Map<String, Object>> selectWhereFieldEqualsArg(String sql,
                                                                String field,
                                                                String arg) {
        return connectAndExecuteQuery(conn -> {
            PreparedStatement statement = conn
                    .prepareStatement(sql + " " + field + "=?");
            statement.setString(1, arg);
            return statement.executeQuery();
        });
    }

    private void addToBuilder(StringBuilder sb,
                              String InitString,
                              String params) {
        if (sb.length() == 0) sb.append(InitString);
        else sb.append(", ");
        sb.append(params);
    }

    private interface executeUpdateFunction<T> {
        void apply(T t) throws SQLException;
    }

    private interface executeQueryFunction<T, R> {
        R apply(T t) throws SQLException;
    }
}
