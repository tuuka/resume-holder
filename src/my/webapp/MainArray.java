package my.webapp;


import my.webapp.model.Resume;
import my.webapp.storage.SortedArrayStorage;
import my.webapp.storage.Storage;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MainArray {
    public static void main(String[] args) {
        Storage storage = new SortedArrayStorage();
        storage.save(new Resume("1"));
        storage.save(new Resume("2"));
        storage.save(new Resume("3"));
        storage.save(new Resume("4"));
        System.out.println(UUID.randomUUID().toString());

        try {
            Method method = storage.getClass()
                    .getDeclaredMethod("toString");
            System.out.println(method.invoke(storage));
        } catch (NoSuchMethodException |
                InvocationTargetException |
                IllegalAccessException e) {
            e.printStackTrace();
        }

        List<Resume> list = new ArrayList<>(List.of(
                new Resume("1"),
                new Resume("2"),
                new Resume("3"),
                new Resume("4")
        ));

        list.removeIf(resume -> Objects.equals(resume.getUuid(), "1"));
        System.out.println(list);

        try {
            Field f = Resume.class.getDeclaredField("uuid");
            System.out.println(f.getType());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }
}
