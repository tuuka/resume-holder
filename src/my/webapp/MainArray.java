package my.webapp;


import my.webapp.model.*;

import java.util.UUID;

public class MainArray {
    public static void main(String[] args) {
//        Storage storage = new SortedArrayStorage();
//        storage.save(new Resume("1"));
//        storage.save(new Resume("2"));
//        storage.save(new Resume("3"));
//        storage.save(new Resume("4"));
        System.out.println(UUID.randomUUID().toString());

//        try {
//            Method method = storage.getClass()
//                    .getDeclaredMethod("toString");
//            System.out.println(method.invoke(storage));
//        } catch (NoSuchMethodException |
//                InvocationTargetException |
//                IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        List<Resume> list = new ArrayList<>(List.of(
//                new Resume("1"),
//                new Resume("2"),
//                new Resume("3"),
//                new Resume("4")
//        ));
//
//        list.removeIf(resume -> Objects.equals(resume.getUuid(), "1"));
//        System.out.println(list);
//
//        try {
//            Field f = Resume.class.getDeclaredField("uuid");
//            System.out.println(f.getType());
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }

        Resume R1 = new Resume();
        R1.setContact(ContactType.MOBILE, "+321654987");
        R1.setContact(ContactType.HOME_PHONE, "+321654987");
        R1.setContact(ContactType.PHONE, "+852369741");
        System.out.println(ContactType.PHONE.returnContact(R1.getContact(ContactType.PHONE)));
        System.out.println(ContactType.MOBILE.returnContact(R1.getContact(ContactType.MOBILE)));

        R1.setSection(SectionType.EXPERIENCE,
            new OrganizationSection(
                new Organization("MKS", null,
                    new Organization.Position("Сборщик",
                            "10/09/2000",
                            "03/07/2001"),
                    new Organization.Position("Наладчик",
                            "04/07/2001",
                            "25/01/2002"),
                    new Organization.Position("Инженер ТАЛ",
                            "26/01/2002",
                            "15/10/2002")
                ),
                new Organization("ФОП", null,
                    new Organization.Position("Разгильдяй",
                            "15/10/2002",
                            "15/10/2003")
                )
            )
        );
        R1.setSection(SectionType.QUALIFICATIONS,
            new ListSection(
                "Могу копать.",
                "Могу не копать.",
                "Могу спать и есть."
        ));
        System.out.println(((OrganizationSection)R1.getSection(SectionType.EXPERIENCE)).getOrganizations());



//        System.out.println(((OrganizationSection)R1.getSection(SectionType.EXPERIENCE)).getOrganizations().get(0) instanceof Organization);
//        System.out.println(((Organization) R1.getSection(SectionType.EXPERIENCE).getItems().get(0)).getOrganizationName());
//        System.out.println(((Organization)R1.getSection(SectionType.QUALIFICATIONS).getItems().get(0)).getOrganizationName());

    }

}

