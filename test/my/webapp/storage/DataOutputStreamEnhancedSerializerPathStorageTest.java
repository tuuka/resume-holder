package my.webapp.storage;

import my.webapp.storage.serializer.DataStreamSerializerEnhanced;

public class DataOutputStreamEnhancedSerializerPathStorageTest extends AbstractStorageTest{
    public DataOutputStreamEnhancedSerializerPathStorageTest() {
        super(new PathStorage("tempStorage",
                new DataStreamSerializerEnhanced()));
    }
}