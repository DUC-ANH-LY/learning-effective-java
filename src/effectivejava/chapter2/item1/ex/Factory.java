package effectivejava.chapter2.item1.ex;


public class Factory {

    public static void main(String[] args) {

        try {

            FakeFactory p3 = (FakeFactory) FakeFactory.getPayment("effectivejava.chapter2.item1.ex.PaypalPayment");

            Payment p1 =
                    PaymentFactory.getPayment(
                            "effectivejava.chapter2.item1.ex.PaypalPayment");

            Payment p2 =
                    PaymentFactory.getPayment(
                            "effectivejava.chapter2.item1.ex.CryptoPayment");

            p1.pay();
            p2.pay();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}