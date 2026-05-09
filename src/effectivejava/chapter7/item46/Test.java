package effectivejava.chapter7.item46;

import java.util.*;

public class Test {
    public static void main(String[] args) {

        List<String> list = Arrays.asList("a", "bb", "ccc");

        List<String> result = new ArrayList<>();

        list.stream()
                .filter(s -> {
                    System.out.println("Filtering: " + s); // ❌ side effect
                    return s.length() > 1;
                })
                .forEach(s -> result.add(s)); // ❌ modifying external list
    }
}
