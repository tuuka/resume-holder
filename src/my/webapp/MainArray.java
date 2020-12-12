package my.webapp;


import my.webapp.model.Resume;
import my.webapp.storage.SortedArrayStorage;
import my.webapp.storage.Storage;

public class MainArray {
    public static void main(String[] args) {

        Storage storage = new SortedArrayStorage();
        storage.save(new Resume("1"));
        storage.save(new Resume("2"));
        storage.save(new Resume("3"));
        storage.save(new Resume("4"));
        System.out.println(storage);
    }
}
