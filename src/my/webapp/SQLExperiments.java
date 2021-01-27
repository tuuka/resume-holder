package my.webapp;

import my.webapp.model.Resume;
import my.webapp.storage.PostgresTransactionalStorage;

import java.util.List;

public class SQLExperiments {


    public static void main(String[] args) {
        List<Resume> resumes = Resume.generateNFakeResumes(20);


        Resume r1, r2, r3;
        PostgresTransactionalStorage ps = new PostgresTransactionalStorage(Config.get().getDBUrl(),
                Config.get().getDBUser(), Config.get().getDBPassword());
//        Storage ps = new PostgresJsonStorage(Config.get().getDBUrl(),
//                Config.get().getDBUser(), Config.get().getDBPassword());
        ps.clear();
        resumes.forEach(ps::save);
        ps.save(new Resume("xxx", "xxx"));
        ps.save(new Resume("yyy", "yyy"));
        ps.save(new Resume("zzz", "zzz"));
//        r1 = ps.get("111");
//        r1 = ps.get(resumes.get(0).getUuid());
//        System.out.println(r1);
//        System.out.println(ContactType.MOBILE.toHtml(r1.getContacts().get(ContactType.MOBILE)));
//        System.out.println(ContactType.HOME_PAGE.toHtml(r1.getContacts().get(ContactType.HOME_PAGE)));

//        Storage storage= my.webapp.Config.get().getStorage();
//        System.out.println(storage);
////        System.out.println(storage.getAllSorted());
//        r1 = ps.get(resumes.get(0).getUuid());
//        r2 = ps.get(resumes.get(1).getUuid());
//        r3 = ps.get(resumes.get(2).getUuid());
//        System.out.println(r1.getSections());
//        System.out.println(r1.sort().getSections());
//        System.out.println(r2.getSections());
//        System.out.println(r3.getSections());

    }

}
