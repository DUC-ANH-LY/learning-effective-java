package br.com.leonardoz.features.synchronizers;

import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Semaphores controls the number of activities that can access a resource or
 * perform a certain action;
 * <p>
 * - First you give a number of 'permits';
 * <p>
 * - Activities will acquire it and release when they're done;
 * <p>
 * - If none is available, activity will block until one become available.
 * <p>
 * Good for resource pools.
 */
public class UsingSemaphores {
    static int i = 0;

    public static void main(String[] args) {

		var executor = Executors.newCachedThreadPool();
//		a thread only acquire max 3 permit to run can access a resource by waiting queue for locking
//		var semaphore = new Semaphore(3);
//
//		Runnable r = () -> {
//			try {
//				System.out.println("Trying to acquire - " + Thread.currentThread().getName());
//				if (semaphore.tryAcquire(2, TimeUnit.SECONDS)) {
//					// use-get resource
//					// simulate work in progress
//					i += 1;
//					System.out.println("Acquired - " + Thread.currentThread().getName());
//					Thread.sleep(2000);
//					System.out.println("Done - " + Thread.currentThread().getName());
//				}
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			} finally {
//				semaphore.release();
//				System.out.println(i);
//			}
//		};
//		for (int i = 0; i < 4; i++) {
//			executor.execute(r);
//		}
//
//		executor.shutdown();
//
//


        Semaphore semaphore = new Semaphore(2);
        Runnable task = () -> {
            try {
                // nothing happened
                semaphore.acquire(3);
                System.out.println(Thread.currentThread().getName() + " đã vào");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
//                semaphore.release(4);
                System.out.println(Thread.currentThread().getName() + " đã ra");
            }
        };
        new Thread(task, "Luồng-1").start();
        new Thread(task, "Luồng-2").start();
        new Thread(task, "Luồng-3").start();

    }
}
