package my.webapp.storage.sql;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import my.webapp.exception.StorageException;
import my.webapp.model.ListSection;
import my.webapp.model.OrganizationSection;
import my.webapp.model.TextSection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLHelper {
    private final Logger LOGGER;
    private final SQLConnectionFactory connectionFactory;
    private static ObjectMapper OM = null;

    public SQLHelper(SQLConnectionFactory connectionFactory, Logger logger) {
        this.connectionFactory = connectionFactory;
        this.LOGGER = logger;
    }

    public <T> T executeConnected(Connection conn, String sql,
                                   ExecuteFunction<T> func) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            return func.apply(ps);
        }
    }

    public <T> T connectAndExecute(String sql, ExecuteFunction<T> func) {
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            return func.apply(statement);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new StorageException(e.getMessage(), e);
        }
    }

    public <T> T transactionalExecute(TransactionalFunction<T> func) {
        try (Connection conn = connectionFactory.getConnection()) {
            try {
                conn.setAutoCommit(false);
                T result = func.apply(conn);
                conn.commit();
                return result;
            } catch (SQLException e) {
                conn.rollback();
//                String state = e.getSQLState();
//                if (state != null)
//                    if (state.equals("23505"))
//                        throw new SQLException("Resume exists!");
                throw new SQLException(e);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new StorageException(e.getMessage(), e);
        }
    }

    public interface SQLConnectionFactory {
        Connection getConnection() throws SQLException;
    }

    public interface ExecuteFunction<T> {
        T apply(PreparedStatement statement)
                throws SQLException;
    }

    public interface TransactionalFunction<T> {
        T apply(Connection conn)
                throws SQLException;
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

    public void addToBuilder(StringBuilder sb,
                              String InitString,
                              String params) {
        if (sb.length() == 0) sb.append(InitString);
        else sb.append(", ");
        sb.append(params);
    }

    public interface convertFunction<T, R>{
        R apply(T t) throws JsonProcessingException, SQLException;
    }

    public static <T> T convert(convertFunction<ObjectMapper, T> func)
            throws SQLException {
        try {
            return func.apply(getObjectMapper());
        } catch (JsonProcessingException e) {
            throw new SQLException("Error Json converting!");
        }
    }

    private static ObjectMapper getObjectMapper(){
        if (OM == null) {
            OM = new ObjectMapper()
//                .configure(SerializationFeature.INDENT_OUTPUT, true)
            ;
            OM.setDefaultTyping(
                    ObjectMapper.DefaultTypeResolverBuilder
                            .construct(ObjectMapper.DefaultTyping.EVERYTHING,
                                    BasicPolymorphicTypeValidator
                                            .builder()
                                            .allowIfBaseType("my.webapp")
                                            .allowIfSubType(TextSection.class)
                                            .allowIfSubType(ListSection.class)
                                            .allowIfSubType(OrganizationSection.class)
                                            .allowIfSubType(ArrayList.class)
                                            .allowIfSubType(EnumMap.class)
                                            .build())
                            .init(JsonTypeInfo.Id.CLASS, null)
                            .inclusion(JsonTypeInfo.As.PROPERTY)
                            .typeProperty("@type"));
        }
        return OM;
    }
}
