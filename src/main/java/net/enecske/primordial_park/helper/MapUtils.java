package net.enecske.primordial_park.helper;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapUtils {
    @SafeVarargs
    public static <K, V> LinkedHashMap<K, V> linkedMapOf(Map.Entry<K, V>... entries) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();

        for (Map.Entry<K, V> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }
}
