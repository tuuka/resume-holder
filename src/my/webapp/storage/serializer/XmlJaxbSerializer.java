package my.webapp.storage.serializer;

import my.webapp.model.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;

/* Using dependencies:
javax.activation-api-1.2.0
jaxb-api-2.3.1
jaxb-core-3.0.0
jaxb-impl-2.3.2
jaxb-runtime-3.0.0
*/

public class XmlJaxbSerializer extends ResumeSerializer{

    protected XmlJaxbParser jaxbParser;

    public XmlJaxbSerializer(){this(".xml");}

    public XmlJaxbSerializer(String fileSuffix) {
        jaxbParser = new XmlJaxbParser(
                Resume.class,
                Link.class,
                OrganizationSection.class,
                Organization.class,
                Organization.Position.class,
                ListSection.class,
                TextSection.class
        );
        this.fileSuffix = fileSuffix;
    }

    @Override
    public void saveResume(Resume r, OutputStream os) throws IOException {
        try (Writer w = new OutputStreamWriter(os)){
        jaxbParser.marshall(r, w);
        }
    }

    @Override
    public Resume loadResume(InputStream is) throws IOException {
        try (Reader r = new InputStreamReader(is)){
            return jaxbParser.unmarshall(r);
        }
    }

    static class XmlJaxbParser{
        private final Marshaller marshaller;
        private final Unmarshaller unmarshaller;

        public XmlJaxbParser(Class<?>... classesToBeBound) {
            try {
                JAXBContext ctx = JAXBContext.newInstance(classesToBeBound);
                marshaller = ctx.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
                unmarshaller = ctx.createUnmarshaller();
            } catch (JAXBException e) {
                throw new IllegalStateException(e);
            }
        }
        @SuppressWarnings("unchecked")
        public <T> T unmarshall(Reader reader){
            try {
                return (T) unmarshaller.unmarshal(reader);
            } catch (JAXBException e) {
                throw new IllegalStateException(e);
            }
        }

        public void marshall (Object instance, Writer writer){
            try {
                marshaller.marshal(instance, writer);
            } catch (JAXBException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}



