package my.webapp.storage.serializer;

import my.webapp.exception.StorageException;
import my.webapp.model.*;
import my.webapp.util.DateUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataStreamSerializer extends ResumeSerializer {
    private final String resumeBegin = "<resume>";
    private final String resumeEnd = "</resume>";
    private final String itemsBegin = "<items>";
    private final String itemsEnd = "</items>";
    private final String organizationsBegin = "<organizarions>";
    private final String organizationsEnd = "</organizarions>";
    private final String positionsBegin = "<positions>";
    private final String positionsEnd = "</positions>";


    public DataStreamSerializer() {
        this(".dat");
    }

    public DataStreamSerializer(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    @Override
    public void saveResume(Resume r, OutputStream os) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(os)) {
            dos.writeUTF(resumeBegin);
            dos.writeUTF(r.getUuid());
            dos.writeUTF(r.getFullName());
            for (ContactType ct : ContactType.values()) {
                dos.writeUTF(ct.name());
                String ctx = r.getContact(ct);
                dos.writeUTF(ctx==null? "": ctx);
            }
            for (SectionType st : SectionType.values()) {
                dos.writeUTF(st.name());
                Section section = r.getSection(st);
                if (section == null) {dos.writeUTF(""); continue;}
                if (section instanceof TextSection)
                    dos.writeUTF(((TextSection) section).getContent());
                if (section instanceof ListSection) {
                    dos.writeUTF(itemsBegin);
                    for (String item : (((ListSection) section).getItems()))
                        dos.writeUTF(item);
                    dos.writeUTF(itemsEnd);
                }
                if (section instanceof OrganizationSection) {
                    dos.writeUTF(organizationsBegin);
                    for (Organization organization :
                            (((OrganizationSection) section)
                                    .getOrganizations())) {
                        dos.writeUTF(organization.getHomePage().getName());
                        dos.writeUTF(organization.getHomePage().getUrl());
                        dos.writeUTF(positionsBegin);
                        for (Organization.Position position :
                                organization.getPositions()) {
                            dos.writeUTF(DateUtil.format(position.getStartDate()));
                            dos.writeUTF(DateUtil.format(position.getEndDate()));
                            dos.writeUTF(position.getTitle());
                            dos.writeUTF(position.getDescription());
                        }
                        dos.writeUTF(positionsEnd);
                    }
                    dos.writeUTF(organizationsEnd);
                }
            }
            dos.writeUTF(resumeEnd);
        }
    }

    @Override
    public Resume loadResume(InputStream is) throws IOException {
        Resume r;
        List<Organization> organizations;
        List<String> items;
        String ctx, key, value;


        try (DataInputStream dis = new DataInputStream(is)) {
            ctx = dis.readUTF();
            if (!ctx.equals(resumeBegin))
                throw new StorageException("Wrong resume file structure!");
            // Read Uuid and fullName
            r = new Resume(dis.readUTF(),dis.readUTF());
            // Read contacts
            while(isEnumContains(ContactType.class, key = dis.readUTF())){
                if ((value = dis.readUTF()).length() > 0)
                    r.setContact(ContactType.valueOf(key), value);
            }
            // Read Sections (assume that first section key is already read
            // in previous while block)
            do { // here in 'key' should be SectionType Key
                // if read section value is empty pass it without adding to resume
                if ((ctx = dis.readUTF()).length() == 0) continue;
                // check if content is a ListSection (should be items)
                if (ctx.equals(itemsBegin)){
                    items = new ArrayList<>();
                    // Read ListSection items
                    while (!(value = dis.readUTF()).equals(itemsEnd))
                        items.add(value);
                    r.setSection(SectionType.valueOf(key), new ListSection(items));
                } else //check if that is a OrganizationSection (should be organizations)
                    if(ctx.equals(organizationsBegin)){
                        organizations = new ArrayList<>();
                        // read organizations
                        while (!(value = dis.readUTF()).equals(organizationsEnd)){
                            Link link = new Link(value, dis.readUTF());
                            if (!dis.readUTF().equals(positionsBegin))
                                throw new StorageException("Positions begin tag not found!");
                            List<Organization.Position> positions = new ArrayList<>();
                            // read positions
                            while (!(value = dis.readUTF()).equals(positionsEnd)){
                                positions.add(new Organization.Position(
                                        DateUtil.parse(value),
                                        DateUtil.parse(dis.readUTF()),
                                        dis.readUTF(),
                                        dis.readUTF()
                                ));
                            }
                            organizations.add(new Organization(
                                    link, positions
                            ));
                        }
                        r.setSection(SectionType.valueOf(key),
                                new OrganizationSection(organizations));
                } else { // TextSection
                        r.setSection(SectionType.valueOf(key),
                                new TextSection(ctx));
                    }
            } while(isEnumContains(SectionType.class, key = dis.readUTF()));
            if (!key.equals(resumeEnd))
                throw new StorageException("Wrong resume structure at the end of file!");
            return r;
        }
    }

    private <T extends Enum<T>> boolean isEnumContains(Class<T> e, String s){
        return Arrays.stream(e.getEnumConstants())
                .anyMatch(t->t.name().equals(s));
    }
}

