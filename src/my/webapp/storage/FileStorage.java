package my.webapp.storage;

import my.webapp.exception.StorageException;
import my.webapp.model.Resume;
import my.webapp.storage.serializer.ObjectStreamSerializer;
import my.webapp.storage.serializer.ResumeSerializer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileStorage extends AbstractStorage<File> {
    private final File directory;
    // Паттерн "Стратегия" - вставляем объект-стратегию поведения сериализации
    private final ResumeSerializer serializer;

    public FileStorage(String directory, ResumeSerializer serializer) {
        this(new File(directory), serializer);
    }

    public FileStorage(ResumeSerializer serializer) {
        this(new File("fileStorage"), serializer);
    }

    public FileStorage(File directory, ResumeSerializer serializer) {
        Objects.requireNonNull(directory, "Directory must not be null!");
        if (!directory.exists() && !directory.mkdirs())
            throw new StorageException("Can not find or create directory " +
                    directory.getAbsoluteFile());
        if (!directory.isDirectory())
            throw new IllegalArgumentException(
                    directory.getAbsolutePath() + " is not a directory!");
        if (!directory.canRead() || !directory.canRead())
            throw new IllegalArgumentException(
                    directory.getAbsolutePath() + " is not readable/writable!");
        this.directory = directory;
        this.serializer = serializer == null ? new ObjectStreamSerializer()
                : serializer;
    }

    @Override
    protected void doSave(Resume r, File file) {
        try {
            boolean a = file.createNewFile();
        } catch (IOException e) {
            throw new StorageException("Error creating file "
                    + file.getAbsoluteFile(), e);
        }
        doUpdate(r, file);
    }

    @Override
    protected void doUpdate(Resume r, File file) {
        try (BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(file))) {
            serializer.saveResume(r, bos);
        } catch (FileNotFoundException e) {
            throw new StorageException("Cannot find Resume file " + file, e);
        } catch (IOException e) {
            throw new StorageException("Error saving Resume to file " + file, e);
        }
    }

    @Override
    protected Resume doGet(File file) {
        Resume r;
        try (BufferedInputStream bos = new BufferedInputStream(
                new FileInputStream(file))) {
            r = serializer.loadResume(bos);
        } catch (FileNotFoundException e) {
            throw new StorageException("Cannot find Resume file " + file, e);
        } catch (IOException e) {
            throw new StorageException("Error reading Resume from file " + file, e);
        }
        return r;
    }

    @Override
    protected void doDelete(File file) {
        if (!file.delete()) throw
                new StorageException("Error deleting file " + file.getAbsoluteFile());
    }

    @Override
    protected int doSize() {
        return directory.listFiles().length;
    }

    @Override
    protected void doClear() {
        for (File f : directory.listFiles())
            if (!f.delete()) throw
                    new StorageException("Can not delete file " + f);
    }

    @Override
    protected File getSearchKey(String uuid) {
        return new File(directory, uuid + serializer.getFileSuffix());
    }

    @Override
    protected boolean isExist(File file) {
        return file.exists();
    }

    @Override
    protected Resume[] doGetAll() {
        List<Resume> resumes = new ArrayList<>();
        for (File f : directory.listFiles())
            resumes.add(doGet(f));
        return resumes.toArray(new Resume[0]);
    }
}
