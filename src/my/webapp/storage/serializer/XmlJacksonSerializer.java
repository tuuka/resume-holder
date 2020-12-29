package my.webapp.storage.serializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XmlJacksonSerializer extends JsonJacksonSerializer {

    public XmlJacksonSerializer() {
        this(".xml");
    }

    public XmlJacksonSerializer(String fileSuffix) {
        OM = getConfiguredMapper(
                XmlMapper.builder()
                //Adding LocalDate support
//                .addModule(new JavaTimeModule())
                .build())
                //to avoid NullPointerException when deserializing empty fields
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        this.fileSuffix = fileSuffix;
    }
}
