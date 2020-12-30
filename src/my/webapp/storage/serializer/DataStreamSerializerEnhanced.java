package my.webapp.storage.serializer;

import my.webapp.model.*;
import my.webapp.util.DateUtil;

import java.io.*;
import java.util.*;

public class DataStreamSerializerEnhanced extends ResumeSerializer {

    public DataStreamSerializerEnhanced() {
        this(".dat");
    }

    public DataStreamSerializerEnhanced(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    @Override
    public void saveResume(Resume r, OutputStream os) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(os)) {
            dos.writeUTF(r.getUuid());
            dos.writeUTF(r.getFullName());

            writeCollection(dos, r.getContacts().entrySet(), entry->{
                dos.writeUTF(entry.getKey().name());
                dos.writeUTF(entry.getValue());
            });

            writeCollection(dos, r.getSections().entrySet(), entry->{
                SectionType type = entry.getKey();
                Section section = entry.getValue();
                dos.writeUTF(type.name());
                switch (type) {
                    case PERSONAL:
                    case OBJECTIVE:
                        dos.writeUTF(((TextSection) section).getContent());
                        break;
                    case ACHIEVEMENT:
                    case QUALIFICATIONS:
                        writeCollection(dos, ((ListSection) section).getItems(), dos::writeUTF);
                        break;
                    case EXPERIENCE:
                    case EDUCATION:
                        writeCollection(dos, ((OrganizationSection) section).getOrganizations(), org -> {
                            dos.writeUTF(org.getHomePage().getName());
                            dos.writeUTF(org.getHomePage().getUrl());
                            writeCollection(dos, org.getPositions(), position -> {
                                dos.writeUTF(DateUtil.format(position.getStartDate()));
                                dos.writeUTF(DateUtil.format(position.getEndDate()));
                                dos.writeUTF(position.getTitle());
                                dos.writeUTF(position.getDescription());
                            });
                        });
                        break;
                }
            });
        }
    }

    @Override
    public Resume loadResume(InputStream is) throws IOException {
        Resume r;

        try (DataInputStream dis = new DataInputStream(is)) {
            r = new Resume(dis.readUTF(), dis.readUTF());
            readCollection(dis, () -> r.setContact(ContactType.valueOf(dis.readUTF()),
                    dis.readUTF()));
            readCollection(dis, () -> {
                String type = dis.readUTF();
                switch (type) {
                    case "PERSONAL":
                    case "OBJECTIVE":
                        r.setSection(SectionType.valueOf(type),
                                new TextSection(dis.readUTF()));
                        break;
                    case "ACHIEVEMENT":
                    case "QUALIFICATIONS":
                        List<String> items = new ArrayList<>();
                        readCollection(dis, ()->items.add(dis.readUTF()));
                        r.setSection(SectionType.valueOf(type),
                                new ListSection(items));
                        break;
                    case "EXPERIENCE":
                    case "EDUCATION":
                        List<Organization> organizations = new ArrayList<>();
                        readCollection(dis, () -> {
                            Link link = new Link(dis.readUTF(), dis.readUTF());
                            List<Organization.Position> positions = new ArrayList<>();
                            readCollection(dis, () ->
                                    positions.add(new Organization.Position(
                                    DateUtil.parse(dis.readUTF()),
                                    DateUtil.parse(dis.readUTF()),
                                    dis.readUTF(),
                                    dis.readUTF()
                            )));
                            organizations.add(new Organization(link, positions));
                        });
                        r.setSection(SectionType.valueOf(type),
                                new OrganizationSection(organizations));
                        break;
                }
            });
            return r;
        }
    }

    private interface ValueWriter<T>{
        void write(T value) throws IOException;
    }

    private interface ValueReader<T>{
        void read() throws IOException;
    }

    private <T> void writeCollection(DataOutputStream dos,
                                     Collection<T> collection,
                                     ValueWriter<T> writer) throws IOException {
        dos.writeInt(collection.size());
        for (T elem : collection){
            writer.write(elem);
        }
    }

    private <T> void readCollection(DataInputStream dis,
                                    ValueReader<T> reader) throws IOException {
        int size = dis.readInt();
        for (int i = 0; i < size; i++){
            reader.read();
        }
    }

}

