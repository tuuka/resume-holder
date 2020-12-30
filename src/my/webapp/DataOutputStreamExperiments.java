package my.webapp;

import my.webapp.model.Resume;
import my.webapp.storage.serializer.DataStreamSerializerEnhanced;

import java.io.*;
import java.util.Objects;

public class DataOutputStreamExperiments {
    public static void main(String[] args) {
        Resume r = MainJacksonExperiments.generateResume();

        DataStreamSerializerEnhanced dss = new DataStreamSerializerEnhanced();
        try (OutputStream os = new FileOutputStream(new File("D:\\temp\\r.dat"))){
            dss.saveResume(r, os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Resume r_r = null;
        try (InputStream is = new FileInputStream(new File("D:\\temp\\r.dat"))){
            r_r = dss.loadResume(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(Objects.equals(r_r, r));
    }
}
