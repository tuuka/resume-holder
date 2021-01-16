package my.webapp.storage;

import my.webapp.Config;

public class PostgresOneBigQueryStorageTest extends AbstractStorageTest {

    public PostgresOneBigQueryStorageTest() {
        super(new PostgresOneBigQueryStorage(
                Config.get().getDBUrl(),
                Config.get().getDBUser(),
                Config.get().getDBPassword()));
    }
}