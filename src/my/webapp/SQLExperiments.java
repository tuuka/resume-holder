package my.webapp;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import my.webapp.model.*;
import my.webapp.storage.PostgresTransactionalStorage;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class SQLExperiments {


    public static void main(String[] args) {
        List<Resume> resumes = Resume.generateNFakeResumes(4);


        PostgresTransactionalStorage ps = new PostgresTransactionalStorage(Config.get().getDBUrl(),
                Config.get().getDBUser(), Config.get().getDBPassword());
        ps.clear();
        ps.save(new Resume("111", "222"));
        ObjectMapper om = new ObjectMapper()
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                ;

        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator
                .builder()
                .allowIfBaseType("my.webapp")
                .allowIfSubType(TextSection.class)
                .allowIfSubType(ListSection.class)
                .allowIfSubType(OrganizationSection.class)
                .allowIfSubType(ArrayList.class)
                .allowIfSubType(EnumMap.class)
                .build();

        om.setDefaultTyping(
                ObjectMapper.DefaultTypeResolverBuilder
                        .construct(ObjectMapper.DefaultTyping.EVERYTHING, ptv)
                        .init(JsonTypeInfo.Id.CLASS, null)
                        .inclusion(JsonTypeInfo.As.PROPERTY)
                        .typeProperty("@type")
        );

//        om.activateDefaultTypingAsProperty(ptv, ObjectMapper.DefaultTyping.EVERYTHING, "@type");


        TypeReference<EnumMap<SectionType, Section>> typeRef
                = new TypeReference<>() {};


//            String s = om.writeValueAsString(resumes.get(0).getSections());
//            System.out.println(s);
//            EnumMap<SectionType, Section> ls = om.readValue(s, typeRef);
//            System.out.println(ls);
//            System.out.println(ls.equals(resumes.get(0).getSections()));
//            s = om.writeValueAsString(resumes.get(0).getContacts());
//            System.out.println(s);
//            String cont = SQLHelper.convert(
//                    o-> {
//                        String result = o.writeValueAsString(resumes.get(0).getContacts());
//                        return result;
//                    });
//            System.out.println(cont);
//            TypeReference<EnumMap<ContactType, String>> contactsTypeRef
//                    = new TypeReference<>() {};
//            EnumMap<ContactType, String> c = SQLHelper.convert(
//                    o -> {
//                        EnumMap<ContactType, String> conts = o.readValue(cont, contactsTypeRef);
//                        return conts;
//                    }
//            );
//            System.out.println(c);
//            EnumMap<ContactType, String> c = om.readValue(s, contactsTypeRef);
//            System.out.println(c);

//            String s = om.writeValueAsString(resumes.get(0));
//            System.out.println(s);
//            Resume r = om.readValue(s, Resume.class);
//            System.out.println(r);


//        resumes.forEach(ps::save);
////        System.out.println(resumes.get(0));
//        Resume r1 = ps.get("uuid3");
//        System.out.println(r1.equals(resumes.get(3)));

//        System.out.println(resumes.get(5).getSection(SectionType.EXPERIENCE).equals(r1.getSection(SectionType.EXPERIENCE)));
//        System.out.println(resumes.get(5).getSection(SectionType.EDUCATION).equals(r1.getSection(SectionType.EDUCATION)));
//        System.out.println(resumes.get(5).getSection(SectionType.QUALIFICATIONS).equals(r1.getSection(SectionType.QUALIFICATIONS)));
//        System.out.println(resumes.get(5).getSection(SectionType.PERSONAL).equals(r1.getSection(SectionType.PERSONAL)));
//        System.out.println(resumes.get(5).getSection(SectionType.ACHIEVEMENT).equals(r1.getSection(SectionType.ACHIEVEMENT)));
//        System.out.println(resumes.get(5).getSection(SectionType.EXPERIENCE));
//        System.out.println(r1.getSection(SectionType.EXPERIENCE));

//        System.out.println(ps.get(R1.getUuid()));

//        PostgresJsonStorage postgresJsonStorage = new PostgresJsonStorage();
//        postgresJsonStorage.clear();
//        postgresJsonStorage.save(resumes.get(1));
//        Resume r1 = postgresJsonStorage.get(resumes.get(1).getUuid());
//        System.out.println(resumes.get(1).equals(r1));


    }

}
