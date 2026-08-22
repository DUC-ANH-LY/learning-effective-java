class SharedData {
    volatile boolean flag = false;
}

class MyThread extends Thread {
    SharedData data;

    MyThread(SharedData data) {
        this.data = data;
    }

    public void run() {
        while (!data.flag) {
            // waiting
        }
        System.out.println("Flag changed!");
    }
}

public class VolatileExample {
    public static void main(String[] args) throws Exception {
        SharedData data = new SharedData();
        MyThread t = new MyThread(data);
        t.start();

        Thread.sleep(1000);
        data.flag = true;
    }
}