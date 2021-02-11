package my.webapp;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

public class FilesExperiments {
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
            Path path = Paths.get("D:/projects/java/javaops/resume_holder");
            Files.walkFileTree(path,
                    new FileVisitor<Path>() {
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
        File my_file = new File("D:/projects/java/javaops/resume_holder/src/my");

        FilesExperiments.getDirContent(my_file, 0);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream bios = new ByteArrayInputStream(new byte[0]);
//        bios.transferTo() read();
    }

    public static void getDirContent(File file, int tab){
            System.out.println(new String(new char[tab]).replace("\0", "\t") + file.getAbsolutePath());
            for (File f : Objects.requireNonNull(file.listFiles())) {
                if (f.isDirectory()) FilesExperiments.getDirContent(f, tab+1);
                    else {
                        System.out.println("\t" + new String(new char[tab]).replace("\0", "\t") + f.getName());

                }
            }
    }

}
