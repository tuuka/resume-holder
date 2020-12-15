package my.webapp.storage;


import my.webapp.exception.ArrayStorageOverflowException;
import my.webapp.model.Resume;

import java.util.Arrays;

public class ArrayStorage extends AbstractStorage<Integer> {

    protected static final int STORAGE_CAPACITY = 20;

    protected final Resume[] storage = new Resume[STORAGE_CAPACITY];
    protected int size = 0;


    @Override
    protected void doSave(Resume r, Integer key) {
//        if (isExist(r.getUuid()))
//            throw new StorageResumeExistsException(r.getUuid());
        if (size < STORAGE_CAPACITY)
            storage[size++] = r;
        else throw new ArrayStorageOverflowException("Storage overflow!");
    }

    @Override
    protected void doUpdate(Resume r, Integer key) {
        storage[key] = r;
//        int index = findIndexOnUuid(resume.getUuid());
//        if (index >= 0) storage[index] = resume;
//        else throw new StorageResumeNotFoundException(resume.getUuid());
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
    protected boolean isExist(String uuid) {
        return getSearchKey(uuid) >= 0;
    }

    @Override
    protected Resume[] doGetAll() {
        return Arrays.copyOf(storage, size);
    }

//
//    /**
//     * @return Resume with provided uuid from storage
//     */
//    @Override
//    public Resume get(String uuid) {
//        int index = findIndexOnUuid(uuid);
//        if (index >= 0) return storage[index];
//        throw new StorageResumeNotFoundException("uuid");
//    }

//    @Override
//    public void delete(String uuid) {
//        int index = findIndexOnUuid(uuid);
//        if (index >= 0) {
//            storage[index] = storage[--size];
//            storage[size + 1] = null;
//            return;
//        }
//        throw new StorageResumeNotFoundException("uuid");
//    }

//    @Override
//    public Resume[] getAll() {
//        return Arrays.copyOf(storage, size);
//    }
//
//    @Override
//    public Resume[] getAllToPosition(int pos) {
//        if (pos > size) return Arrays.copyOf(storage, size);
//        return Arrays.copyOf(storage, pos);
//    }


    @Override
    public String toString() {
        return "ArrayStorage{" +
                "size=" + size +
                ", storage=" + Arrays.toString(storage) +
                '}';
    }
}
