package my.webapp.storage;

import my.webapp.exception.StorageResumeNotFoundException;
import my.webapp.exception.ArrayStorageOverflowException;
import my.webapp.exception.StorageResumeExistsException;
import my.webapp.model.Resume;

import java.util.Arrays;

public class SortedArrayStorage extends ArrayStorage {

    @Override
    public void save(Resume resume) {
        int index = Arrays.binarySearch(Arrays.copyOf(storage, size), resume);
        if (index > -1) throw new StorageResumeExistsException(resume.getUuid());
        if (size == STORAGE_CAPACITY) throw new ArrayStorageOverflowException();
        else {
            index = -index - 1;
            System.arraycopy(storage, index, storage, index + 1, storage.length - index - 1);
            storage[index] = resume;
            size++;
        }
    }

    @Override
    public Resume get(String uuid) {
        int index = findIndexOnUuid(uuid);
        if (index < 0) throw new StorageResumeNotFoundException(uuid);
        return storage[index];
    }

    @Override
    public void delete(String uuid) {
        int index = findIndexOnUuid(uuid);
        if (index < 0) throw new StorageResumeNotFoundException(uuid);
        System.arraycopy(storage, index + 1, storage, index, storage.length - index - 1);
        storage[size--] = null;
    }

    @Override
    protected int findIndexOnUuid(String uuid) {
        return Arrays.binarySearch(storage, 0, size, new Resume(uuid));
//        return Arrays.binarySearch(Arrays
//                .stream(Arrays.copyOf(storage, size))
//                .map(Resume::getUuid)
//                .toArray(), uuid);
    }

    @Override
    public String toString() {
        return "SortedArrayStorage{" +
                "size=" + size +
                ", storage=" + Arrays.toString(storage) +
                '}';
    }
}
