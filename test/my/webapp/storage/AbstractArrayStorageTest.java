package my.webapp.storage;

import my.webapp.exception.ArrayStorageResumeExistsException;
import my.webapp.exception.ArrayStorageResumeNotFoundException;
import my.webapp.model.Resume;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static my.webapp.model.ResumeTest.*;
import static org.junit.Assert.*;

public abstract class AbstractArrayStorageTest {

    protected Storage storage;
    protected int storageArrayCapacity;

    public AbstractArrayStorageTest(Storage storage) {
        this.storage = storage;
        this.storageArrayCapacity = ArrayStorage.STORAGE_CAPACITY;
        for (int i = 4; i < storageArrayCapacity; i++)
            storage.save(new Resume(String.valueOf(i)));
    }

    @Test
    public void save() {
        storage.save(R4);
        assertSize(storageArrayCapacity - 3);
        assertEquals(storage.get(R4.getUuid()), R4);
        assertThrows(ArrayStorageResumeExistsException.class,
                ()->storage.save(R4));
    }

    @Test
    public void update(){
//        Resume oldResume = storage.get("5");
//        storage.update(new Resume("5"));
//        assertNotEquals(storage.get("5"), oldResume);
        Assert.assertThrows(ArrayStorageResumeNotFoundException.class,
                ()->storage.update(new Resume("dummy")));
    }

    @Test
    public void get() {
        assertEquals(storage.get("5").getUuid(), "5");
        String lastUuid = String.valueOf(storageArrayCapacity - 4);
        assertEquals(storage.get(lastUuid).getUuid(), lastUuid);
    }

    @Test(expected = ArrayStorageResumeNotFoundException.class)
    public void getDummy() {
        storage.get("dummy");
    }

    @Test
    public void delete() {
        int oldSize = storage.size();
        storage.delete("5");
        assertEquals(oldSize - 1, storage.size());
        storage.delete("6");
        assertEquals(oldSize - 2, storage.size());
        Assert.assertThrows(ArrayStorageResumeNotFoundException.class, ()->storage.get("6"));
    }

    @Test
    public void clear() {
        storage.clear();
        assertEquals(storage.size(), 0);
    }

    @Test
    public void size() {
        clearAndSave3Resumes();
        assertSize(3);
    }

    @Test
    public void getAll() {
        clearAndSave3Resumes();
        Resume[] array = storage.getAll();
        assertEquals(storage.get(R1.getUuid()), array[0]);
        assertEquals(storage.get(R2.getUuid()), array[1]);
        assertEquals(storage.get(R3.getUuid()), array[2]);
    }

    @Test
    public void getAllToPosition() {
        clearAndSave3Resumes();
        Resume[] array = storage.getAllToPosition(2);
        assertEquals(storage.get(R1.getUuid()), array[0]);
        assertEquals(storage.get(R2.getUuid()), array[1]);
        assertEquals(array.length, 2);
    }

    private void assertSize(int size) {
        assertEquals(storage.size(), size);
    }

    private void clearAndSave3Resumes() {
        storage.clear();
        storage.save(R1);
        storage.save(R2);
        storage.save(R3);
    }
}