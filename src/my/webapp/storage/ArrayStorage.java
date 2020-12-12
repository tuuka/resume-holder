package my.webapp.storage;

import my.webapp.exception.ArrayStorageOverflowException;
import my.webapp.model.Resume;

import java.util.Arrays;

public class ArrayStorage {
    protected static final int STORAGE_CAPACITY = 20;

    private final Resume[] storage = new Resume[STORAGE_CAPACITY];
    private int size = 0;

    public void save(Resume resume) {
        if (size < STORAGE_CAPACITY)
            storage[size++] = resume;
        else throw new ArrayStorageOverflowException("Storage capacity exceeded!");
    }

    /**
     * @return Resume with provided uuid from storage
     */
    public Resume get(String uuid) {
        for (Resume r : storage)
            if (r.getUuid().equals(uuid)) return r;
        return null;
    }

    public void delete(String uuid) {
        for (int i = 0; i < size; i++)
            if (storage[i].getUuid().equals(uuid)) {
                storage[i] = storage[--size];
                storage[size + 1] = null;
                return;
            }
    }

    public int size() {
        return this.size;
    }

    public void clear() {
        Arrays.fill(storage, 0, storage.length, null);
        size = 0;
    }

    public Resume[] getAll() {
        if (size == 0) return null;
        return Arrays.copyOf(storage, size);
    }

    public Resume[] getAllToPosition(int pos) {
        if (size == 0) return null;
        if (pos > size) return Arrays.copyOf(storage, size);
        return Arrays.copyOf(storage, pos);
    }

    @Override
    public String toString() {
        return "ArrayStorage{" +
                "size=" + size +
                ", storage=" + Arrays.toString(storage) +
                '}';
    }
}
