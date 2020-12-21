package my.webapp.storage;

import my.webapp.exception.ArrayStorageOverflowException;
import my.webapp.model.Resume;

import java.util.Arrays;
import java.util.Comparator;

public class SortedArrayStorage extends ArrayStorage {

    @Override
    protected void doSave(Resume resume, Integer key) {
        if (size < STORAGE_CAPACITY) {
            key = -key - 1;
            System.arraycopy(storage, key, storage, key + 1, storage.length - key - 1);
            storage[key] = resume;
            size++;
        } else throw new ArrayStorageOverflowException();
    }

    @Override
    protected void doDelete(Integer key) {
        System.arraycopy(storage, key + 1, storage, key, storage.length - key - 1);
        storage[--size] = null;
    }

    @Override
    protected Integer getSearchKey(String uuid) {
        return Arrays.binarySearch(storage, 0,
                size, new Resume(uuid, "dummy"), Comparator.comparing(Resume::getUuid));
    }

    @Override
    public String toString() {
        return "SortedArrayStorage{" +
                "size=" + size +
                ", storage=" + Arrays.toString(storage) +
                '}';
    }
}
