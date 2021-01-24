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
    private final String DBUrl;
    private final String DBUser;
    private final String DBPassword;

    public static Config get(){return INSTANCE;}

    private Config(){
//        try(InputStream is = getClass().getClassLoader().getResourceAsStream("resumes.properties")){
//        try(InputStream is = new FileInputStream("/resources/resumes.properties")){
            try (InputStream is = Config.class.getResourceAsStream(PROPS_FILE)) {
            Properties properties = new Properties();
            properties.load(is);
            storageDir = properties.getProperty("storage.dir");
            arrayCapacity = Integer
                    .parseInt(properties.getProperty("array_storage.capacity"));
            DBUrl = properties.getProperty("db.url");
            DBUser = properties.getProperty("db.user");
            DBPassword = properties.getProperty("db.password");
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

    public Storage getStorage() { return storage; }
}
