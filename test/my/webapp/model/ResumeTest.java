package my.webapp.model;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ResumeTest {
    public static final Resume R1;
    public static final Resume R2;
    public static final Resume R3;
    public static final Resume R4;

    public static final String R1Uuid;
    public static final String R2Uuid;
    public static final String R3Uuid;


    static {
        R1Uuid = "1.000";
        R2Uuid = "2.000";
        R3Uuid = "3.000";

        R1 = new Resume(R1Uuid);
        R2 = new Resume(R2Uuid);
        R3 = new Resume(R3Uuid);
        R4 = new Resume();
    }

    @Test
    public void getUuid() {
        Assert.assertEquals(R1.getUuid(), R1Uuid);
        Assert.assertEquals(R2.getUuid(), R2Uuid);
        Assert.assertEquals(R3.getUuid(), R3Uuid);
        Assert.assertEquals(R4.getUuid(), "4.000");

    }

    @Test
    public void setUuid() {
        R4.setUuid("abcd000");
        Assert.assertEquals(R4.getUuid(), "abcd000");
    }
}