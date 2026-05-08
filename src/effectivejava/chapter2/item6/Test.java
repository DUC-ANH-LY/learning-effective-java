package effectivejava.chapter2.item6;

public class Test {
    public static void main(String[] args) throws InterruptedException {

        while (true) {
            Thread.sleep(20000);

            for (int i = 0;i<= 10000000;i++) {
                System.out.println("yeah");
//              2.53 mb String only
                String s = new String("bikini");

                // just cache no heap required fully main function 2.18mb
//              String s = "bikini";
            }
            System.out.println("Application running...");
            Thread.sleep(100000);
            System.out.println("Done");
        }

//
//        for (int i = 0;i<= 1000;i++) {
//            String s = "bikini";
//        }
    }
}
