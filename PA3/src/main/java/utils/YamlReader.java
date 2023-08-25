package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.Yaml;
import topology.Node;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: A read utility to get configuration.
 * @author: Xue Zhang
 * @date: 2022-03-04
 * @version: 1.0
 **/
public class YamlReader {

    private static Map<String, Object> properties;


    private static class SingletonHolder {
        private static final YamlReader instance = new YamlReader();
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
        Map<String, Object> rootProperty = (Map<String, Object>) properties.get(root);
        return rootProperty.getOrDefault(key, "");
    }

    public static List<Node> convertMapToObject(Object value) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        List<Node> links = mapper.convertValue(value, new TypeReference<List<Node>>() {});
//        for(Node link: links) {
//            System.out.println(link);
//        }
        return links;
    }

//    public static void main(String[] args) throws IOException {
//        Object ip = YamlReader.getInstance().getValueByKey("superPeer_1", "ip");
//        Object serverPort = YamlReader.getInstance().getValueByKey("superPeer_1", "serverPort");
//        Object clientPort = YamlReader.getInstance().getValueByKey("superPeer_1", "clientPort");
//        Object links = YamlReader.getInstance().getValueByKey("superPeer_1", "links");
//        System.out.println(ip);
//        System.out.println(serverPort);
//        System.out.println(clientPort);
//        for(Node link: convertMapToObject(links)) {
//            System.out.println(link);
//        }
//    }

}
