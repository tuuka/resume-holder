package my.webapp;

import my.webapp.storage.PostgresTransactionalStorage;
import my.webapp.storage.Storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Config INSTANCE = new Config();
    private static final String PROPS_FILE = "/resumes.properties";

    private final String storageDir;
    private final Storage storage;
    private final int arrayCapacity;
    private String DBUrl;
    private String DBUser;
    private String DBPassword;

    public static Config get() {
        return INSTANCE;
    }

    private Config() {
//        try(InputStream is = getClass().getClassLoader().getResourceAsStream("resumes.properties")){
//        try(InputStream is = new FileInputStream("/resources/resumes.properties")){
        try (InputStream is = Config.class.getResourceAsStream(PROPS_FILE)) {
            Properties properties = new Properties();
            properties.load(is);
            storageDir = properties.getProperty("storage.dir");
            arrayCapacity = Integer
                    .parseInt(properties.getProperty("array_storage.capacity"));
            DBUrl = System.getenv("JDBC_DATABASE_URL");
            if (DBUrl != null) DBUrl = DBUrl.replace("postgres://", "jdbc:postgresql://");
            DBUser = System.getenv("JDBC_DATABASE_USERNAME");
            DBPassword = System.getenv("JDBC_DATABASE_PASSWORD");
            if (DBUrl == null || DBUrl.length() == 0 ||
                    DBUser == null || DBUser.length() == 0 ||
                    DBPassword == null || DBPassword.length() == 0) {
                DBUrl = properties.getProperty("db.url");
                DBUser = properties.getProperty("db.user");
                DBPassword = properties.getProperty("db.password");
            }
            storage = new PostgresTransactionalStorage(DBUrl, DBUser, DBPassword);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read properties file!", e);
        }
    }

    public String getStorageDir() {
        return storageDir;
    }

    public int getArrayCapacity() {
        return arrayCapacity;
    }

    public String getDBUrl() {
        return DBUrl;
    }

    public String getDBUser() {
        return DBUser;
    }

    public String getDBPassword() {
        return DBPassword;
    }

    public Storage getStorage() {
        return storage;
    }
}
