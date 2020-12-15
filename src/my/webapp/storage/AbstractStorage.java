package my.webapp.storage;

import my.webapp.exception.StorageResumeExistsException;
import my.webapp.exception.StorageResumeNotFoundException;
import my.webapp.model.Resume;

import java.util.Arrays;


/* Абстрактный класс для всех хранилищ. K - ключ, используемый для поиска
* Имплементирует все методы интерфейса Storage, используя дополнительные
* абстрактные методы, реализация которых будет осуществлена в
* дочерних классах. */

public abstract class AbstractStorage<K> implements Storage{

    protected abstract void doSave(Resume r, K key);

    protected abstract void doUpdate(Resume r, K key);

    protected abstract Resume doGet(K key);

    protected abstract void doDelete(K key);

    protected abstract int doSize();

    protected abstract void doClear();

    protected abstract K getSearchKey(String uuid);

    protected abstract boolean isExist(String uuid);

    protected abstract Resume[] doGetAll();

    @Override
    public void save(Resume r) {
        String uuid = r.getUuid();
        if (isExist(uuid))
            throw new StorageResumeExistsException(uuid);
        doSave(r, getSearchKey(uuid));
    }

    @Override
    public void update(Resume r) {
        String uuid = r.getUuid();
        if (!isExist(uuid))
            throw new StorageResumeNotFoundException(uuid);
        doUpdate(r, getSearchKey(uuid));
    }

    @Override
    public Resume get(String uuid) {
        if (!isExist(uuid))
            throw new StorageResumeNotFoundException(uuid);
        return doGet(getSearchKey(uuid));
    }

    @Override
    public void delete(String uuid) {
        if (!isExist(uuid))
            throw new StorageResumeNotFoundException(uuid);
        doDelete(getSearchKey(uuid));
    }

    @Override
    public int size() {
        return doSize();
    }

    @Override
    public void clear() {
        doClear();
    }

    @Override
    public Resume[] getAll() {
        return doGetAll();
    }

    @Override
    public Resume[] getAllToPosition(int pos) {
        if (pos > size()) pos = size();
        return Arrays.copyOfRange(getAll(), 0, pos);
    }
}
