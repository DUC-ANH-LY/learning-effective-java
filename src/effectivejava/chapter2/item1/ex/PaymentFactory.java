package effectivejava.chapter2.item1.ex;

class PaymentFactory {

    static Payment getPayment(String className)
            throws Exception {


//      reflection
        Class<?> cls = Class.forName(className);

        return (Payment) cls
                .getDeclaredConstructor()
                .newInstance();
    }
}