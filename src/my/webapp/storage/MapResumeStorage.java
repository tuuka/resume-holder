package my.webapp.storage;

import my.webapp.model.Resume;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MapResumeStorage extends AbstractStorage<Resume>{
    private final Map<String, Resume> storage;

    public MapResumeStorage() {
        storage = new HashMap<>();
    }

    @Override
    protected void doSave(Resume r, Resume key) {
        storage.put(key.getUuid(), r);
    }

    @Override
    protected void doUpdate(Resume r, Resume key) {
        storage.put(key.getUuid(), r);
    }

    @Override
    protected Resume doGet(Resume key) {
        return storage.get(key.getUuid());
    }

    @Override
    protected void doDelete(Resume key) {
        storage.remove(key.getUuid());
    }

    @Override
    protected int doSize() {
        return storage.size();
    }

    @Override
    protected void doClear() {
        storage.clear();
    }

    @Override
    protected Resume getSearchKey(String uuid) {
        return storage.get(uuid);
    }

    @Override
    protected boolean isExist(Resume key) {
        return storage.containsKey(key.getUuid());
    }

    @Override
    protected Resume[] doGetAll() {
        return storage.values().toArray(new Resume[0]);
    }

    @Override
    public String toString() {
        return "MapStorage{" +
                "size=" + storage.size() +
                ", storage=" + Arrays.toString(getAll()) +
                '}';
    }
}
