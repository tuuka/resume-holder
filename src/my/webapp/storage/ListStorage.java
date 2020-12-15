package my.webapp.storage;

import my.webapp.exception.StorageResumeExistsException;
import my.webapp.exception.StorageResumeNotFoundException;
import my.webapp.model.Resume;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListStorage implements Storage{
    private final List<Resume> storage;


    public ListStorage() {
        storage = new ArrayList<>();
    }

    @Override
    public void save(Resume resume) {
        if (!storage.contains(resume)) storage.add(resume);
            else throw new StorageResumeExistsException(resume.getUuid());
    }

    private int getIndexOnUuid(String uuid){
        for ( int i = 0; i < storage.size(); i++ )
            if (storage.get(i).getUuid().equals(uuid)) return i;
        return -1;
    }

    @Override
    public void update(Resume resume) {
        /* indexOf использует equals для элементов, что (в будущем верятно)
           не будет работать, если элементы будут включать много разных полей
           потому ищем индекс по uuid*/
        int index = getIndexOnUuid(resume.getUuid());
        if (index >= 0) storage.set(index, resume);
            else throw new StorageResumeNotFoundException(resume.getUuid());
    }

    @Override
    public Resume get(String uuid) {
        int index = getIndexOnUuid(uuid);
        if (index == -1) throw new StorageResumeNotFoundException(uuid);
        return storage.get(index);
    }

    @Override
    public void delete(String uuid) {
        if (!storage.removeIf(r->r.getUuid().equals(uuid)))
            throw new StorageResumeNotFoundException(uuid);
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
        return storage.toArray(new Resume[0]);
    }

    @Override
    public Resume[] getAllToPosition(int pos) {
        return storage.subList(0, pos).toArray(new Resume[0]);
    }

    @Override
    public String toString() {
        return "ListStorage{" +
                "size=" + storage.size() +
                ", storage=" + Arrays.toString(getAll()) +
                '}';
    }
}