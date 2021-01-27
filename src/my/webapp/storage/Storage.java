package my.webapp.storage;

import my.webapp.model.Resume;
import my.webapp.util.LoggerFactory;

import java.util.List;
import java.util.logging.Logger;

public interface Storage {

    Logger LOGGER = LoggerFactory.getLogger(Storage.class);

    void save(Resume resume);

    void update(Resume resume);

    /**
     * @return Resume with provided uuid from storage
     */
    Resume get(String uuid);

    void delete(String uuid);

    int size();

    void clear();

    List<Resume> getAllSorted();

}
