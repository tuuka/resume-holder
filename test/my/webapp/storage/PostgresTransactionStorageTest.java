package my.webapp.storage;

import my.webapp.Config;

public class PostgresTransactionStorageTest extends AbstractStorageTest {

    public PostgresTransactionStorageTest() {
        super(new PostgresTransactionalStorage(
                Config.get().getDBUrl(),
                Config.get().getDBUser(),
                Config.get().getDBPassword()));
    }
}