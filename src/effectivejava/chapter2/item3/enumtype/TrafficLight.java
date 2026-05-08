package effectivejava.chapter2.item3.enumtype;

public enum TrafficLight {
    RED(30, "test456"),
    GREEN(45, "test123");

    private final int duration;
    private final String test;
    TrafficLight(int duration, String test) {
        this.duration = duration;
        this.test = test;
    }

    public int getDuration() {
        return duration;
    }

    public String getTest() {
        return test;
    }
}