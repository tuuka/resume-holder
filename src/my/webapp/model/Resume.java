package my.webapp.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Resume implements Comparable<Resume>, Serializable {
    private static final long serialVersionUID = 1L;
    private static int count;
    private String uuid;
    private String fullName;

    private final Map<ContactType, String> contacts = new EnumMap<>(ContactType.class);

    private final Map<SectionType, Section> sections = new EnumMap<>(SectionType.class);

    public static final Resume EMPTY = new Resume();

    static {
        EMPTY.setSection(SectionType.OBJECTIVE, TextSection.EMPTY);
        EMPTY.setSection(SectionType.PERSONAL, TextSection.EMPTY);
        EMPTY.setSection(SectionType.ACHIEVEMENT, ListSection.EMPTY);
        EMPTY.setSection(SectionType.QUALIFICATIONS, ListSection.EMPTY);
        EMPTY.setSection(SectionType.EXPERIENCE, new OrganizationSection(Organization.EMPTY));
        EMPTY.setSection(SectionType.EDUCATION, new OrganizationSection(Organization.EMPTY));
    }

    public Resume(){ this("John Doe-" + (count + 1)); }

    public Resume(String fullName){
        this(UUID.randomUUID().toString(), fullName);
    }

    public Resume(String uuid, String fullName) {
        Objects.requireNonNull(uuid, "Uuid cannot be null!");
        Objects.requireNonNull(fullName, "Full name cannot be null!");
        this.uuid = uuid;
        this.fullName = fullName;
        count++;
    }

    public String getUuid() { return uuid; }

    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getFullName() { return fullName; }

    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getContact(ContactType type){ return contacts.get(type); }

    public Section getSection(SectionType type) { return sections.get(type); }

    public void setContact(ContactType type, String value){
        contacts.put(type, value);
    }

    public void setSection(SectionType type, Section section){
        sections.put(type, section);
    }

    public Map<ContactType, String> getContacts() { return contacts; }

    public Map<SectionType, Section> getSections() { return sections; }

    @Override
    public String toString() {
        return "Resume{" +
                "uuid='" + uuid + '\'' +
                ", fullName='" + fullName + '\'' +
                ", contacts=" + contacts +
                ", sections=" + sections +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resume resume = (Resume) o;
        return Objects.equals(uuid, resume.uuid) &&
                Objects.equals(fullName, resume.fullName) &&
                Objects.equals(contacts, resume.contacts) &&
                Objects.equals(sections, resume.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, fullName, contacts, sections);
    }

    @Override
    public int compareTo(Resume o) {
        int fullNameComp = this.getFullName().compareTo(o.getFullName());
        return fullNameComp !=0? fullNameComp:
            this.getUuid().compareTo(o.getUuid());
    }

    // made to maintain identity when serializing and deserializing
    public void sort(){
        for (Map.Entry<SectionType, Section> entry : this.getSections().entrySet()){
            if (entry.getValue() instanceof ListSection)
                Collections.sort(((ListSection) entry.getValue()).getItems());
            if (entry.getValue() instanceof OrganizationSection) {
                OrganizationSection os = (OrganizationSection)entry.getValue();
                os.getOrganizations().sort(Comparator.comparing(o -> o.getHomePage().getName()));
                os.getOrganizations().forEach(o->
                    o.getPositions().sort(Comparator.comparing(Organization.Position::getTitle))
                );
            }
        }
    }

    public static List<Resume> generateNFakeResumes(int n){
        Random random = new Random();
        List<Resume> resumeList = new ArrayList<>();
        List<String> ls;
        for (int i = 0; i < n; i++){
            Resume r = new Resume(String.format("uuid%d", i), String.format("name%d", i));
            for (ContactType ct : ContactType.values())
                r.setContact(ct, String.format("+%d", random.nextInt(1000000000)));
            for (SectionType st : SectionType.values())
                switch (st){
                    case PERSONAL:
                        r.setSection(st, new TextSection("PERSONAL of R uuid" + i));
                        break;
                    case OBJECTIVE:
                        r.setSection(st, new TextSection("OBJECTIVE of R uuid" + i));
                        break;
                    case QUALIFICATIONS:
                        ls = new ArrayList<>();
                        for (int q = 0; q < 1+random.nextInt(5); q++)
                            ls.add(String.format("QUALIFICATION%d of R uuid%d", q, i));
                        r.setSection(st, new ListSection(ls));
                        break;
                    case ACHIEVEMENT:
                        ls = new ArrayList<>();
                        for (int q = 0; q < 1+random.nextInt(5); q++)
                            ls.add(String.format("ACHIEVEMENT%d of R uuid%d", q, i));
                        r.setSection(st, new ListSection(ls));
                        break;
                    case EDUCATION:
                        r.setSection(st, generateOrgSection("EDU_", i, random));
                        break;
                    case EXPERIENCE:
                        r.setSection(st, generateOrgSection("EXP_", i, random));
                        break;
                }
            resumeList.add(r);
        }
        return resumeList;
    }

    private static Section generateOrgSection(String s, int i, Random random){
        List<Organization> lo;
        List<Organization.Position> lp;
        lo = new ArrayList<>();
        lp = new ArrayList<>();
        for (int o = 0; o < 1+random.nextInt(5); o++){
            LocalDate ld  = LocalDate.ofYearDay(1990+random.nextInt(20), random.nextInt(364)+1);
            for (int p = 0; p < 1+random.nextInt(3); p++)
                lp.add(new Organization.Position(
                        LocalDate.ofEpochDay(ld.toEpochDay()+(p+o)*10),
                        LocalDate.ofEpochDay(ld.toEpochDay()+(p+o)*20),
                        String.format("Position%d at %sOrganization%d%d", p, s, i, o),
                        String.format("PosDescr%d at %sOrganization%d%d", p, s, i, o)));

            lo.add(new Organization(
                    new Link(String.format("%sOrganization%d%d", s, i, o),
                            String.format("www.%sorganization%d%d.com", s, i, o)),
                    lp));
        }
        return new OrganizationSection(lo);
    }
}
