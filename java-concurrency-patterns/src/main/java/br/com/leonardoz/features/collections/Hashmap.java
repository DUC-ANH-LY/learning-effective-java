package br.com.leonardoz.features.collections;

import java.util.HashMap;
import java.util.Map;

public class Hashmap {

    static Map<String, Integer> map = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                map.put("key", map.getOrDefault("key", 0) + 1);
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

        System.out.println("Final value (HashMap): " + map.get("key"));
    }
}