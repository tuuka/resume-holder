package my.webapp.model;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Resume implements Comparable<Resume>, Serializable {
    private static final long serialVersionUID = 1L;
    private static int count;
    private String uuid;
    private String fullName;

//    @JsonDeserialize(keyAs = ContactType.class, contentAs = String.class)
    private final Map<ContactType, String> contacts = new EnumMap<>(ContactType.class);

//    @JsonDeserialize(keyAs = SectionType.class, contentAs = Section.class)
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


    public Map<ContactType, String> getContacts() {
        return contacts;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getContact(ContactType type){
        return contacts.get(type);
    }

    public Section getSection(SectionType type) {
        return sections.get(type);
    }

    public void setContact(ContactType type, String value){
        contacts.put(type, value);
    }

    public void setSection(SectionType type, Section section){
        sections.put(type, section);
    }

    @Override
    public String toString() {
        return String.format("Resume : uuid='%s',\tname: '%s'" +
                "\n\tcontacts:%s\n\tsections:%s",
                uuid, fullName,
                contacts.keySet().stream()
                                        .collect(StringBuilder::new,
                                                (sb,item)-> sb.append("\n\t\t").append(item.returnContact(contacts.get(item))).append(";"),
                                                StringBuilder::append).toString(),
                sections.keySet().stream()
                        .collect(StringBuilder::new,
                                (sb,item)-> sb.append("\n\t\t").append(item.getTitle()).append(":").append(sections.get(item)),
                                StringBuilder::append).toString()

                );
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
}
