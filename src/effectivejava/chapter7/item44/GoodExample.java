package effectivejava.chapter7.item44;

import java.util.function.Function;

public class GoodExample {
    public static void main(String[] args) {

        // standard functional interface
        Function<String, Integer> f = s -> s.length();

        System.out.println(f.apply("hello")); // 5
    }
}