package my.webapp;

import my.webapp.model.*;
import my.webapp.storage.PostgresStorage;

import java.util.List;

public class SQLExperiments {


    public static void main(String[] args) {
        List<Resume> resumes = Resume.generateNFakeResumes(4);


        PostgresStorage ps = new PostgresStorage(Config.get().getDBUrl(),
                Config.get().getDBUser(), Config.get().getDBPassword());
        ps.clear();
        resumes.forEach(ps::save);
        Resume r1 = ps.get("uuid3");
        System.out.println(r1.equals(resumes.get(3)));
        ps.delete("uuid3");
        ps.delete("uuid3");
//        System.out.println(resumes.get(5).getSection(SectionType.EXPERIENCE).equals(r1.getSection(SectionType.EXPERIENCE)));
//        System.out.println(resumes.get(5).getSection(SectionType.EDUCATION).equals(r1.getSection(SectionType.EDUCATION)));
//        System.out.println(resumes.get(5).getSection(SectionType.QUALIFICATIONS).equals(r1.getSection(SectionType.QUALIFICATIONS)));
//        System.out.println(resumes.get(5).getSection(SectionType.PERSONAL).equals(r1.getSection(SectionType.PERSONAL)));
//        System.out.println(resumes.get(5).getSection(SectionType.ACHIEVEMENT).equals(r1.getSection(SectionType.ACHIEVEMENT)));
//        System.out.println(resumes.get(5).getSection(SectionType.EXPERIENCE));
//        System.out.println(r1.getSection(SectionType.EXPERIENCE));

//        System.out.println(ps.get(R1.getUuid()));
    }

}
