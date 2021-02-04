package my.webapp.model;

import my.webapp.Config;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResumeTest {
    public static final Resume R1;
    public static final Resume R2;
    public static final Resume R3;
    public static final Resume R4;

    public static final String R2Uuid;
    public static final String R3Uuid;

    public static final List<Resume> RESUMES =
            Resume.generateNFakeResumes(Config.get().getArrayCapacity());

    static {
        R2Uuid = "b";
        R3Uuid = "c";

        R1 = Resume.generateNFakeResumes(1).get(0);
        R2 = new Resume(R2Uuid, R2Uuid);
        R3 = new Resume(R3Uuid, R3Uuid);
        R4 = new Resume("d", "d");
    }

    @Test
    public void getUuid() {
        assertEquals(R2Uuid, R2.getUuid());
        assertEquals(R3Uuid, R3.getUuid());
        assertEquals("d", R4.getUuid());
    }

    @Test
    public void setUuid() {
        R4.setUuid("abcd000");
        assertEquals("abcd000", R4.getUuid());
    }
}