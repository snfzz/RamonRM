package com.ramon.ramonrm.util;

public class DelayedUtil extends Thread {
    private static long lastClickTime;

    public synchronized static boolean isFastClick(long interval) {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < interval) {
            return false;
        }
        lastClickTime = time;
        return true;
    }
}
