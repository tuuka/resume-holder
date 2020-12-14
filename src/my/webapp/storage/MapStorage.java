package my.webapp.storage;

import my.webapp.exception.StorageResumeExistsException;
import my.webapp.exception.StorageResumeNotFoundException;
import my.webapp.model.Resume;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MapStorage implements Storage{
    private final Map<String, Resume> storage;

    public MapStorage() {
        storage = new HashMap<>();
    }

    @Override
    public void save(Resume resume) {
        if (!storage.containsKey(resume.getUuid()))
            storage.put(resume.getUuid(), resume);
        else throw new StorageResumeExistsException(resume.getUuid());
    }

    @Override
    public void update(Resume resume) {
        if (storage.containsKey(resume.getUuid()))
            storage.put(resume.getUuid(), resume);
        else throw new StorageResumeNotFoundException(resume.getUuid());
    }

    @Override
    public Resume get(String uuid) {
        Resume r = storage.get(uuid);
        if (r != null) return r;
        throw new StorageResumeNotFoundException(uuid);
    }

    @Override
    public void delete(String uuid) {
        Resume r = storage.remove(uuid);
        if (r == null) throw new StorageResumeNotFoundException(uuid);
    }

    @Override
    public int size() {
        return storage.size();
    }

    @Override
    public void clear() {
        storage.clear();
    }

    @Override
    public Resume[] getAll() {
        return storage.values().toArray(new Resume[0]);
    }

    @Override
    public Resume[] getAllToPosition(int pos) {
        return Arrays.copyOfRange(getAll(), 0, pos);
    }

    @Override
    public String toString() {
        return "MapStorage{" +
                "size=" + storage.size() +
                ", storage=" + Arrays.toString(getAll()) +
                '}';
    }
}
