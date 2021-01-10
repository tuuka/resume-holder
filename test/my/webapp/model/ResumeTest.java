package my.webapp.model;

import my.webapp.util.DateUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResumeTest {
    public static final Resume R1;
    public static final Resume R2;
    public static final Resume R3;
    public static final Resume R4;

    public static final String R2Uuid;
    public static final String R3Uuid;

    static {
        R2Uuid = "b";
        R3Uuid = "c";

        R1 = new Resume("dummy.dummy", "dummy");
        R1.setContact(ContactType.MOBILE, "+123456789");
        R1.setContact(ContactType.PHONE, "+987654321");
        Organization.Position p1 = new Organization.Position("11/2000", "12/2000", "worker1", "dummy worker 1");
        Organization.Position p2 = new Organization.Position("12/2001", "01/2002", "worker2", "dummy worker 2");
        Organization.Position p3 = new Organization.Position("02/2003", "12/2003", "worker3", "dummy worker 3");
        Organization.Position p4 = new Organization.Position("05/2005", "06/2006", "worker4", "dummy worker 4");
        Organization o1 = new Organization("Dummy company1", "dummy.company1@gmail.com", p1, p2);
        Organization o2 = new Organization("Dummy company2", "dummy.company2@gmail.com", p3);
        Organization o3 = new Organization("Dummy company3", "dummy.company3@gmail.com", p4);
        R1.setSection(SectionType.EXPERIENCE, new OrganizationSection(o1, o2, o3));
        R1.setSection(SectionType.QUALIFICATIONS, new ListSection("Can dig", "Can to not dig", "Can sleep"));
        R1.setSection(SectionType.ACHIEVEMENT, new ListSection("Know how to dig", "Know how to not dig", "Know how to sleep"));
        R1.setSection(SectionType.EDUCATION, new OrganizationSection(
                new Organization("Dummy company4", "dummy.company4@gmail.com",
                        new Organization.Position("01/1999", "10/1999", "student", "had been learning how to dig"))));
        R1.setSection(SectionType.PERSONAL, new TextSection("Very good person that can work hard in area of digging."));
        R1.setSection(SectionType.OBJECTIVE, new TextSection("Experienced digger"));

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
        assertEquals(R4.getUuid(), "abcd000");
    }

    @Test
    public void testDummyResume() {
        assertEquals("+123456789", R1.getContact(ContactType.MOBILE));
        assertEquals("+987654321", R1.getContact(ContactType.PHONE));
        assertEquals(DateUtil.parse("01/2002"), ((OrganizationSection) R1
                .getSection(SectionType.EXPERIENCE)).getOrganizations().get(0)
                .getPositions().get(1).getEndDate());

    }
}