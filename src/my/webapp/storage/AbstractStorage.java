package my.webapp.storage;

import my.webapp.exception.StorageResumeExistsException;
import my.webapp.exception.StorageResumeNotFoundException;
import my.webapp.model.Resume;

import java.util.Arrays;
import java.util.Date;
import java.util.logging.*;


/* Абстрактный класс для всех хранилищ. K - ключ, используемый для поиска
* Имплементирует все методы интерфейса Storage, используя дополнительные
* абстрактные методы, реализация которых будет осуществлена в
* дочерних классах. */

public abstract class AbstractStorage<K> implements Storage{

    private static Logger LOGGER;

//    static {
//        //https://www.logicbig.com/tutorials/core-java-tutorial/logging/customizing-default-format.html
//        System.setProperty("java.util.logging.SimpleFormatter.format",
//                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
//        LOGGER = Logger.getLogger(ArrayStorage.class.getName());
//    }

    static {
        SimpleFormatter sf = new SimpleFormatter() {
            private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(format,
                        new Date(lr.getMillis()),
                        lr.getLevel().getLocalizedName(),
                        lr.getMessage()
                );
            }
        };

        Logger mainLogger = Logger.getLogger(AbstractStorage.class.getPackageName());
        mainLogger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(sf);

//        FileHandler fileHandler;
//        try {
//            fileHandler = new FileHandler("D:\\temp\\storage.log", true);
//            fileHandler.setFormatter(sf);
//            mainLogger.addHandler(fileHandler);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        mainLogger.addHandler(handler);
        LOGGER = Logger.getLogger(ArrayStorage.class.getName());
        LOGGER.setLevel(Level.WARNING);
    }

    protected abstract void doSave(Resume r, K key);

    protected abstract void doUpdate(Resume r, K key);

    protected abstract Resume doGet(K key);

    protected abstract void doDelete(K key);

    protected abstract int doSize();

    protected abstract void doClear();

    protected abstract K getSearchKey(String uuid);

    protected abstract boolean isExist(K key);

    protected abstract Resume[] doGetAll();

    /* Если ключ не uuid (для Map) */
    private K getExistedSearchKey(String uuid) {
        K key = getSearchKey(uuid);
        if (!isExist(key)) {
            LOGGER.warning("Resume uuid = " + uuid + " is not exist");
            throw new StorageResumeNotFoundException(uuid);
        }
        return key;
    }

    private K getNotExistedSearchKey(String uuid) {
        K key = getSearchKey(uuid);
        if (isExist(key)) {
            LOGGER.warning("Resume uuid = " + uuid + " already exist");
            throw new StorageResumeExistsException(uuid);
        }
        return key;
    }

    @Override
    public void save(Resume r) {
        LOGGER.info("Saving resume " + r + ".");
        doSave(r, getNotExistedSearchKey(r.getUuid()));
    }

    @Override
    public void update(Resume r) {
        LOGGER.info("Updating resume " + r + ".");
        doUpdate(r, getExistedSearchKey(r.getUuid()));
    }

    @Override
    public Resume get(String uuid) {
        LOGGER.info("Getting resume with uuid = " + uuid + ".");
        return doGet(getExistedSearchKey(uuid));
    }

    @Override
    public void delete(String uuid) {
        LOGGER.info("Deleting resume with uuid = " + uuid + ".");
        doDelete(getExistedSearchKey(uuid));
    }

    @Override
    public int size() {
        return doSize();
    }

    @Override
    public void clear() {
        doClear();
    }

    @Override
    public Resume[] getAll() {
        return doGetAll();
    }

    @Override
    public Resume[] getAllToPosition(int pos) {
        if (pos > size()) pos = size();
        return Arrays.copyOfRange(getAll(), 0, pos);
    }
}
