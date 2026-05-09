package effectivejava.chapter11.item81;

class Shared {

    private int data;
    private boolean hasData = false;
    private final Object lock = new Object();

    private void printState(Thread t, String msg) {
        System.out.println(
                System.currentTimeMillis()
                        + " | " + t.getName()
                        + " | STATE=" + t.getState()
                        + " | " + msg
        );
    }

    // Producer
    public void produce(int value, Thread t) throws InterruptedException {
        synchronized (lock) {

            printState(t, "entered produce");

            while (hasData) {
                printState(t, "WAITING (buffer full)");
                lock.wait();
            }

            data = value;
            hasData = true;

            printState(t, "produced " + value);

            lock.notify();
            printState(t, "notify consumer");
        }
    }

    // Consumer
    public void consume(Thread t) throws InterruptedException {
        synchronized (lock) {

            printState(t, "entered consume");

            while (!hasData) {
                printState(t, "WAITING (buffer empty)");
                lock.wait();
            }

            printState(t, "consumed " + data);

            hasData = false;

            lock.notify();
            printState(t, "notify producer");
        }
    }
}

public class WaitNoti {

    public static void main(String[] args) {

        Shared shared = new Shared();

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try {
                    shared.produce(i, Thread.currentThread());
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "PRODUCER");

        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try {
                    shared.consume(Thread.currentThread());
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "CONSUMER");

        producer.start();
        consumer.start();
    }
}