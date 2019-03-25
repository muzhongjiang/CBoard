package org.cboard.cache;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 缓存方式1：Java heap
 */
public class HeapCacheManager<T> implements CacheManager<T> {

    private ConcurrentMap<String, CacheObject> cache = new ConcurrentHashMap<>();

    /**
     * 默认构造方法
     */
    public HeapCacheManager() {

    }

    @Override
    public void put(String key, T data, long expire) {
        cache.put(key, new CacheObject(new Date().getTime(), expire, data));
    }

    @Override
    public T get(String key) {
        CacheObject o = cache.get(key);

        if (o == null) {//等于null
            return null;
        } else if (System.currentTimeMillis() >= o.getT1() + o.getExpire()) {//不等于null，但失效！
            cache.remove(key);//删除失效的key
            return null;
        } else {
            return (T) o.getD();
        }
    }

    @Override
    public void remove(String key) {
        cache.remove(key);
    }


}
