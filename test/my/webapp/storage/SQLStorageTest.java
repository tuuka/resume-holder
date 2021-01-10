package my.webapp.storage;

import my.webapp.Config;

public class SQLStorageTest extends AbstractStorageTest {

    public SQLStorageTest() {
        super(new PostgresStorage(
                Config.get().getDBUrl(),
                Config.get().getDBUser(),
                Config.get().getDBPassword()));
    }
}