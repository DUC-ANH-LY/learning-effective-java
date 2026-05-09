package effectivejava.chapter7.item43;

class Student {
    String name;
    int score;

    Student(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public String toString() {
        return name + " (" + score + ")";
    }

    public int hihi() {
        System.out.println("wtf");
        return 1;
    }
}