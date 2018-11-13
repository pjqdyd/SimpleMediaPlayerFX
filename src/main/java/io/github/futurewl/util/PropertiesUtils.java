package io.github.futurewl.util;

import java.io.InputStream;
import java.util.*;

/**
 * 功能描述：Props 工具类
 *
 * @author weilai create by 2018/11/13:2:13 PM
 * @version 1.0
 */
public class PropertiesUtils {
    public static Map<String, String> readDetails() {
        Map<String, String> map = new HashMap<>();
        try {
            InputStream in = PropertiesUtils.class.getClassLoader().getResourceAsStream("details.properties");
            Properties p = new Properties();
            p.load(in);
            map.put("name", p.getProperty("name"));
            map.put("version", p.getProperty("version"));
            map.put("link", p.getProperty("link"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static List<String> readFormats() {
        List<String> formats = null;
        try {
            InputStream in = PropertiesUtils.class.getClassLoader().getResourceAsStream("details.properties");
            Properties p = new Properties();
            p.load(in);
            formats = Arrays.asList(p.getProperty("formats").split(","));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formats;
    }
}
