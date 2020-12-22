package my.webapp.storage.serializer;

import my.webapp.exception.StorageException;
import my.webapp.model.Resume;

import java.io.*;

public class ObjectStreamSerializer implements ResumeSerializer{
    @Override
    public void saveResume(Resume r, OutputStream os) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(r);
    }

    @Override
    public Resume loadResume(InputStream is) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(is)) {
            return (Resume) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new StorageException("Error read resume!", e);
        }
    }
}
