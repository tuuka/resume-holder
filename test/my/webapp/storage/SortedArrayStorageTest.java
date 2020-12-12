package my.webapp.storage;


import my.webapp.model.Resume;
import org.junit.Test;


public class SortedArrayStorageTest extends AbstractArrayStorageTest {

    public SortedArrayStorageTest() {
        super(new SortedArrayStorage());
    }

//    @Test
//    public void addFullDeleteFull() {
//        storage.clear();
//        for (int i = 0; i < storageArrayCapacity; i++) {
//            storage.save(new Resume());
//        }
//    }


}