package com.bkw.glide;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class LRU {
    public static void main(String[] args) {
        //accessOrder= true ，拥有访问排序的功能
        LinkedHashMap<String, Integer> hashMap = new LinkedHashMap(0, 0.75F, true);

        hashMap.put("一", 1); //最开始添加的，它的回收率是最高的
        hashMap.put("二", 2);
        hashMap.put("三", 3);
        hashMap.put("四", 4);
        hashMap.put("五", 5); //最后添加的它的Lru回收率是最低的

        //使用一次
        hashMap.get("四");

        Set<Map.Entry<String, Integer>> entries = hashMap.entrySet();
        for (Map.Entry<String, Integer> entry : entries) {
            System.out.println("" + entry.getValue());
        }

    }
}
