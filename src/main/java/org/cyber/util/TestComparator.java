package org.cyber.util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
public class TestComparator {
//    public static void main(String[] args) {
//        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
// 
//        list.add(getData(0));
//        list.add(getData(3));
//        list.add(getData(05));
//        list.add(getData(6));
//        list.add(getData(2));
// 
//        System.out.println("≈≈–Ú«∞" + list);
// 
//        Collections.sort(list, new Comparator<Map<String, String>>() {
//            public int compare(Map<String, String> o1, Map<String, String> o2) {
//                return o1.get("countScore").compareTo(o2.get("countScore"));
//            }
//        });
// 
//        System.out.println("≈≈–Ú∫Û" + list);
//    }
 
    private static Map<String, String> getData(int num) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("countScore", String.valueOf(num));
        return map;
    }
}