package effectivejava.chapter2.item3.enumtype;

public class Main {
    // This code would normally appear outside the class!
    public static void main(String[] args) {
        Elvis elvis = Elvis.INSTANCE;
        elvis.setDuration(123);
        System.out.println(elvis.getDuration());
        System.out.println(elvis.INSTANCE.toString());
        elvis.leaveTheBuilding();

        System.out.println(TrafficLight.RED.getDuration());
        System.out.println(TrafficLight.RED.getTest());
        System.out.println(TrafficLight.GREEN.getTest());

    }
}
