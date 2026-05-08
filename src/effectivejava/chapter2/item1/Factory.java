package effectivejava.chapter2.item1;

interface Payment {
    void pay();
}

// --------------------------------------------

class PaypalPayment implements Payment {

    @Override
    public void pay() {
        System.out.println("Paid with PayPal");
    }
}

// --------------------------------------------

class CryptoPayment implements Payment {

    @Override
    public void pay() {
        System.out.println("Paid with Crypto");
    }
}

// --------------------------------------------

class PaymentFactory {

    public static Payment getPayment(String className)
            throws Exception {

        Class<?> cls = Class.forName(className);

        return (Payment) cls
                .getDeclaredConstructor()
                .newInstance();
    }
}

// --------------------------------------------

public class Factory {

    public static void main(String[] args) {

        try {

            Payment p1 =
                    PaymentFactory.getPayment(
                            "effectivejava.chapter2.item1.PaypalPayment");

            Payment p2 =
                    PaymentFactory.getPayment(
                            "effectivejava.chapter2.item1.CryptoPayment");

            p1.pay();
            p2.pay();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}