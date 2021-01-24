package my.webapp.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import my.webapp.Config;
import my.webapp.model.ContactType;
import my.webapp.model.Resume;
import my.webapp.model.Section;
import my.webapp.model.SectionType;
import my.webapp.storage.sql.SQLHelper;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

/* Реализация SQL Postgres Storage в глубоко упрощеном виде, когда секции и
* контакты преобразоуются в Json-cтроку, которая и сохраняется в таблице БД.*/

public class PostgresJsonStorage implements Storage {

    private final SQLHelper helper;

    public PostgresJsonStorage() {
        this(Config.get().getDBUrl(), Config.get().getDBUser(),
                Config.get().getDBPassword());
    }

    public PostgresJsonStorage(String DBUrl, String user, String password) {
        helper = new SQLHelper(() -> DriverManager
                .getConnection(DBUrl, user, password), LOGGER);
    }

    @Override
    public void save(Resume resume) {
        LOGGER.info("Saving resume with Uuid=" + resume.getUuid());
        resume.sort();
        helper.connectAndExecute("INSERT INTO resume_json " +
                        "(uuid, full_name, contacts, sections) " +
                        "VALUES (?, ?, ?, ?)",
                ps -> {
                    int rs = setStatement(ps, resume.getUuid(), resume.getFullName(),
                            SQLHelper.convert(om -> om.writeValueAsString(resume.getContacts())),
                            SQLHelper.convert(om -> om.writeValueAsString(resume.getSections()))
                    ).executeUpdate();
                    if (rs < 1) throw new SQLException("Error saving Resume uuid=" + resume.getUuid());
                    return rs;
                }
        );
    }

    @Override
    public void update(Resume resume) {
        LOGGER.info("Updating resume with Uuid=" + resume.getUuid());
        resume.sort();
        helper.connectAndExecute(
                "UPDATE resume_json SET" +
                        "(uuid, full_name, contacts, sections) " +
                        "= (?, ?, ?, ?) WHERE uuid=?",
                ps -> {
                    int rs = setStatement(ps, resume.getUuid(), resume.getFullName(),
                            SQLHelper.convert(om -> om.writeValueAsString(resume.getContacts())),
                            SQLHelper.convert(om -> om.writeValueAsString(resume.getSections())),
                            resume.getUuid()
                    ).executeUpdate();
                    if (rs < 1) throw new SQLException("Nothing updated in Resume uuid=" + resume.getUuid());
                    return rs;
                });
    }

    @Override
    public Resume get(String uuid) {
        LOGGER.info("Getting resume with Uuid=" + uuid);
        return helper.connectAndExecute(
                "SELECT * FROM resume_json WHERE uuid=?",
                ps -> {
                    ps.setString(1, uuid);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next())
                        throw new SQLException("Resume uuid=" + uuid + " not found!");
                    return getResume(rs);
                });
    }

    @Override
    public void delete(String uuid) {
        helper.connectAndExecute(
                "DELETE FROM resume_json WHERE uuid=?",
                ps -> {
                    ps.setString(1, uuid);
                    int rs = ps.executeUpdate();
                    if (rs < 1) throw new SQLException(
                            "Nothing deleted for Resume uuid=" + uuid);
                    return null;
                }
        );
    }

    @Override
    public int size() {
        return helper.connectAndExecute("SELECT count(*) FROM resume_json",
                statement -> {
                    ResultSet rs = statement.executeQuery();
                    return rs.next() ? rs.getInt(1) : 0;
                });
    }

    @Override
    public void clear() {
        LOGGER.info("Clearing all resumes.");
        helper.connectAndExecute("DELETE FROM resume_json",
                PreparedStatement::execute);
    }

    @Override
    public Resume[] getAll() {
        return helper.connectAndExecute("SELECT * FROM resume_json",
                ps -> {
                    ResultSet rs = ps.executeQuery();
                    List<Resume> resumes = new ArrayList<>();
                    while (rs.next()) {
                        resumes.add(getResume(rs));
                    }
                    return resumes.toArray(new Resume[0]);
                });
    }

    @Override
    public Resume[] getAllToPosition(int pos) {
        if (pos > size()) pos = size();
        return Arrays.copyOfRange(getAll(), 0, pos);
    }

    private Resume getResume(ResultSet rs) throws SQLException {
        Resume r = new Resume(rs.getString("uuid"), rs.getString("full_name"));
        TypeReference<EnumMap<ContactType, String>> contactTypeRef
                = new TypeReference<>() {
        };
        SQLHelper.convert(om -> om.readValue(
                rs.getString("contacts"), contactTypeRef))
                .forEach(r::setContact);
        TypeReference<EnumMap<SectionType, Section>> sectionTypeRef
                = new TypeReference<>() {
        };
        SQLHelper.convert(om -> om.readValue(
                rs.getString("sections"), sectionTypeRef))
                .forEach(r::setSection);
        r.sort();
        return r;
    }

    private PreparedStatement setStatement(PreparedStatement ps, String... args)
            throws SQLException {
        for (int i = 0; i < args.length; i++)
            ps.setString(i + 1, args[i]);
        return ps;
    }
}
