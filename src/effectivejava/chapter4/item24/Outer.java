package effectivejava.chapter4.item24;

class Outer {
    private int value = 10;

    class Inner {
        void print() {
            System.out.println(value);
        }
    }
}