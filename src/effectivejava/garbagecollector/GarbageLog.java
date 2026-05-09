package effectivejava.garbagecollector;

import java.util.ArrayList;
import java.util.List;

public class GarbageLog {

    // Static list causes memory leak
    static List<byte[]> memoryLeak = new ArrayList<>();

    public static void main(String[] args)
            throws Exception {

        Runtime runtime = Runtime.getRuntime();
        int counter = 0;

        while (true) {

            // Create temporary objects
            for (int i = 0; i < 1000; i++) {

                byte[] temp =
                        new byte[1024 * 100]; // 100 KB

                // temp dies quickly
            }

            // Create long-lived object
            byte[] leak =
                    new byte[1024 * 1024]; // 1 MB

            memoryLeak.add(leak);

            counter++;

            printMemory(runtime, counter);

            Thread.sleep(500);
        }
    }

    static void printMemory(
            Runtime runtime,
            int counter) {

        long total =
                runtime.totalMemory()
                        / 1024 / 1024;

        long free =
                runtime.freeMemory()
                        / 1024 / 1024;

        long used = total - free;

        System.out.println(
                "Iteration: " + counter
                        + " | Used MB: " + used
                        + " | Free MB: " + free
                        + " | Total MB: " + total);
    }
}