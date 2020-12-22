package my.webapp.storage;

import my.webapp.model.Resume;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MapUuidStorage extends AbstractStorage<String>{
    private final Map<String, Resume> storage;

    public MapUuidStorage() {
        storage = new HashMap<>();
    }

    @Override
    protected void doSave(Resume r, String uuid) {
        storage.put(uuid, r);
    }

    @Override
    protected void doUpdate(Resume r, String uuid) {
        storage.put(uuid, r);
    }

    @Override
    protected Resume doGet(String uuid) {
        return storage.get(uuid);
    }

    @Override
    protected void doDelete(String uuid) {
        storage.remove(uuid);
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
    protected String getSearchKey(String uuid) {
        return uuid;
    }

    @Override
    protected boolean isExist(String uuid) {
        return storage.containsKey(uuid);
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
