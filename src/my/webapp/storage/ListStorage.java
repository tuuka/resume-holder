package my.webapp.storage;

import my.webapp.model.Resume;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListStorage extends AbstractStorage<Integer>{
    private final List<Resume> storage;

    public ListStorage() {
        storage = new ArrayList<>();
    }


    @Override
    protected void doSave(Resume r, Integer key) {
        storage.add(r);
    }

    @Override
    protected void doUpdate(Resume r, Integer key) {
        storage.set(key, r);
    }

    @Override
    protected Resume doGet(Integer key) {
        return storage.get(key);
    }

    @Override
    protected void doDelete(Integer key) {
        storage.remove(key.intValue());
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
    protected Integer getSearchKey(String uuid) {
        for ( int i = 0; i < storage.size(); i++ )
            if (storage.get(i).getUuid().equals(uuid)) return i;
        return -1;
    }

    @Override
    protected boolean isExist(Integer index) {
        return index >= 0;
    }

    @Override
    protected Resume[] doGetAll() {
        return storage.toArray(new Resume[0]);
    }

    @Override
    public String toString() {
        return "ListStorage{" +
                "size=" + storage.size() +
                ", storage=" + Arrays.toString(getAll()) +
                '}';
    }
}
