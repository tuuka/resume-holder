package my.webapp.storage;

import my.webapp.storage.serializer.ObjectStreamSerializer;

public class ObjectSerializerFileStorageTest extends AbstractStorageTest {
    public ObjectSerializerFileStorageTest() {
        super(new FileStorage(AbstractStorageTest.STORAGE_DIR,
                new ObjectStreamSerializer()));
    }
}