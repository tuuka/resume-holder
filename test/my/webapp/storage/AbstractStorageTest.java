package my.webapp.storage;

import my.webapp.exception.ArrayStorageOverflowException;
import my.webapp.exception.StorageResumeExistsException;
import my.webapp.exception.StorageResumeNotFoundException;
import my.webapp.model.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import static my.webapp.model.ResumeTest.*;
import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractStorageTest {
    protected static Logger logger;
    protected static Level loggerLevel;

    static {
        try {
            Field loggerFiled = AbstractStorage.class.getDeclaredField("LOGGER");
            loggerFiled.setAccessible(true);
            logger = ((Logger)loggerFiled
                    .get(AbstractStorage.class));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    protected Storage storage;
    protected final int CAPACITY = ArrayStorage.STORAGE_CAPACITY;

    public AbstractStorageTest(Storage storage) {
        this.storage = storage;
    }

    @BeforeAll
    public static void disableLogging(){
        if (logger != null) {
            loggerLevel = logger.getLevel();
            logger.setLevel(Level.OFF);
        }
    }

    @BeforeEach
    public void setUp() {
        storage.clear();
    }

    @Test
    public void save() {
        for (int i = 4; i < CAPACITY; i++)
            storage.save(new Resume(String.valueOf(i), String.valueOf(i)));
        storage.save(R1);
        storage.save(R2);
        assertEquals(storage.size(), CAPACITY - 2);
        storage.save(R3);
        storage.save(R4);
        assertThrows(StorageResumeExistsException.class,
                () -> storage.save(R4));
    }

    @Test
    public void saveOverflowTest() {
        if (storage instanceof ArrayStorage) {
            storage.clear();
            for (int i = 0; i < CAPACITY; i++)
                storage.save(new Resume(String.valueOf(i), String.valueOf(i)));
            assertThrows(ArrayStorageOverflowException.class,
                    () -> storage.save(new Resume("overflow")));
        }
    }

    @Test
    public void update() {
        storage.save(R1);
        storage.save(R2);
        Resume oldResume = storage.get(R2.getUuid());
        storage.update(new Resume(R2.getUuid(), "dummy"));
        assertNotEquals(storage.get(R2.getUuid()), oldResume);
        assertThrows(StorageResumeNotFoundException.class,
                () -> storage.update(new Resume("dummy")));
    }

    @Test
    public void get() {
        storage.save(R1);
        storage.save(R2);
        assertEquals(R2, storage.get(R2.getUuid()));
        assertEquals(R1.getUuid(), storage.get(R1.getUuid()).getUuid());
        assertThrows(StorageResumeNotFoundException.class,
                () -> storage.get("dummy"));
    }

    @Test
    public void delete() {
        storage.clear();
        for (int i = CAPACITY; i > 0; i--)
            storage.save(new Resume(String.valueOf(i), String.valueOf(i)));
        assertEquals(CAPACITY, storage.size());
        for (int i = CAPACITY; i > 0; i--) {
            storage.delete(String.valueOf(i));
        }
        assertEquals(0, storage.size());
        storage.save(R2);
        storage.delete(R2.getUuid());
        assertThrows(StorageResumeNotFoundException.class,
                () -> storage.delete(R2.getUuid()));
    }

    @Test
    public void size() {
        storage.save(R1);
        storage.save(R2);
        assertEquals(2, storage.size());
    }

    @Test
    public void clear() {
        storage.clear();
        assertEquals(storage.size(), 0);
        assertThrows(StorageResumeNotFoundException.class,
                () -> storage.get(R2.getUuid()));
    }

    @Test
    public void getAll() {
        storage.save(new Resume("a", "a"));
        storage.save(new Resume("b", "b"));
        storage.save(new Resume("c", "c"));
        Resume[] array = storage.getAll();
        assertEquals(array.length, storage.size());
        if (storage instanceof ArrayStorage) {
            assertEquals("a", storage.get("a").getUuid());
            assertEquals("b", storage.get("b").getUuid());
            assertEquals("c", storage.get("c").getUuid());
        }
    }

    @Test
    public void getAllToPosition() {
        storage.save(R1);
        storage.save(R2);
        storage.save(R3);
        Resume[] array = storage.getAllToPosition(2);
        assertEquals(array.length, 2);
    }

    @Test
    public void fulfilledResumeTest(){
        storage.save(R1);
        Resume r_loaded = storage.get("dummy.dummy");
        assertEquals("dummy", r_loaded.getFullName());
        assertEquals("Very good person that can work hard in area of digging.",
                ((TextSection)r_loaded.getSection(SectionType.PERSONAL)).getContent());
        assertEquals(3, ((OrganizationSection)r_loaded.getSection(SectionType.EXPERIENCE)).getOrganizations().size());
        assertEquals("student", ((OrganizationSection)r_loaded.getSection(SectionType.EDUCATION))
                .getOrganizations().get(0).getPositions().get(0).getTitle());
    }

    @AfterEach
    public void tearDown() {
        storage.clear();
    }

    @AfterAll
    public static void restoreLogging(){
        if (logger != null) {
            logger.setLevel(loggerLevel);
        }
    }
}