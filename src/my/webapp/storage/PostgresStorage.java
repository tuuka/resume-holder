package my.webapp.storage;

import my.webapp.exception.StorageException;
import my.webapp.model.*;
import my.webapp.util.DateUtil;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;


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
        sqlUpdate(conn -> {
            conn.prepareStatement(
                    String.format("INSERT INTO resume (uuid, full_name) VALUES('%s', '%s')",
                            resume.getUuid(), resume.getFullName())).execute();

            //saving contacts
            int mapSize = resume.getContacts().size();
            StringBuilder sql = new StringBuilder();
            List<String> args = new ArrayList<>();
            if (mapSize > 0) {
                sql.append("INSERT INTO contact (cont_type, cont_value, resume_uuid) VALUES");
                for (Map.Entry<ContactType, String> entry : resume.getContacts().entrySet()) {
                    sql.append("(?, ?, ?)");
                    args.add(entry.getKey().toString());
                    args.add(entry.getValue());
                    args.add(resume.getUuid());
                    if (--mapSize > 0) sql.append(",");
                }
                saveElement(conn, sql.toString(), args.toArray(new String[0]));
            }

            //saving sections
            for (Map.Entry<SectionType, Section> entry : resume.getSections().entrySet()) {
                int sec_id = saveElement(conn,
                        "INSERT INTO section " +
                                "(sec_type, resume_uuid) VALUES(?,?)",
                        entry.getKey().toString(),
                        resume.getUuid());
                switch (entry.getKey()) {
                    case PERSONAL:
                    case OBJECTIVE:
                        saveElement(conn,
                                "INSERT INTO texts " +
                                        "(texts_value, section_id) VALUES(?,?)",
                                sec_id,
                                ((TextSection) entry.getValue()).getContent());
                        break;
                    case ACHIEVEMENT:
                    case QUALIFICATIONS:
                        for (String text : ((ListSection) entry.getValue()).getItems()) {
                            saveElement(conn,
                                    "INSERT INTO texts " +
                                            "(texts_value, section_id) VALUES(?,?)",
                                    sec_id,
                                    text);
                        }
                        break;
                    case EXPERIENCE:
                    case EDUCATION:
                        List<Organization> organizationList =
                                ((OrganizationSection) entry.getValue())
                                        .getOrganizations();
                        for (Organization o : organizationList) {
                            int org_id = saveElement(
                                    conn,
                                    "INSERT INTO organization (org_title, org_url, " +
                                            "section_id) VALUES(?,?,?)",
                                    sec_id,
                                    o.getHomePage().getName(),
                                    o.getHomePage().getUrl()
                            );
                            List<Organization.Position> positions = o.getPositions();
                            for (Organization.Position p : positions) {
                                saveElement(conn,
                                        "INSERT INTO position (pos_title, pos_description, " +
                                                "start_date, end_date, organization_id) " +
                                                "VALUES(?,?,?,?,?)",
                                        org_id,
                                        p.getTitle(),
                                        p.getDescription(),
                                        DateUtil.format(p.getStartDate()),
                                        DateUtil.format(p.getEndDate()));
                            }
                        }
                        break;
                }
            }
        });
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
        sqlUpdate(conn -> {
            PreparedStatement statement = conn.prepareStatement(
                    "DELETE FROM resume WHERE resume.uuid=?");
            statement.setString(1, uuid);
            if (statement.executeUpdate() == 0)
                throw new StorageException("Can't delete resume uuid=" + uuid);
        });
    }

    @Override
    public int size() {
        return sqlQuery(conn -> {
            PreparedStatement statement = conn
                    .prepareStatement("SELECT * FROM resume");
            return statement.executeQuery();
        }).size();
    }

    @Override
    public void clear() {
        LOGGER.info("Clearing all resumes.");
        sqlUpdate(conn -> {
            PreparedStatement statement = conn.prepareStatement(
                    "DELETE FROM resume");
            statement.execute();
        });
    }

    @Override
    public Resume[] getAll() {
        List<Map<String, Object>> rMapList = sqlQuery(conn -> {
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

    private void sqlUpdate(sqlUpdFunction<Connection> func) {
        try (Connection conn = connectionFactory.getConnection()) {
            func.apply(conn);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new StorageException(e.getMessage(), e);
        }
    }

    private List<Map<String, Object>> selectWhereFieldEqualsArg(String sql,
                                                                String field,
                                                                String arg) {
        return sqlQuery(conn -> {
            PreparedStatement statement = conn
                    .prepareStatement(sql + " " + field + "=?");
            statement.setString(1, arg);
            return statement.executeQuery();
        });
    }

    private List<Map<String, Object>> sqlQuery(
            sqlSelectFunction<Connection, ResultSet> func) {
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
//                        String colName = meta.getColumnTypeName(i);
//                        String clName = meta.getColumnClassName(i);
//                        if (meta.getColumnClassName(i).equals("java.sql.Array"))
//                            row.put(name, rs.getArray(i).getArray());
//                        else
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

    private int saveElement(Connection conn,
                            String sql, String... args) throws SQLException {
        return saveElement(conn, sql, null, args);
    }

    private int saveElement(Connection conn,
                            String sql, Integer int_id, String... args) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < args.length; i++) {
            statement.setString(i + 1, args[i]);
        }
        if (int_id != null)
            statement.setInt(args.length + 1, int_id);

        // Retrieve auto-generated id
        if (statement.executeUpdate() <= 0)
            throw new SQLException("Didn't generate id of saved element.");
        ResultSet resultSet = statement.getGeneratedKeys();
        resultSet.next();
        return resultSet.getInt(1);
    }

    private interface sqlUpdFunction<T> {
        void apply(T t) throws SQLException;
    }

    private interface sqlSelectFunction<T, R> {
        R apply(T t) throws SQLException;
    }
}
