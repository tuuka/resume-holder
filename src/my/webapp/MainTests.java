package my.webapp;


import java.io.*;
import java.util.UUID;

public class MainTests {
    public static void main(String[] args) {
//        Storage storage = new SortedArrayStorage();
//        storage.save(new Resume("1"));
//        storage.save(new Resume("2"));
//        storage.save(new Resume("3"));
//        storage.save(new Resume("4"));
        System.out.println(UUID.randomUUID().toString());

//        try {
//            Method method = storage.getClass()
//                    .getDeclaredMethod("toString");
//            System.out.println(method.invoke(storage));
//        } catch (NoSuchMethodException |
//                InvocationTargetException |
//                IllegalAccessException e) {
//            e.printStackTrace();
//        }

//
//        try {
//            Field f = Resume.class.getDeclaredField("uuid");
//            System.out.println(f.getType());
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }

        File file = new File(".\\.gitignore");
        try (BufferedReader br = new BufferedReader(new FileReader(file))){
            System.out.println(file.getAbsolutePath());
            String s;
            while ((s = br.readLine()) != null){
                System.out.println(s);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(file.getAbsolutePath());
    }

}

