package my.webapp.storage;

import my.webapp.Config;
import my.webapp.exception.ArrayStorageOverflowException;
import my.webapp.exception.StorageException;
import my.webapp.model.ContactType;
import my.webapp.model.Resume;
import my.webapp.model.Section;
import my.webapp.model.SectionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static my.webapp.model.ResumeTest.R1;
import static my.webapp.model.ResumeTest.R2;
import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractStorageTest {
    protected static String STORAGE_DIR = Config.get().getStorageDir();
    protected static Logger logger;
    protected static Level loggerLevel;

    protected Storage storage;
    protected static final int CAPACITY = ArrayStorage.STORAGE_CAPACITY;
    protected static final List<Resume> resumes = Resume.generateNFakeResumes(CAPACITY);

    public AbstractStorageTest(Storage storage) {
        this.storage = storage;
    }

    @BeforeEach
    public void setUp() {
        logger = Storage.LOGGER;
        loggerLevel = logger.getLevel();
        logger.setLevel(Level.OFF);
        storage.clear();
    }

    @Test
    public void save() {
        for (int i = 4; i < CAPACITY; i++)
            storage.save(resumes.get(i));
        storage.save(R1);
        storage.save(R2);
        assertEquals(storage.size(), CAPACITY - 2,
                "Storage size didn't increase after saving!");
        assertThrows(StorageException.class,
                () -> storage.save(R1),
                "Attempt to save existing resume have to throw " +
                        "'StorageResumeExistsException'!");
    }

    @Test
    public void saveOverflowTest() {
        if (storage instanceof ArrayStorage) {
            for (int i = 0; i < CAPACITY; i++)
                storage.save(new Resume(String.valueOf(i), String.valueOf(i)));
            assertThrows(ArrayStorageOverflowException.class,
                    () -> storage.save(new Resume("overflow")));
        }
    }

    @Test
    public void update() {
        storage.save(resumes.get(0));
        storage.save(resumes.get(1));
        Resume r = new Resume(resumes.get(0).getUuid(), resumes.get(0).getFullName());
        for (Map.Entry<ContactType, String> entry :
                resumes.get(2).getContacts().entrySet()) {
            r.setContact(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<SectionType, Section> entry :
                resumes.get(2).getSections().entrySet()) {
            r.setSection(entry.getKey(), entry.getValue());
        }
        storage.update(r);
        assertNotEquals(resumes.get(0), storage.get(resumes.get(0).getUuid()),
                "Original and modified resume should not be equal!");
        assertThrows(StorageException.class,
                () -> storage.update(new Resume("dummy")),
                "Attempt to change a non-existent resume should throw" +
                        " 'StorageException'");
    }

    @Test
    public void get() {
        storage.save(resumes.get(0));
        storage.save(resumes.get(1));
        assertAll("getting resumes",
                () -> assertEquals(resumes.get(0), storage.get(resumes.get(0).getUuid())),
                () -> assertEquals(resumes.get(1), storage.get(resumes.get(1).getUuid()))
        );
        assertThrows(StorageException.class,
                () -> storage.get("dummy"),
                "Attempt to get a non-existent resume should throw" +
                        " 'StorageException'");
    }

    @Test
    public void delete() {
        storage.save(resumes.get(0));
        storage.save(resumes.get(1));
        storage.delete(resumes.get(0).getUuid());
        assertEquals(1, storage.size(),
                "Storage size didn't decrease after deletion!");
        storage.delete(resumes.get(1).getUuid());
        assertThrows(StorageException.class,
                () -> storage.delete(resumes.get(1).getUuid()),
                "Attempt to delete a non-existent resume should throw" +
                        " 'StorageException'!");
    }

    @Test
    public void size() {
        storage.save(resumes.get(0));
        storage.save(resumes.get(1));
        assertEquals(2, storage.size(), "Storage size have to " +
                "be equal to number of stored resumes!");
    }

    @Test
    public void clear() {
        storage.save(resumes.get(0));
        storage.save(resumes.get(1));
        storage.clear();
        assertEquals(0, storage.size(),
                "Clear() have to zero storage size!");
        assertThrows(StorageException.class,
                () -> storage.get(resumes.get(0).getUuid()),
                "After clear() there have not be any resumes!");
    }

    @Test
    public void getAllSorted() {
        resumes.stream().sorted().limit(5).forEach(storage::save);
        Collections.sort(resumes);
        List<Resume> earnedList = storage.getAllSorted();
        assertEquals(storage.size(), earnedList.size());
//        if (storage instanceof ArrayStorage) {
        assertAll("Sorted resumes should have same order",
                () -> assertEquals(resumes.get(0), earnedList.get(0)),
                () -> assertEquals(resumes.get(1), earnedList.get(1))
        );
//        }
    }

    @AfterEach
    public void tearDown() {
        storage.clear();
        if (logger != null) {
            logger.setLevel(loggerLevel);
        }
    }
}