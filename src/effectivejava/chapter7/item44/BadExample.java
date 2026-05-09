package effectivejava.chapter7.item44;

interface StringToInt {
    int hihi(String s);
}

public class BadExample {
    public static void main(String[] args) {

        // custom functional interface usage
        StringToInt f = s -> s.length();

        System.out.println(f.hihi("hello")); // 5
    }
}