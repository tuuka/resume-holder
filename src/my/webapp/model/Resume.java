package my.webapp.model;

import java.util.Objects;

public class Resume implements Comparable<Resume>{
    public static int count;
    private String uuid;

    public Resume(){
        this((count + 1) + ".000");
    }
//    public Resume(){
//        this(UUID.randomUUID().toString());
//    }

    public Resume(String uuid) {
        this.uuid = uuid;
        count++;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "Resume{" +
                "uuid='" + uuid + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resume resume = (Resume) o;
        return Objects.equals(uuid, resume.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public int compareTo(Resume o) {
        if (o == null || getClass() != o.getClass()) return 1;
        return this.getUuid().compareTo(o.getUuid());
    }
}
