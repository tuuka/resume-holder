package my.webapp.storage;

import my.webapp.storage.serializer.XmlJaxbSerializer;

public class XmlJaxbSerializerPathStorageTest extends AbstractStorageTest{
    public XmlJaxbSerializerPathStorageTest() {
        super(new PathStorage("tempStorage",
                new XmlJaxbSerializer()));
    }
}