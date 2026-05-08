package effectivejava.chapter2.item3.enumtype;

// Enum singleton - the preferred approach (Page 18)
public enum Elvis {
    INSTANCE("hihi");
    private int duration; // Not final, so it's mutable

    Elvis(String hihi) {
    }

    public void leaveTheBuilding() {
        System.out.println("Whoa baby, I'm outta here!");
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }
}
