package my.webapp.storage;

import my.webapp.storage.serializer.XmlJacksonSerializer;

public class XmlJacksonSerializerPathStorageTest extends AbstractStorageTest{
    public XmlJacksonSerializerPathStorageTest() {
        super(new PathStorage("tempStorage",
                new XmlJacksonSerializer()));
    }
}