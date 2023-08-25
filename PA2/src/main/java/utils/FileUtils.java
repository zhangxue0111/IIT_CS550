package utils;

import topology.Node;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: A utility of file operation
 * @author: Xue Zhang
 * @date: 2022-03-01
 * @version: 1.0
 **/
public class FileUtils {

    public static void generateFiles(List<Node> nodes) throws IOException {
        for(Node node: nodes) {
            String dir = "src/dir/" + node.getNodeName();
            File file = new File(dir);
            if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
            }
            for (int i = 1; i < 11; i++) {
                File file2 = new File(dir, node.getNodeName() + "_" + i + ".txt");
                if (!file2.exists()) {
                    boolean newFile = file2.createNewFile();
                    System.out.println(newFile);
                }
            }
        }
    }

    public static void generateConfigFile(String path, List<Node> nodes) {
        FileWriter fw = null;
        try {
            File file = new File(path);
            fw = new FileWriter(file,false);
            for (Node node : nodes) {
                fw.write(node.getNodeName() + ": \n");
                fw.write("     ip: " + node.getIp() + "\n");
                fw.write("     serverPort: " + node.getServerPort() + "\n");
                fw.write("     clientPort: " + node.getClientPort() + "\n");
                fw.write("     links: \n");
                int linkNum = 0;
                for (Node link : node.getLinks()) {
                    linkNum++;
                    fw.write("          - link: " + "\n");
                    fw.write("            nodeName: " + link.getNodeName() + "\n");
                    fw.write("            ip: " + link.getIp() + "\n");
                    fw.write("            serverPort: " + link.getServerPort() + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fw != null){

                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
