package my.webapp.storage;

import my.webapp.storage.serializer.JsonJacksonSerializer;

public class JacksonSerializerFileStorageTest extends AbstractStorageTest{
    public JacksonSerializerFileStorageTest() {
        super(new FileStorage("tempStorage",
                new JsonJacksonSerializer()));
    }
}