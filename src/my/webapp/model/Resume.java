package my.webapp.model;

import com.github.javafaker.Faker;

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
    private String uuid;
    private String fullName;

    private final Map<ContactType, String> contacts = new EnumMap<>(ContactType.class);
    private final Map<SectionType, Section> sections = new EnumMap<>(SectionType.class);

//    public static final Resume EMPTY = new Resume();
//
//    static {
//        EMPTY.setSection(SectionType.OBJECTIVE, TextSection.EMPTY);
//        EMPTY.setSection(SectionType.PERSONAL, TextSection.EMPTY);
//        EMPTY.setSection(SectionType.ACHIEVEMENT, ListSection.EMPTY);
//        EMPTY.setSection(SectionType.QUALIFICATIONS, ListSection.EMPTY);
//        EMPTY.setSection(SectionType.EXPERIENCE, new OrganizationSection(Organization.EMPTY));
//        EMPTY.setSection(SectionType.EDUCATION, new OrganizationSection(Organization.EMPTY));
//    }

    public Resume(){ this(generateNFakeResumes(1).get(0).getFullName()); }

    public Resume(String fullName){
        this(UUID.randomUUID().toString(), fullName);
    }

    public Resume(String uuid, String fullName) {
        Objects.requireNonNull(uuid, "Uuid cannot be null!");
        Objects.requireNonNull(fullName, "Full name cannot be null!");
        this.uuid = uuid;
        this.fullName = fullName;
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

    public Resume sort(){
        for (Map.Entry<SectionType, Section> entry : this.getSections().entrySet()){
            if (entry.getValue() instanceof ListSection)
                Collections.sort(((ListSection) entry.getValue()).getItems());
            if (entry.getValue() instanceof OrganizationSection) {
                OrganizationSection os = (OrganizationSection)entry.getValue();
                os.getOrganizations().sort(Organization::compareTo);
                os.getOrganizations().forEach(o -> o.getPositions().sort(Organization.Position::compareTo));
            }
        }
        return this;
    }

    public static List<Resume> generateNFakeResumes(int n){
//        Faker faker = new Faker(new Locale("ru"));
        Faker faker = new Faker();
        Random random = new Random();
        List<Resume> resumeList = new ArrayList<>();
        List<String> ls;
        for (int i = 0; i < n; i++){
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            Resume r = new Resume(UUID.randomUUID().toString(), firstName + " " + lastName);
            for (ContactType ct : ContactType.values()){
                switch (ct) {
                    case MOBILE:
                    case PHONE:
                        r.setContact(ct, faker.phoneNumber().cellPhone());
                        break;
                    case SKYPE:
                        r.setContact(ct, faker.name().username());
                        break;
                    case MAIL:
                        r.setContact(ct, faker.internet().emailAddress());
                        break;
                    case GITHUB:
                        r.setContact(ct, String.format("http://www.github.com/%s", lastName));
                        break;
                    case HOME_PAGE:
                        r.setContact(ct, "http://" + faker.internet().url());
                        break;
                }}
            for (SectionType st : SectionType.values())
                switch (st){
                    case PERSONAL:
                        r.setSection(st, new TextSection(faker.chuckNorris()
                                .fact().replaceAll("\"", "'")));
                        break;
                    case OBJECTIVE:
                        r.setSection(st, new TextSection(faker.job().title()));
                        break;
                    case QUALIFICATIONS:
                    case ACHIEVEMENT:
                        ls = new ArrayList<>();
                        for (int q = 0; q < 1+random.nextInt(5); q++)
                            ls.add(faker.lorem().sentence(3));
                        r.setSection(st, new ListSection(ls));
                        break;
                    case EDUCATION:
                    case EXPERIENCE:
                        r.setSection(st, generateOrgSection(random, faker, st));
                        break;
                }
            resumeList.add(r.sort());
        }
        Collections.sort(resumeList);
        return resumeList;
    }

    private static Section generateOrgSection(Random random, Faker faker, SectionType sectionType){
        List<Organization> lo;
        List<Organization.Position> lp;
        lo = new ArrayList<>();
        long currentDay  = LocalDate.ofYearDay(1990+random.nextInt(20),
                random.nextInt(364)+1).toEpochDay();
        for (int o = 0; o < 1+random.nextInt(4); o++){
            lp = new ArrayList<>();
            for (int p = 0; p < 1+random.nextInt(3); p++)
                lp.add(new Organization.Position(
                        LocalDate.ofEpochDay(currentDay),
                        LocalDate.ofEpochDay(currentDay += 120),
                        sectionType.equals(SectionType.EDUCATION)
                        ? "Student" : faker.job().position(),
                        faker.job().title()));
            lo.add(new Organization(
                    new Link(sectionType.equals(SectionType.EDUCATION)
                            ? faker.university().name()
                            : faker.company().name(),
                            "http://" +faker.company().url()), lp));
        }
        return new OrganizationSection(lo);
    }
}
