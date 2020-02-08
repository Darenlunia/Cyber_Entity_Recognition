package org.cyber.regexpTag;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//[Section1]
//Key1=Value1
//Key2=Value2
//[Section2]
//Key=Value

/**
 * @Title: 读取配置�?
 * @Description: 读取pdf文件内容 
 * @param path
 *
 */
public class ConfigReader {
     
    /**
     * 整个ini的引�?
     */
    private Map<String,Map<String, String>>  map = null;
    /**
     * 当前Section的引�?
     */
    private String currentSection = null;
     
    /**
     * 构�?�函�?-读取
     * @param path
     */
    public ConfigReader(String path) {
        map = new HashMap<String, Map<String,String>>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            read(reader);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IO Exception:" + e);
        }
         
    }
 
    /**
     * 读取文件
     * @param reader
     * @throws IOException
     */
    private void read(BufferedReader reader) throws IOException {
        String line = null;
        while((line=reader.readLine())!=null) {
            parseLine(line);
        }
    }
     
    /**
     * 转换
     * @param line
     */
    private void parseLine(String line) {
        line = line.trim();
        // 去掉前后空格
        if(line.matches("^\\#.*$")) {
        	//匹配ini中的注释
            return;
        }else if (line.matches("^\\[\\S+\\]$")) {
            // section
            String section = line.replaceFirst("^\\[(\\S+)\\]$","$1");
            addSection(map,section);
        }else if (line.matches("^\\S+=.*$")) {
            // key ,value
            int i = line.indexOf("=");
            String key = line.substring(0, i).trim();
            String value =line.substring(i + 1).trim();
            addKeyValue(map,currentSection,key,value);
        }
    }
 
 
    /**
     * 增加新的Key和Value
     * @param map
     * @param currentSection
     * @param key
     * @param value
     */
	private void addKeyValue(Map<String, Map<String, String>> map, String currentSection, String key, String value) {
		if (!map.containsKey(currentSection)) {
			return;
		}
		Map<String, String> childMap = map.get(currentSection);
		childMap.put(key, value);
	}
 
 
    /**
     * 增加Section
     * @param map
     * @param section
     */
    private void addSection(Map<String, Map<String, String>> map,
            String section) {
        if (!map.containsKey(section)) {
            currentSection = section;
            Map<String,String> childMap = new HashMap<String,String>();
            map.put(section, childMap);
        }
    }
     
    /**
     * 获取配置文件指定Section和指定子键的�?
     * @param section
     * @param key
     * @return
     */
    public String get(String section,String key){
        if(map.containsKey(section)) {
            return  get(section).containsKey(key) ?
                    get(section).get(key): null;
        }
        return null;
    }
     
     
     
    /**
     * 获取配置文件指定Section的子键和�?
     * @param section
     * @return
     */
    public Map<String, String> get(String section){
        return  map.containsKey(section) ? map.get(section) : null;
    }
     
    /**
     * 获取这个配置文件的节点和�?
     * @return
     */
    public Map<String, Map<String, String>> get(){
        return map;
    }
     
}