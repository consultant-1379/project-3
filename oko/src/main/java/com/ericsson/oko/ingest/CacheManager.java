package com.ericsson.oko.ingest;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

//FIXME - this never removes entries unless they're looked up. Add a periodic clean sweep which removes all outdated entries
public class CacheManager <T, U> {
    private class CacheItem <U> {
        private U item;
        private LocalDateTime lastUpdate;

        public CacheItem(U item){
            this.setItem(item);
        }

        public U getItem(){
            return this.item;
        }

        public void setItem(U item){
            this.item = item;
            this.lastUpdate = LocalDateTime.now();
        }

        public boolean isInvalid(LocalDateTime oldestAllowedUpdate){
            return lastUpdate.isBefore(oldestAllowedUpdate);
        }
    }

    private Map<T, CacheItem<U>> cache;
    private long secondsToLive;

    private LocalDateTime oldestAllowedUpdate(){
        return LocalDateTime.now().minusSeconds(secondsToLive);
    }

    public CacheManager(long secondsToLive){
        this.secondsToLive = secondsToLive;
        this.cache = new HashMap<>();
    }

    public U get(T key){
        if(!cache.containsKey(key)) return null;
        var item = cache.get(key);
        if(item.isInvalid(oldestAllowedUpdate())){
            cache.remove(key);
            return null;
        }
        return item.getItem();
    }

    public void set(T key, U val){
        if(cache.containsKey(key)) cache.get(key).setItem(val);
        else cache.put(key, new CacheItem<>(val));
    }

    public boolean has(T key){
        return get(key) != null;
    }
}
