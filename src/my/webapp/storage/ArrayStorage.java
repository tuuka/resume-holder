package my.webapp.storage;


import my.webapp.exception.ArrayStorageOverflowException;
import my.webapp.exception.ArrayStorageResumeExistsException;
import my.webapp.exception.ArrayStorageResumeNotFoundException;
import my.webapp.model.Resume;

import java.util.Arrays;

public class ArrayStorage implements Storage {

    protected static final int STORAGE_CAPACITY = 10000;

    protected final Resume[] storage = new Resume[STORAGE_CAPACITY];
    protected int size = 0;

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void clear() {
        Arrays.fill(storage, 0, size, null);
        size = 0;
    }

    @Override
    public void save(Resume resume) {
        if (findIndexOnUuid(resume.getUuid()) >= 0)
            throw new ArrayStorageResumeExistsException(resume.getUuid());
        if (size < STORAGE_CAPACITY)
            storage[size++] = resume;
        else throw new ArrayStorageOverflowException("Storage capacity exceeded!");
    }

    @Override
    public void update(Resume resume) {
        int index = findIndexOnUuid(resume.getUuid());
        if (index >= 0) storage[index] = resume;
            else throw new ArrayStorageResumeNotFoundException(resume.getUuid());
    }

    /**
     * @return Resume with provided uuid from storage
     */
    @Override
    public Resume get(String uuid) {
        int index = findIndexOnUuid(uuid);
        if (index >= 0) return storage[index];
        throw new ArrayStorageResumeNotFoundException("uuid");
    }

    @Override
    public void delete(String uuid) {
        int index = findIndexOnUuid(uuid);
        if (index >= 0) {
            storage[index] = storage[--size];
            storage[size + 1] = null;
            return;
        }
        throw new ArrayStorageResumeNotFoundException("uuid");
    }

    @Override
    public Resume[] getAll() {
        return Arrays.copyOf(storage, size);
    }

    @Override
    public Resume[] getAllToPosition(int pos) {
        if (pos > size) return Arrays.copyOf(storage, size);
        return Arrays.copyOf(storage, pos);
    }

    protected int findIndexOnUuid(String uuid) {
        for (int i = 0; i < size; i++)
            if (storage[i].getUuid().equals(uuid)) return i;
        return -1;
    }

    @Override
    public String toString() {
        return "ArrayStorage{" +
                "size=" + size +
                ", storage=" + Arrays.toString(storage) +
                '}';
    }
}
