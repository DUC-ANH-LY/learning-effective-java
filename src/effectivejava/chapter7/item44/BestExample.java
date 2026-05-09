package effectivejava.chapter7.item44;

import java.util.function.Function;

public class BestExample
{
    public static void main(String[] args) {

        Function<String, Integer> f = String::length;

        System.out.println(f.apply("hello")); // 5
    }
}