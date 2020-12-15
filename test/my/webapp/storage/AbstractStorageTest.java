package my.webapp.storage;

import my.webapp.exception.ArrayStorageOverflowException;
import my.webapp.exception.StorageResumeExistsException;
import my.webapp.exception.StorageResumeNotFoundException;
import my.webapp.model.Resume;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static my.webapp.model.ResumeTest.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public abstract class AbstractStorageTest {

    protected Storage storage;
    protected final int CAPACITY = ArrayStorage.STORAGE_CAPACITY;

    public AbstractStorageTest(Storage storage) {
        this.storage = storage;
    }

    @Before
    public void setUp() {

    }

    @Test
    public void save() {
        for (int i = 4; i < CAPACITY; i++)
            storage.save(new Resume(String.valueOf(i)));
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
                storage.save(new Resume(String.valueOf(i)));
            assertThrows(ArrayStorageOverflowException.class,
                    () -> storage.save(new Resume("overflow")));
        }
    }

    @Test
    public void update() {
        storage.save(R1);
        storage.save(R2);
//        Resume oldResume = storage.get("5");
//        storage.update(new Resume("5"));
//        assertNotEquals(storage.get("5"), oldResume);
        Assert.assertThrows(StorageResumeNotFoundException.class,
                () -> storage.update(new Resume("dummy")));
    }

    @Test
    public void get() {
        storage.save(R1);
        storage.save(R2);
        assertEquals(R2, storage.get(R2.getUuid()));
        assertEquals(R1.getUuid(), storage.get(R1.getUuid()).getUuid());
        Assert.assertThrows(StorageResumeNotFoundException.class,
                () -> storage.get("dummy"));
    }

    @Test
    public void delete() {
        storage.clear();
        for (int i = CAPACITY; i > 0; i--)
            storage.save(new Resume(String.valueOf(i)));
        assertEquals(CAPACITY, storage.size());
        for (int i = CAPACITY; i > 0; i--) {
            storage.delete(String.valueOf(i));
        }
        assertEquals(0, storage.size());
        storage.save(R2);
        storage.delete(R2.getUuid());
        Assert.assertThrows(StorageResumeNotFoundException.class,
                () -> storage.get(R2.getUuid()));
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
        Assert.assertThrows(StorageResumeNotFoundException.class,
                () -> storage.get(R2.getUuid()));
    }

    @Test
    public void getAll() {
        storage.save(R1);
        storage.save(R2);
        storage.save(R3);
        Resume[] array = storage.getAll();
        assertEquals(array.length, storage.size());
        if (storage instanceof ArrayStorage) {
            assertEquals(storage.get(R1.getUuid()), array[0]);
            assertEquals(storage.get(R2.getUuid()), array[1]);
            assertEquals(storage.get(R3.getUuid()), array[2]);
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

}