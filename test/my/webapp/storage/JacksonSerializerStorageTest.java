package my.webapp.storage;

import my.webapp.storage.serializer.JsonJacksonSerializer;

public class JacksonSerializerStorageTest extends AbstractStorageTest{
    public JacksonSerializerStorageTest() {
        super(new FileStorage("tempStorage",
                new JsonJacksonSerializer()));
    }
}