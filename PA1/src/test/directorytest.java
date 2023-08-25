package test;

import java.io.File;
import java.io.IOException;

/**
 * @description: TODO
 * @author: Xue Zhang
 * @date: 2022-02-15
 * @version: 1.0
 **/
public class directorytest {

    public static void main(String[] args) throws IOException {
        String dir = "src/dir/peer3";
        File file = new File(dir);
        if(!file.exists()) {
            boolean mkdirs = file.mkdirs();
        }
        for(int i = 0; i< 10;i++) {
            File file2 = new File(dir, "foo_bar_" + i + ".txt");
            if(!file2.exists()) {
                file2.createNewFile();
            }
        }
    }
}
