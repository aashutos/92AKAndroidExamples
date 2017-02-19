package com.ntak.examples.jniexample.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.WeakHashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Hashtable;

/**
 * Created by akakshepati on 21/12/16.
 */
public class MapBuilder<K,V> {

    private Map<K,V> map;

    public MapBuilder(Class<? extends Map> type) {

        switch (type.getCanonicalName()) {
            case "java.util.TreeMap":                           map = new TreeMap<>();
                                                                break;

            case "java.util.WeakHashMap":                       map = new WeakHashMap<>();
                                                                break;

            case "java.util.IdentityHashMap":                   map = new IdentityHashMap<>();
                                                                break;

            case "java.util.LinkedHashMap":                     map = new LinkedHashMap<>();
                                                                break;

            case "java.util.Hashtable":                         map = new Hashtable<>();
                                                                break;

            case "java.util.concurrent.ConcurrentSkipListMap":  map = new ConcurrentSkipListMap<>();
                                                                break;

            case "java.util.concurrent.ConcurrentHashMap":      map = new ConcurrentHashMap<>();
                                                                break;

            default:                                            map = new HashMap<>();
        }
    }

    public MapBuilder<K,V> addEntry(K key, V value) {
        map.put(key,value);
        return this;
    }

    public Map<K,V> build() {
        return map;
    }

}
