package my.webapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class MainFiles {
    public static void main(String[] args) {
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

        try {
            Path path = Path.of("D:/projects/java/javaops/my_basejava1");
            Files.walkFileTree(path,
                    new FileVisitor<>() {
                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                            if (dir.compareTo(path.resolve(".git")) == 0)
                                return FileVisitResult.SKIP_SUBTREE;
                            System.out.printf("Visit dir %s\\\n", dir);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                            System.out.println("\t\t\t" + file.getFileName());
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path file, IOException exc) {
                            System.out.println("Error visiting site");
                            return FileVisitResult.TERMINATE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                            return FileVisitResult.CONTINUE;
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* дерево через File */
        System.out.println("---------------------------------------------");
        File my_file = new File("D:/projects/java/javaops/my_basejava1/src/my");

        MainFiles.getDirContent(my_file, 0);


    }

    static public void getDirContent(File file, int tab){
            System.out.println(file.getAbsolutePath());
            for (File f : file.listFiles()) {
                if (f.isDirectory()) MainFiles.getDirContent(f, tab+1);
                    else {
                        System.out.println("\t".repeat(tab) + f.getName());

                }
            }
    }
}
