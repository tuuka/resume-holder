package my.webapp.storage;

import my.webapp.storage.serializer.JsonJacksonSerializer;

public class JacksonSerializerPathStorageTest extends AbstractStorageTest{
    public JacksonSerializerPathStorageTest() {
        super(new PathStorage("tempStorage",
                new JsonJacksonSerializer()));
    }
}