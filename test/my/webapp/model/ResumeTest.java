package my.webapp.model;

import my.webapp.util.DateUtil;
import org.junit.Assert;
import org.junit.Test;

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

        R1 = new Resume(R1Uuid, R1Uuid);
        R2 = new Resume(R2Uuid, R2Uuid);
        R3 = new Resume(R3Uuid, R3Uuid);
        R4 = new Resume("1", "1");
    }

    @Test
    public void getUuid() {
        Assert.assertEquals(R1Uuid, R1.getUuid());
        Assert.assertEquals(R2Uuid, R2.getUuid());
        Assert.assertEquals(R3Uuid, R3.getUuid());
        Assert.assertEquals("1", R4.getUuid());

    }

    @Test
    public void setUuid() {
        R4.setUuid("abcd000");
        Assert.assertEquals(R4.getUuid(), "abcd000");
    }

    @Test
    public void testDummyResume(){
        Resume R1 = new Resume();
        R1.setContact(ContactType.MOBILE, "+321654987");
        R1.setContact(ContactType.HOME_PHONE, "+321654987");
        R1.setContact(ContactType.PHONE, "+852369741");

        R1.setSection(SectionType.EXPERIENCE,
            new OrganizationSection(
                new Organization("MKS", null,
                    new Organization.Position("09/2000", "07/2001", "Сборщик",

                            "Крутил компы."),
                    new Organization.Position("07/2001",
                            "01/2002", "Наладчик", "Включал/выключал компы."
                    ),
                    new Organization.Position("01/2002",
                            "10/2002", "Инженер ТАЛ", "Крутил/включал/выключал сервера"
                    )
                ),
                new Organization("ФОП", null,
                    new Organization.Position("10/2002",
                            "10/2003", "Разгильдяй", ""
                    )
                )
            )
        );
        R1.setSection(SectionType.QUALIFICATIONS,
            new ListSection(
                    "Могу копать.",
                    "Могу не копать.",
                    "Могу спать и есть."
            ));
        Assert.assertEquals("+321654987", R1.getContact(ContactType.MOBILE));
        Assert.assertEquals("+321654987", R1.getContact(ContactType.HOME_PHONE));
        Assert.assertEquals(DateUtil.parse("10/2002"), ((OrganizationSection)R1
                .getSection(SectionType.EXPERIENCE)).getOrganizations().get(0)
                .getPositions().get(2).getEndDate());

    }
}