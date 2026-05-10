package br.com.leonardoz.features.collections;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentMap {

    static Map<String, Integer> map = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                map.merge("key", 1, Integer::sum);
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        Thread t3 = new Thread(task);

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println("Final value (ConcurrentHashMap): " + map.get("key"));
    }
}