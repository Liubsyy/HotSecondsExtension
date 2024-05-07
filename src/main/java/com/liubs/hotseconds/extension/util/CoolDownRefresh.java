package com.liubs.hotseconds.extension.util;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 冷却时间，避免频繁刷新
 * @author Liubsyy
 * @date 2024/5/8
 **/
public enum CoolDownRefresh {
    INSTANCE;

    private Map<Class<?>,TimeUse> TIME_COOL_DOWN = new ConcurrentHashMap<>();

    /**
     * 冷却若干秒
     * @param kclass
     * @param seconds
     */
    public boolean addCoolDown(Class<?> kclass,int seconds) {
        synchronized (kclass) {
            if(isInCoolDown(kclass)) {
                return false;
            }
            TIME_COOL_DOWN.put(kclass,new TimeUse(System.currentTimeMillis(),seconds));
            return true;
        }
    }

    /**
     * 是否在冷却期间
     * @param klass
     * @return
     */
    public boolean isInCoolDown(Class<?> klass) {
        TimeUse timeUse = TIME_COOL_DOWN.get(klass);
        return null != timeUse && (System.currentTimeMillis() - timeUse.timestamp) >= timeUse.seconds;
    }

    static class TimeUse {
        long timestamp;
        int seconds;
        public TimeUse(long timestamp, int seconds) {
            this.timestamp = timestamp;
            this.seconds = seconds;
        }
    }

}
