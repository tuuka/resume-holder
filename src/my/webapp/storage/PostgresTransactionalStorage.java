package my.webapp.storage;

import my.webapp.Config;
import my.webapp.exception.StorageResumeNotFoundException;
import my.webapp.model.*;
import my.webapp.storage.sql.SQLHelper;
import my.webapp.util.DateUtil;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/* Реализация SQL Postgres Storage посредством групировки простых запросов
в транзакции*/

public class PostgresTransactionalStorage implements Storage {

    protected final SQLHelper helper;

    public PostgresTransactionalStorage() {
        this(Config.get().getDBUrl(),
                Config.get().getDBUser(),
                Config.get().getDBPassword());
    }

    public PostgresTransactionalStorage(String DBUrl, String user, String password) {
        helper = new SQLHelper(() -> {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Can't find JDBC Driver", e);
            }
            return DriverManager
                .getConnection(DBUrl, user, password);
            }, LOGGER);
    }

    @Override
    public void save(Resume resume) {
        LOGGER.info("Saving resume with Uuid=" + resume.getUuid());
        // sorting to maintain identity when deserializing (for tests)
        resume.sort();

        helper.transactionalExecute(conn -> {
            helper.executeConnected(conn,
                    "INSERT INTO resume (uuid, full_name) VALUES (?, ?)",
                    ps -> {
                        ps.setString(1, resume.getUuid());
                        ps.setString(2, resume.getFullName());
                        return ps.execute();
                    }
            );

            helper.executeConnected(conn,
                    "INSERT INTO contact (cont_type, cont_value, resume_uuid) " +
                            "VALUES (?, ?, ?)",
                    ps -> {
                        for (Map.Entry<ContactType, String> entry : resume.getContacts().entrySet()) {
                            ps.setString(1, entry.getKey().toString());
                            ps.setString(2, entry.getValue());
                            ps.setString(3, resume.getUuid());
                            ps.addBatch();
                        }
                        return ps.executeBatch();
                    }
            );

            for (Map.Entry<SectionType, Section> entry : resume.getSections().entrySet()) {
                SectionType sectionType = entry.getKey();
                int secID = helper.executeConnected(conn,
                        "INSERT INTO section (sec_type, resume_uuid) " +
                                "VALUES (?, ?) RETURNING sec_id",
                        ps -> {
                            ps.setString(1, sectionType.toString());
                            ps.setString(2, resume.getUuid());
                            ResultSet rs = ps.executeQuery();
                            rs.next();
                            return rs.getInt("sec_id");
                        }
                );
                switch (sectionType) {
                    case PERSONAL:
                    case OBJECTIVE:
                        helper.executeConnected(conn,
                                "INSERT INTO texts (texts_value, section_id) " +
                                        "VALUES (?, ?)",
                                ps -> {
                                    ps.setString(1,
                                            ((TextSection) entry.getValue())
                                                    .getContent());
                                    ps.setInt(2, secID);
                                    return ps.execute();
                                }
                        );
                        break;
                    case ACHIEVEMENT:
                    case QUALIFICATIONS:
                        helper.executeConnected(conn,
                                "INSERT INTO texts (texts_value, section_id) " +
                                        "VALUES (?, ?)",
                                ps -> {
                                    for (String text : ((ListSection) entry.getValue()).getItems()) {
                                        ps.setString(1, text);
                                        ps.setInt(2, secID);
                                        ps.addBatch();
                                    }
                                    return ps.executeBatch();
                                }
                        );
                        break;
                    case EXPERIENCE:
                    case EDUCATION:
                        for (Organization o : ((OrganizationSection) entry.getValue()).getOrganizations()) {
                            int orgID = helper.executeConnected(conn,
                                    "INSERT INTO organization " +
                                            "(org_title, org_url, section_id) " +
                                            "VALUES (?, ?, ?) RETURNING org_id",
                                    ps -> {
                                        ps.setString(1, o.getHomePage().getName());
                                        ps.setString(2, o.getHomePage().getUrl());
                                        ps.setInt(3, secID);
                                        ResultSet rs = ps.executeQuery();
                                        rs.next();
                                        return rs.getInt("org_id");
                                    }
                            );
                            helper.executeConnected(conn,
                                    "INSERT INTO position (pos_title, " +
                                            "pos_description, start_date, " +
                                            "end_date, organization_id) " +
                                            "VALUES (?, ?, ?, ?, ?)",
                                    ps -> {
                                        for (Organization.Position p : o.getPositions()) {
                                            ps.setString(1, p.getTitle());
                                            ps.setString(2, p.getDescription());
                                            ps.setString(3, DateUtil.format(p.getStartDate()));
                                            ps.setString(4, DateUtil.format(p.getEndDate()));
                                            ps.setInt(5, orgID);
                                            ps.addBatch();
                                        }
                                        return ps.executeBatch();
                                    }
                            );
                        }
                        break;
                }
            }
            return null;
        });
    }

    @Override
    public void update(Resume resume) {
        delete(resume.getUuid());
        save(resume);
    }

    @Override
    public Resume get(String uuid) {
        return helper.transactionalExecute(conn -> {
            String fullName = helper.executeConnected(conn,
                    "SELECT full_name FROM resume WHERE resume.uuid=?",
                    ps -> {
                        ps.setString(1, uuid);
                        ResultSet rs = ps.executeQuery();
                        if (!rs.next()) throw new SQLException(
                                "Can't find resume uuid=" + uuid);
                        return rs.getString("full_name");
                    }
            );
            Resume r = new Resume(uuid, fullName);

            helper.executeConnected(conn,
                    "SELECT cont_type, cont_value " +
                            "FROM resume " +
                            "JOIN contact c ON resume.uuid = c.resume_uuid " +
                            "WHERE resume.uuid=?",
                    ps -> {
                        ps.setString(1, uuid);
                        ResultSet rs = ps.executeQuery();
                        while (rs.next())
                            r.setContact(ContactType.valueOf(rs.getString("cont_type")),
                                    rs.getString("cont_value"));
                        return null;
                    }
            );

            helper.executeConnected(conn,
                    "SELECT sec_type ,texts_value, org_title, org_url " +
                            ",pos_title, pos_description,start_date, end_date " +
                            "FROM resume " +
                            "JOIN section on resume.uuid = section.resume_uuid " +
                            "LEFT JOIN organization on section.sec_id = organization.section_id " +
                            "LEFT JOIN position on organization.org_id = position.organization_id " +
                            "LEFT JOIN texts on section.sec_id = texts.section_id " +
                            "WHERE resume.uuid=?",
                    ps -> {
                        ps.setString(1, uuid);
                        ResultSet rs = ps.executeQuery();
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
                    });
            r.sort();
            return r;
        });
    }

    @Override
    public void delete(String uuid) {
        LOGGER.info("Deleting resume uuid =" + uuid);
        helper.connectAndExecute("DELETE FROM resume WHERE resume.uuid=?",
                statement -> {
                    statement.setString(1, uuid);
                    if (statement.executeUpdate() == 0) {
                        LOGGER.warning("Can't delete resume uuid=" + uuid);
                        throw new StorageResumeNotFoundException(
                                "Can't delete resume uuid=" + uuid);
                    }
                    return null;
                });
    }

    @Override
    public int size() {
        return helper.connectAndExecute("SELECT count(*) FROM resume",
                statement -> {
                    ResultSet rs = statement.executeQuery();
                    return rs.next() ? rs.getInt(1) : 0;
                });
    }

    @Override
    public void clear() {
        LOGGER.info("Clearing all resumes.");
        helper.connectAndExecute("DELETE FROM resume",
                PreparedStatement::execute);
    }

    @Override
    public Resume[] getAll() {
        return helper.connectAndExecute("SELECT uuid FROM resume",
                statement -> {
                    ResultSet rs = statement.executeQuery();
                    List<Resume> resumes = new ArrayList<>();
                    while (rs.next()) {
                        resumes.add(get(rs.getString("uuid")));
                    }
                    return resumes.toArray(new Resume[0]);
                });
    }

    @Override
    public Resume[] getAllToPosition(int pos) {
        if (pos > size()) pos = size();
        return Arrays.copyOfRange(getAll(), 0, pos);
    }

}
