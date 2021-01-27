package my.webapp.storage;

import my.webapp.Config;
import my.webapp.exception.StorageException;
import my.webapp.model.Resume;
import my.webapp.storage.serializer.ObjectStreamSerializer;
import my.webapp.storage.serializer.ResumeSerializer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathStorage extends AbstractStorage<Path> {
    private final Path directory;
    private final ResumeSerializer serializer;

    public PathStorage(String directory, ResumeSerializer serializer) {
        this(Path.of(directory), serializer);
    }

    public PathStorage(ResumeSerializer serializer) {
        this(Path.of(Config.get().getStorageDir()), serializer);
    }

    public PathStorage(Path directory, ResumeSerializer serializer) {
        Objects.requireNonNull(directory, "Directory must not be null!");
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new StorageException("Can not find or create directory " +
                        directory.toAbsolutePath());
            }
        }
        if (!Files.isDirectory(directory))
            throw new IllegalArgumentException(
                    directory.toAbsolutePath() + " is not a directory!");
        if (!Files.isReadable(directory) || !Files.isWritable(directory))
            throw new IllegalArgumentException(
                    directory.toAbsolutePath() + " is not readable/writable!");
        this.directory = directory;
        this.serializer = serializer == null ? new ObjectStreamSerializer()
                : serializer;
    }

    @Override
    protected void doSave(Resume r, Path path) {
        try {
            Files.createFile(path);
        } catch (IOException e) {
            throw new StorageException("Error creating file "
                    + path.toAbsolutePath(), e);
        }
        doUpdate(r, path);
    }

    @Override
    protected void doUpdate(Resume r, Path path) {
        try (BufferedOutputStream bos = new BufferedOutputStream(
                Files.newOutputStream(path))) {
            serializer.saveResume(r, bos);
        } catch (IOException e) {
            throw new StorageException("Error saving Resume to file " +
                    path.toAbsolutePath(), e);
        }
    }

    @Override
    protected Resume doGet(Path path) {
        Resume r;
        try (BufferedInputStream bos = new BufferedInputStream(
                Files.newInputStream(path))) {
            r = serializer.loadResume(bos);
        } catch (IOException e) {
            throw new StorageException("Error reading Resume from file " +
                    path.toAbsolutePath(), e);
        }
        return r;
    }

    @Override
    protected void doDelete(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new StorageException("Error deleting file " +
                    path.toAbsolutePath());
        }
    }

    @Override
    protected int doSize() {
        return (int) getFilesList().count();
    }

    @Override
    protected void doClear() {
        getFilesList().forEach(this::doDelete);
    }


    @Override
    protected Path getSearchKey(String uuid) {
        return directory.resolve(uuid + serializer.getFileSuffix());
    }

    @Override
    protected boolean isExist(Path path) {
        return Files.isRegularFile(path);
    }

    @Override
    protected List<Resume> doGetAll() {
        return getFilesList().map(this::doGet).collect(Collectors.toList());
    }

    private Stream<Path> getFilesList() {
        try {
            return Files.list(directory);
        } catch (IOException e) {
            throw new StorageException("Error reading directory " +
                    directory.toAbsolutePath(), e);
        }
    }

    @Override
    public String toString() {
        return "PathStorage{" +
                "storage=" + doGetAll() +
                '}';
    }
}
