package my.webapp.storage;

import my.webapp.Config;
import my.webapp.exception.ArrayStorageOverflowException;
import my.webapp.exception.StorageException;
import my.webapp.model.ContactType;
import my.webapp.model.Resume;
import my.webapp.model.Section;
import my.webapp.model.SectionType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static my.webapp.model.ResumeTest.RESUMES;
import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractStorageTest {
    protected static String STORAGE_DIR = Config.get().getStorageDir();
    protected static Logger logger;
    protected static Level loggerLevel;

    protected final int workingQuantity = 5;
    protected Storage storage;

    public AbstractStorageTest(Storage storage) {
        this.storage = storage;
    }

    @BeforeAll
    static void setUpAll() {
        logger = Storage.LOGGER;
        loggerLevel = logger.getLevel();
        logger.setLevel(Level.OFF);
    }

    @BeforeEach
    public void setUp() {
        storage.clear();
        RESUMES.subList(0, workingQuantity).forEach(storage::save);
    }

    @Test
    public void save() {
        storage.save(RESUMES.get(workingQuantity));
        assertEquals(workingQuantity + 1, storage.size(),
                "Storage size didn't increase after saving!");
        assertThrows(StorageException.class,
                () -> storage.save(RESUMES.get(0)),
                "Attempt to save existing resume must throw " +
                        "'StorageResumeExistsException'!");
    }

    @Test
    public void saveOverflowTest() {
        if (storage instanceof ArrayStorage) {
            storage.clear();
            RESUMES.forEach(storage::save);
            assertThrows(ArrayStorageOverflowException.class,
                    () -> storage.save(new Resume("overflow")),
                    "ArrayStorage overflow must throw " +
                            "'ArrayStorageOverflowException'!");
        }
    }

    @Test
    public void update() {
        Resume r = new Resume(RESUMES.get(0).getUuid(), RESUMES.get(0).getFullName());
        for (Map.Entry<ContactType, String> entry :
                RESUMES.get(workingQuantity - 1).getContacts().entrySet()) {
            r.setContact(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<SectionType, Section> entry :
                RESUMES.get(workingQuantity - 1).getSections().entrySet()) {
            r.setSection(entry.getKey(), entry.getValue());
        }
        storage.update(r);
        assertNotEquals(RESUMES.get(0), storage.get(RESUMES.get(0).getUuid()),
                "Original and modified resumes must not be equal!");
        assertThrows(StorageException.class,
                () -> storage.update(new Resume("dummy")),
                "Attempt to change a non-existent resume must throw" +
                        " 'StorageException'");
    }

    @Test
    public void get() {
        assertAll("getting resumes",
                () -> assertEquals(RESUMES.get(0), storage.get(RESUMES.get(0).getUuid())),
                () -> assertEquals(RESUMES.get(1), storage.get(RESUMES.get(1).getUuid()))
        );
        assertThrows(StorageException.class,
                () -> storage.get("dummy"),
                "Attempt to get a non-existent resume must throw" +
                        " 'StorageException'");
    }

    @Test
    public void delete() {
        storage.delete(RESUMES.get(0).getUuid());
        assertEquals(workingQuantity - 1, storage.size(),
                "Storage size didn't changed after deletion!");
        assertThrows(StorageException.class,
                () -> storage.delete(RESUMES.get(0).getUuid()),
                "Attempt to delete a non-existent resume must throw" +
                        " 'StorageException'!");
    }

    @Test
    public void size() {
        storage.save(new Resume());
        assertEquals(workingQuantity + 1, storage.size(),
                "Storage size have to " +
                "be equal to number of stored resumes!");
    }

    @Test
    public void clear() {
        storage.clear();
        assertEquals(0, storage.size(),
                "clear() must zero storage size!");
        assertThrows(StorageException.class,
                () -> storage.get(RESUMES.get(0).getUuid()),
                "After clear() there must not be any resumes!");
    }

    @Test
    public void getAllSorted() {
        List<Resume> earnedList = storage.getAllSorted();
        assertEquals(storage.size(), earnedList.size(),
                "Number of saved and retrieved resumes must be equal!");
        assertAll("Sorted resumes should have same order",
                () -> assertEquals(RESUMES.get(0), earnedList.get(0)),
                () -> assertEquals(RESUMES.get(1), earnedList.get(1))
        );
    }

//    @Test
//    public void getAllSortedTime(){
//        for (int i = 0; i < 50; i++)
//            storage.getAllSorted();
//    }

    @AfterAll
    static void tearDown() {
        if (logger != null) {
            logger.setLevel(loggerLevel);
        }
    }
}