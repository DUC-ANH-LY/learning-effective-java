package effectivejava.garbagecollector;

public class Test {

    public static void main(String[] args)
            throws Exception {

        Runtime runtime = Runtime.getRuntime();

        print(runtime);

        byte[] data = new byte[100 * 1024 * 1024]; //100mb

        print(runtime);

//        data = null;

//        why null ?
//        Because the object is still reachable.
//
//        Java Garbage Collector only deletes objects that cannot be reached anymore.

        System.gc();

        Thread.sleep(3000);

        print(runtime);
    }

    static void print(Runtime runtime) {

        long used =
                runtime.totalMemory()
                        - runtime.freeMemory();

        System.out.println(
                "Used MB: "
                        + used / 1024 / 1024);
    }
}