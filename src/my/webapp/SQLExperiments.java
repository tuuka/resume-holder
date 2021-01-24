package my.webapp;

import my.webapp.model.ContactType;
import my.webapp.model.Resume;
import my.webapp.storage.PostgresTransactionalStorage;
import my.webapp.storage.Storage;

import java.util.Arrays;
import java.util.List;

public class SQLExperiments {


    public static void main(String[] args) {
        List<Resume> resumes = Resume.generateNFakeResumes(4);



        PostgresTransactionalStorage ps = new PostgresTransactionalStorage(Config.get().getDBUrl(),
                Config.get().getDBUser(), Config.get().getDBPassword());
        ps.clear();
        resumes.forEach(ps::save);
        ps.save(new Resume("111", "111"));
        ps.save(new Resume("222", "222"));
        ps.save(new Resume("333", "333"));
        Resume r1 = ps.get("111");
        r1 = ps.get(resumes.get(0).getUuid());
        System.out.println(r1);
        System.out.println(ContactType.MOBILE.toHtml(r1.getContacts().get(ContactType.MOBILE)));
        System.out.println(ContactType.HOME_PAGE.toHtml(r1.getContacts().get(ContactType.HOME_PAGE)));

        Storage storage= my.webapp.Config.get().getStorage();
        System.out.println(storage);
        System.out.println(Arrays.toString(storage.getAll()));

    }

}
