package br.com.leonardoz.features.collections;

import java.util.concurrent.atomic.AtomicInteger;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicA {

    static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws Exception {

        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {

                int oldValue = counter.get();

                // 🔥 force race condition window
                try { Thread.sleep(1); } catch (Exception ignored) {}

                int newValue = oldValue + 1;

                boolean success = counter.compareAndSet(oldValue, newValue);

                System.out.println(Thread.currentThread().getName()
                        + " CAS(" + oldValue + " → " + newValue + ") = " + success);
            }
        };

        Thread t1 = new Thread(task, "T1");
        Thread t2 = new Thread(task, "T2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Final = " + counter.get());
    }
}