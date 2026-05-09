package effectivejava.chapter8.item63;

import java.util.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        Thread.sleep(10000);
        List<String> words = Arrays.asList(
                "Java", "is", "very", "powerful", "but", "you", "must", "use", "it", "correctly"
        );

        // =========================================
        // ❌ 1. BAD: String concatenation in loop
        // =========================================
        long start1 = System.currentTimeMillis();

        String result1 = "";
        for (String w : words) {
            result1 += w + " "; // ❌ creates many String objects
        }

        long end1 = System.currentTimeMillis();

        System.out.println("❌ String (+) result:");
        System.out.println(result1);
        System.out.println("Time: " + (end1 - start1) + " ms\n");


        // =========================================
        // ✔ 2. GOOD: StringBuilder (recommended)
        // =========================================
        long start2 = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();

        for (String w : words) {
            sb.append(w).append(" ");
        }

        String result2 = sb.toString();

        long end2 = System.currentTimeMillis();

        System.out.println("✔ StringBuilder result:");
        System.out.println(result2);
        System.out.println("Time: " + (end2 - start2) + " ms\n");


//        // =========================================
//        // ✔ 3. GOOD: String.join()
//        // =========================================
//        long start3 = System.currentTimeMillis();
//
//        String result3 = String.join(" ", words);
//
//        long end3 = System.currentTimeMillis();
//
//        System.out.println("✔ String.join() result:");
//        System.out.println(result3);
//        System.out.println("Time: " + (end3 - start3) + " ms\n");
//
//
//        // =========================================
//        // ✔ 4. GOOD: Stream Collectors.joining()
//        // =========================================
//        long start4 = System.currentTimeMillis();
//
//        String result4 = words.stream()
//                .collect(Collectors.joining(" "));
//
//        long end4 = System.currentTimeMillis();
//
//        System.out.println("✔ Stream joining result:");
//        System.out.println(result4);
//        System.out.println("Time: " + (end4 - start4) + " ms\n");
    }
}