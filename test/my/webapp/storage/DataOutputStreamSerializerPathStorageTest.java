package my.webapp.storage;

import my.webapp.storage.serializer.DataStreamSerializer;

public class DataOutputStreamSerializerPathStorageTest extends AbstractStorageTest{
    public DataOutputStreamSerializerPathStorageTest() {
        super(new PathStorage(AbstractStorageTest.STORAGE_DIR,
                new DataStreamSerializer()));
    }
}