package courses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class SortedDictionary {
    static List<String> keys = new ArrayList<>();
    static List<String> values = new ArrayList<>();
    
    public static void insert(String key, String value) {
        if (keys.contains(key)) {
            int index = keys.indexOf(key);
            values.set(index, value);
        } else {
            keys.add(key);
            values.add(key);
        }
    }
    public static String retrieve(String key) {
        int index = keys.indexOf(key);
        return index == -1 ? null : values.get(index);
    }
    
    public static boolean delete(String key) {
        if (keys.contains(key)) {
            int index = keys.indexOf(key);
            keys.remove(index);
            values.remove(index);
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean containsKey(String key) {
        return keys.contains(key);
    }
    
    public static void printDict() {
        List<String[]> sorted = new ArrayList<>();
        for (int i = 0; i < keys.size(); i++) {
            sorted.add(new String[]{keys.get(i), values.get(i)});
        }
        Collections.sort(sorted, (String[] en1, String[] en2) -> en1[0].compareTo(en2[0]));
        for (String[] en : sorted) {
            System.out.println("key: " + en[0] + " value: " + en[1]);
        }
    }

    public static void main(String[] args) {
        insert("C", "value3");
        insert("A", "value1");
        insert("D", "value4");
        insert("B", "value2");
        delete("B");
        printDict();
    }
}
