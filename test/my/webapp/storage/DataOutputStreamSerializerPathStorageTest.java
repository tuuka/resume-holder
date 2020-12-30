package my.webapp.storage;

import my.webapp.storage.serializer.DataStreamSerializer;

public class DataOutputStreamSerializerPathStorageTest extends AbstractStorageTest{
    public DataOutputStreamSerializerPathStorageTest() {
        super(new PathStorage("tempStorage",
                new DataStreamSerializer()));
    }
}