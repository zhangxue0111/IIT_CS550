package utils;

import org.yaml.snakeyaml.Yaml;
import topology.Node;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description: TODO
 * @author: Xue Zhang
 * @date: 2022-03-04
 * @version: 1.0
 **/
public class YamlReader {

    private static Map<String, Map<String, Object>> properties;

    private YamlReader() {
        if(SingletonHolder.instance != null) {
            throw new IllegalStateException();
        }
    }

    private static class SingletonHolder {
        private static YamlReader instance = new YamlReader();
    }

    public static YamlReader getInstance() {
        return SingletonHolder.instance;
    }

    static {
        InputStream in = null;
        try {
            properties = new HashMap<>();
            Yaml yaml = new Yaml();
            in = YamlReader.class.getClassLoader().getResourceAsStream("config.yaml");
            properties = yaml.loadAs(in, HashMap.class);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert in != null;
                in.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Object getValueByKey(String root, String key) {
        Map<String, Object> rootProperty = properties.get(root);
        return rootProperty.getOrDefault(key, "");
    }

    public static void main(String[] args) throws IOException {
        System.out.println(YamlReader.getInstance().getValueByKey("peer_1", "serverPort"));
    }

}
