package my.webapp.storage;

import my.webapp.storage.serializer.DataStreamSerializerEnhanced;

public class DataOutputStreamEnhancedSerializerPathStorageTest extends AbstractStorageTest{
    public DataOutputStreamEnhancedSerializerPathStorageTest() {
        super(new PathStorage(AbstractStorageTest.STORAGE_DIR,
                new DataStreamSerializerEnhanced()));
    }
}