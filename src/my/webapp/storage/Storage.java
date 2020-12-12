package my.webapp.storage;

import my.webapp.exception.ArrayStorageOverflowException;
import my.webapp.model.Resume;

import java.util.Arrays;

public interface Storage {

    void save(Resume resume);

    void update(Resume resume);

    /**
     * @return Resume with provided uuid from storage
     */
    Resume get(String uuid);

    void delete(String uuid);

    int size();

    void clear();

    Resume[] getAll();

    Resume[] getAllToPosition(int pos);

}
