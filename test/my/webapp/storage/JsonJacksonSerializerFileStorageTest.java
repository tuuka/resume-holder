package my.webapp.storage;

import my.webapp.storage.serializer.JsonJacksonSerializer;

public class JsonJacksonSerializerFileStorageTest extends AbstractStorageTest {
    public JsonJacksonSerializerFileStorageTest() {
        super(new FileStorage(AbstractStorageTest.STORAGE_DIR,
                new JsonJacksonSerializer()));
    }
}