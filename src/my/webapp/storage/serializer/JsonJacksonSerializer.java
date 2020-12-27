package my.webapp.storage.serializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

    protected static final ObjectMapper OM = JsonMapper.builder()
            //Adding LocalDate support
            .addModule(new JavaTimeModule())
            .build()
            //We can also use annotation to Resume class:
            // @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
            .setVisibility(PropertyAccessor.FIELD,
                    JsonAutoDetect.Visibility.ANY)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(SerializationFeature.INDENT_OUTPUT, true);

    private String fileSuffix = ".json";

    public JsonJacksonSerializer(){}

    public JsonJacksonSerializer(String fileSuffix) {
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
}
