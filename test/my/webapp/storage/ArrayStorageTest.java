package my.webapp.storage;

import my.webapp.model.Resume;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static my.webapp.model.ResumeTest.*;
import static org.junit.Assert.assertEquals;

public class ArrayStorageTest {
    protected ArrayStorage storage;
    protected int storageArrayCapacity;

    @Before
    public void setUp() throws Exception {
        this.storage = new ArrayStorage();
        Field capacityFiled = ArrayStorage.class.getDeclaredField("STORAGE_CAPACITY");
        capacityFiled.setAccessible(true);
        this.storageArrayCapacity = capacityFiled.getInt(storage);
        for (int i = 4; i < storageArrayCapacity; i++)
            storage.save(new Resume(String.valueOf(i)));
    }

    @Test
    public void save() {
        storage.save(R4);
        assertSize(storageArrayCapacity - 3);
    }

    @Test
    public void get() {
        assertEquals(storage.get("15").getUuid(), "15");
        assertEquals(storage
                        .get(String.valueOf(storageArrayCapacity - 4))
                        .getUuid(),
                String.valueOf(storageArrayCapacity - 4));
    }

    @Test
    public void delete() {
        int oldSize = storage.size();
        storage.delete("5");
        assertEquals(oldSize - 1, storage.size());
        storage.delete("6");
        assertEquals(oldSize - 2, storage.size());
    }

    @Test
    public void clear() {
        storage.clear();
        assertEquals(storage.size(), 0);
    }

    @Test
    public void size() {
        save123();
        assertSize(3);
    }

    @Test
    public void getAll() {
        save123();
        Resume[] array = storage.getAll();
        assertEquals(storage.get(R1.getUuid()), array[0]);
        assertEquals(storage.get(R2.getUuid()), array[1]);
        assertEquals(storage.get(R3.getUuid()), array[2]);
    }

    @Test
    public void getAllToPosition() {
        save123();
        Resume[] array = storage.getAllToPosition(2);
        assertEquals(storage.get(R1.getUuid()), array[0]);
        assertEquals(storage.get(R2.getUuid()), array[1]);
        assertEquals(array.length, 2);
    }

    private void assertSize(int size) {
        assertEquals(storage.size(), size);
    }

    private void save123(){
        storage.clear();
        storage.save(R1);
        storage.save(R2);
        storage.save(R3);
    }
}