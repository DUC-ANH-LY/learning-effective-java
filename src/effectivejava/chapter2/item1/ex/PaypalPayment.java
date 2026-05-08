package effectivejava.chapter2.item1.ex;

class PaypalPayment implements Payment {

    @Override
    public void pay() {
        System.out.println("Paid with PayPal");
    }
}