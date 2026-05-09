package effectivejava.chapter4.item24;

public class Test {
    public static void main(String[] args) {
//        OuterSmart.Helper h = new OuterSmart.Helper();
//        h.help();

        Outer outer = new Outer();
        Outer.Inner inner = outer.new Inner();
        inner.print();
    }
}
