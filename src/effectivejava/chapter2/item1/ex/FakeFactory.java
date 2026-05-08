package effectivejava.chapter2.item1.ex;

public class FakeFactory extends PaymentFactory {
    public static Payment getPayment(String className) {
        System.out.println(className);
        return null;
    }
}
