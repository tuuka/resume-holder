package my.webapp.storage;

import my.webapp.storage.serializer.JsonJacksonSerializer;

public class JsonJacksonSerializerPathStorageTest extends AbstractStorageTest{
    public JsonJacksonSerializerPathStorageTest() {
        super(new PathStorage("tempStorage",
                new JsonJacksonSerializer()));
    }
}