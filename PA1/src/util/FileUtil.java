package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description: TODO
 * @author: Xue Zhang
 * @date: 2022-02-15
 * @version: 1.0
 **/
public class FileUtil {

    public static List<File> getFiles(String path) {
        List<File> lists = new ArrayList<>();
        File dir = new File(path);
        listAllFile(dir, lists);
        return lists;
    }

    public static void listAllFile(File f, List<File> lists) {
        for (File file : Objects.requireNonNull(f.listFiles())) {
            if (file.isDirectory())
                listAllFile(file, lists);
            if (file.isFile()) {
                lists.add(file);
            }
        }
    }
}
