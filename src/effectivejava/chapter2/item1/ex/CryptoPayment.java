package effectivejava.chapter2.item1.ex;


class CryptoPayment implements Payment {

    @Override
    public void pay() {
        System.out.println("Paid with Crypto");
    }
}
