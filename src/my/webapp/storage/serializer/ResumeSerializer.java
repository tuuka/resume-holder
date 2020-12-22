package my.webapp.storage.serializer;

import my.webapp.model.Resume;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ResumeSerializer {
    void saveResume(Resume r, OutputStream os) throws IOException;

    Resume loadResume(InputStream is) throws IOException;
}
