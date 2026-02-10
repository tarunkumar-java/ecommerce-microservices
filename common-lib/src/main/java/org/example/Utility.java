package org.example;


import java.util.concurrent.atomic.AtomicInteger;

public class Utility {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    private static long lastTimestamp = System.currentTimeMillis();

    public static synchronized long generate() {
        long now = System.currentTimeMillis();

        // reset counter if new millisecond
        if (now != lastTimestamp) {
            lastTimestamp = now;
            COUNTER.set(0);
        }

        return now * 1000 + COUNTER.incrementAndGet();
    }

}