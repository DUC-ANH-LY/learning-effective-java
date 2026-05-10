package br.com.leonardoz.features.synchronizers;

import java.util.concurrent.Semaphore;

public class SemophoresRaceCondition {


    static int counter = 0;

    static Semaphore semaphore =
            new Semaphore(3);

    public static void main(String[] args) {

        Runnable r = () -> {

            try {

                semaphore.acquire();

                for (int i = 0; i < 10000; i++) {

                    counter++;
                }
                System.out.println(counter);

            } catch (Exception e) {

            } finally {

                semaphore.release();
            }
        };

        for (int i = 0; i < 3; i++) {

            new Thread(r).start();
        }
    }
}
