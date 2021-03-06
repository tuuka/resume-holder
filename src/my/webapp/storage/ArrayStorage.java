package my.webapp.storage;


import my.webapp.Config;
import my.webapp.exception.ArrayStorageOverflowException;
import my.webapp.model.Resume;

import java.util.Arrays;
import java.util.List;

public class ArrayStorage extends AbstractStorage<Integer> {

    protected static final int STORAGE_CAPACITY = Config.get().getArrayCapacity();

    protected final Resume[] storage = new Resume[STORAGE_CAPACITY];
    protected int size = 0;

    @Override
    protected void doSave(Resume r, Integer key) {
        if (size < STORAGE_CAPACITY)
            storage[size++] = r;
        else throw new ArrayStorageOverflowException("Storage overflow!");
    }

    @Override
    protected void doUpdate(Resume r, Integer key) {
        storage[key] = r;
    }

    @Override
    protected Resume doGet(Integer key) {
        return storage[key];
    }

    @Override
    protected void doDelete(Integer key) {
        storage[key] = storage[--size];
        storage[size] = null;
    }

    @Override
    protected int doSize() {
        return this.size;
    }

    @Override
    protected void doClear() {
        Arrays.fill(storage, 0, size, null);
        size = 0;
    }

    @Override
    protected Integer getSearchKey(String uuid) {
        for (int i = 0; i < size; i++)
            if (storage[i].getUuid().equals(uuid)) return i;
        return -1;
    }

    @Override
    protected boolean isExist(Integer index) {
        return index >= 0;
    }

    @Override
    protected List<Resume> doGetAll() {
        return Arrays.asList(Arrays.copyOf(storage, size));
    }

    @Override
    public String toString() {
        return "ArrayStorage{" +
                "size=" + size +
                ", storage=" + Arrays.toString(storage) +
                '}';
    }
}
