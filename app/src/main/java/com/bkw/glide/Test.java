package com.bkw.glide;

public class Test {
    public static void main(String[] args) {
//        MemoryCache cache = new MemoryCache(5);
//        cache.put("name1", Value.getInstance());
//        cache.put("name2", Value.getInstance());
//        cache.put("name2", Value.getInstance());
//
//        Value value = cache.remove("name2");
//
//        cache.userRemove("name2");
//        cache.setMemoryCacheCallback(new MemoryCacheCallback() {
//            @Override
//            public void entryRemovedMemory(String key, Value value) {
//              System.out.println( "内存缓存中的元素被移除了 value:" + value + ",key=" + key);
//            }
//        });


        int inSampleSize = (int) Math.min(3.3, 3.3);
        inSampleSize = Math.max(1, inSampleSize);
        System.out.println( "inSampleSize Math.max:" +inSampleSize);
    }
}
