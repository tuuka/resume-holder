package my.webapp.storage;

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
    public void setUp(){
        for (int i = 4; i < CAPACITY; i++)
            storage.save(new Resume(String.valueOf(i)));
        storage.save(R1);
        storage.save(R2);
    }

    @Test
    public void save() {
        assertEquals(storage.size(), CAPACITY - 2);
        storage.save(R3);
        storage.save(R4);
        assertThrows(StorageResumeExistsException.class,
                () -> storage.save(R4));
    }

    @Test
    public void update() {
//        Resume oldResume = storage.get("5");
//        storage.update(new Resume("5"));
//        assertNotEquals(storage.get("5"), oldResume);
        Assert.assertThrows(StorageResumeNotFoundException.class,
                () -> storage.update(new Resume("dummy")));
    }

    @Test
    public void get() {
        assertEquals(storage.get(R2.getUuid()), R2);
        String lastUuid = String.valueOf(CAPACITY - 4);
        assertEquals(storage.get(lastUuid).getUuid(), lastUuid);
        Assert.assertThrows(StorageResumeNotFoundException.class,
                () -> storage.get("dummy"));
    }

    @Test
    public void delete() {
        int oldSize = storage.size();
        storage.delete(R1.getUuid());
        assertEquals(oldSize - 1, storage.size());
        storage.delete(R2.getUuid());
        assertEquals(oldSize - 2, storage.size());
        Assert.assertThrows(StorageResumeNotFoundException.class,
                () -> storage.get(R2.getUuid()));
    }

    @Test
    public void size() {
        assertEquals(storage.size(), CAPACITY - 2);
    }

    @Test
    public void clear() {
        storage.clear();
        assertEquals(storage.size(), 0);
    }

    @Test
    public void getAll() {
        Resume[] array = storage.getAll();
        assertEquals(storage.size(), array.length);
    }

    @Test
    public void getAllToPosition() {
        Resume[] array = storage.getAllToPosition(2);
        assertEquals(array.length, 2);
    }

}