package my.webapp.storage;

import my.webapp.storage.serializer.ObjectStreamSerializer;

public class ObjectSerializerStorageTest extends AbstractStorageTest{
    public ObjectSerializerStorageTest() {
        super(new FileStorage("tempStorage",
                new ObjectStreamSerializer()));
    }
}