package my.webapp.storage.serializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import my.webapp.model.Resume;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* Using dependencies:
jackson-annotations-2.10.2-sources.jar
jackson-core-2.10.2.jar
jackson-databind-2.10.2.jar
jackson-datatype-jsr310-2.10.2.jar
*/

public class JsonJacksonSerializer implements ResumeSerializer{
    protected ObjectMapper OM;
    protected String fileSuffix;

    public JsonJacksonSerializer(){this(".json");}

    public JsonJacksonSerializer(String fileSuffix) {
        OM = getConfiguredMapper(JsonMapper.builder()
                //Adding LocalDate support
//                .addModule(new JavaTimeModule())
                .build());
        this.fileSuffix = fileSuffix;
    }

    @Override
    public String getFileSuffix(){
        return fileSuffix;
    }

    @Override
    public void saveResume(Resume r, OutputStream os) throws IOException {
        OM.writeValue(os, r);
    }

    @Override
    public Resume loadResume(InputStream is) throws IOException {
        return OM.readValue(is, Resume.class);
    }

    protected ObjectMapper getConfiguredMapper(ObjectMapper om){
        return om
                //We can also use annotation to Resume class:
                // @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.FIELD,
                        JsonAutoDetect.Visibility.ANY)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(SerializationFeature.INDENT_OUTPUT, true);
    }
}
