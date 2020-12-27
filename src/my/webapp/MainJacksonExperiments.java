package my.webapp;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import my.webapp.model.*;

import java.io.File;
import java.io.IOException;

public class MainJacksonExperiments {
    public static void main(String[] args) throws IOException {

        //        String carJson = "[{\"brand\":\"ford\", \"doors\":5},{\"brand\":\"ferrari\", \"doors\":3}]";
////        String carJson = "{ \"brand\" : \"Mercedes\", \"doors\" : 5 }";
//        JsonParser parser  = (new JsonFactory()).createParser(carJson);
//
//        while(!parser.isClosed()){
//            JsonToken jsonToken = parser.nextToken();
//            if(JsonToken.FIELD_NAME.equals(jsonToken)) {
//                System.out.print("field = " + parser.getCurrentName() +
//                        " : ");
//                parser.nextToken();
//                System.out.println(parser.getValueAsString());
//            } else
//                System.out.println("jsonToken = " + jsonToken);
//        }



        Resume r = new Resume("dummy.dummy", "dummy");
        r.setContact(ContactType.MOBILE, "+123456789");
        r.setContact(ContactType.PHONE, "+987654321");
        Organization.Position p1 = new Organization.Position("11/2000", "12/2000", "worker1", "dummy worker 1");
        Organization.Position p2 = new Organization.Position("12/2001", "01/2002", "worker2", "dummy worker 2");
        Organization.Position p3 = new Organization.Position("02/2003", "12/2003", "worker3", "dummy worker 3");
        Organization.Position p4 = new Organization.Position("05/2005", "06/2006", "worker4", "dummy worker 4");
        Organization o1 = new Organization("Dummy company1", "dummy.company1@gmail.com", p1, p2);
        Organization o2 = new Organization("Dummy company2", "dummy.company2@gmail.com", p3);
        Organization o3 = new Organization("Dummy company3", "dummy.company3@gmail.com", p4);
        r.setSection(SectionType.EXPERIENCE, new OrganizationSection(o1, o2, o3));
        r.setSection(SectionType.QUALIFICATIONS, new ListSection("Can dig", "Can to not dig", "Can sleep"));
        r.setSection(SectionType.ACHIEVEMENT, new ListSection("Know how to dig", "Know how to not dig", "Know how to sleep"));
        r.setSection(SectionType.EDUCATION, new OrganizationSection(
                new Organization("Dummy company4", "dummy.company4@gmail.com",
                        new Organization.Position("01/1999", "10/1999", "student", "had been learning how to dig"))));
        r.setSection(SectionType.PERSONAL, new TextSection("Very good person that can work hard in area of digging."));
        r.setSection(SectionType.OBJECTIVE, new TextSection("Experienced digger"));

//        System.out.println(r);



        ObjectMapper OM = JsonMapper.builder()
                //For adding type info to output Json
//                .activateDefaultTyping(
//                BasicPolymorphicTypeValidator.builder()
//                        .allowIfSubType("my.webapp.")
//                        .allowIfSubType(EnumMap.class)
//                        .allowIfSubType(ArrayList.class)
//                        .allowIfSubType(LocalDate.class)
//                        .build(),
//                ObjectMapper.DefaultTyping.EVERYTHING, JsonTypeInfo.As.WRAPPER_OBJECT)
                .addModule(new JavaTimeModule()) // to normally work with JDK8 time
                //JsonAutoDetect here used to avoid using
                //@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY) annotation
                .build().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        //Error when deserializing LocalDate. Need something else to store in "yyyy-MM" format
//        OM.configOverride(LocalDate.class)
//                .setFormat(JsonFormat.Value.forPattern("yyyy-MM"));
        OM.configure(SerializationFeature.INDENT_OUTPUT, true);
        OM.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String s = OM.writeValueAsString(r);
        System.out.println(s);

        File file = new File("d:\\temp\\r.json");
        OM.writeValue(file, r);
//        Resume r_r = OM.readValue(s, Resume.class);
        Resume r_r = OM.readValue(file, Resume.class);
        System.out.println(r_r.equals(r));

    }
}
