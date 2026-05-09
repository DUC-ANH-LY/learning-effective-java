package effectivejava.garbagecollector;

class Person {

    private String name;

    public Person(String name) {
        this.name = name;
    }
}

public class Garbage {

    public static void main(String[] args)
            throws Exception {

        Runtime runtime = Runtime.getRuntime();

        System.out.println("Before object:");
        printMemory(runtime);

        Person p1 = new Person("Alice");
        System.out.println("After object creation:");
        printMemory(runtime);

        System.gc();

        Thread.sleep(2000);

        System.out.println("After GC:");
        printMemory(runtime);

        // Keep object alive
        System.out.println(p1);
    }

    static void printMemory(Runtime runtime) {

        long used =
                runtime.totalMemory()
                        - runtime.freeMemory();

        System.out.println(
                "Used memory: "
                        + used / 1024
                        + " KB");
    }
}