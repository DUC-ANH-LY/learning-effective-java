package br.com.leonardoz.features.locks;

public class WithOutSyncReentancy {

    static class MyLock {

        private boolean locked = false;

        public synchronized void lock()
                throws InterruptedException {
//          deadlock
            while (locked) {
                wait();
            }

            locked = true;
        }

        public synchronized void unlock() {

            locked = false;

            notify();
        }
    }

    private final MyLock lock = new MyLock();

    public void methodA() throws InterruptedException {

        lock.lock();

        System.out.println(
                Thread.currentThread().getName()
                        + " entered methodA"
        );

        // Calls another method needing same lock
        methodB();

        lock.unlock();
    }

    public void methodB() throws InterruptedException {

        System.out.println(
                Thread.currentThread().getName()
                        + " trying methodB"
        );

        // DEADLOCK HERE
        lock.lock();

        System.out.println(
                Thread.currentThread().getName()
                        + " entered methodB"
        );

        lock.unlock();
    }

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(3000);

        WithOutSyncReentancy example =
                new WithOutSyncReentancy();

        Thread t = new Thread(() -> {

            try {
                example.methodA();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t.start();
    }
}
