package br.com.leonardoz.features.locks;

import java.util.concurrent.locks.ReentrantLock;



// diff than synchronized is manually handle lock and unlock
public class ExplicitLock {


    private final ReentrantLock lock =
            new ReentrantLock();

    private int counter = 0;

    public void increment() {

        lock.lock();

        try {

            counter++;

            System.out.println(
                    Thread.currentThread().getName()
                            + " counter = " + counter
            );

        } finally {
//            System.out.println("hihi");
//  if not unlock t2  can not access and create dead lock
            lock.unlock();
        }
    }

    public static void main(String[] args)
            throws InterruptedException {

        ExplicitLock example =
                new ExplicitLock();

        Thread t1 = new Thread(example::increment, "T1");
        Thread t2 = new Thread(example::increment, "T2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }
}
