package effectivejava.chapter11.item80;

import java.util.concurrent.*;

public class Test {

    public static void main(String[] args) throws Exception {

        // =====================================================
        // ❌ 1. BAD: Raw Thread creation
//        Thread = expensive + unmanaged + manual control
        // =====================================================
//        Thread t1 = new Thread(() -> {
//            System.out.println("❌ Thread 1: " + doWork(1));
//        });
//
//        Thread t2 = new Thread(() -> {
//            System.out.println("❌ Thread 2: " + doWork(2));
//        });
//
//        t1.start();
//        t2.start();
//
//        t1.join();
//        t2.join();

//
//        // =====================================================
//        // ✔ 2. GOOD: ExecutorService (thread pool)
////        Thread pool = reusable workers + controlled concurrency
//        // =====================================================
//        ExecutorService executor = Executors.newFixedThreadPool(2);
//
//        executor.execute(() -> {
//            System.out.println("✔ Executor Task 1: " + doWork(3));
//        });
//
//        executor.execute(() -> {
//            System.out.println("✔ Executor Task 2: " + doWork(4));
//        });
//
//        executor.shutdown();
//        executor.awaitTermination(5, TimeUnit.SECONDS);
//
//
//        // =====================================================
//        // ✔ 3. BETTER: ExecutorService with Future (return value)
//        // =====================================================
//        ExecutorService executor2 = Executors.newFixedThreadPool(2);
//
//        Future<Integer> future1 = executor2.submit(() -> doWork(5));
//        Future<Integer> future2 = executor2.submit(() -> doWork(6));
//
//        System.out.println("✔ Future result 1: " + future1.get());
//        System.out.println("✔ Future result 2: " + future2.get());
//
//        executor2.shutdown();


        // =====================================================
        // ✔ 4. BEST: CompletableFuture (async pipeline)
//        non-blocking pipeline + functional chaining
        // =====================================================
        CompletableFuture<Integer> cf =
                CompletableFuture.supplyAsync(() -> doWork(7))
                        .thenApply(result -> result * 2)
                        .thenApply(result -> result + 10);

        System.out.println("✔ CompletableFuture result: " + cf.get());
    }

    // Simulated workload
    static int doWork(int x) {
        try {
            Thread.sleep(500); // simulate heavy work
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return x * 10;
    }
}