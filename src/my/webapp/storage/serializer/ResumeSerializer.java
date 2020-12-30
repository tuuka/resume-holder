package my.webapp.storage.serializer;

import my.webapp.model.Resume;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class ResumeSerializer {
    protected String fileSuffix = ".res";

    public String getFileSuffix(){ return fileSuffix; }

    public abstract void saveResume(Resume r, OutputStream os) throws IOException;

    public abstract Resume loadResume(InputStream is) throws IOException;
}
