interface Shape {
    void draw();
}

class Circle implements Shape {
    @Override
    public void draw() {
        System.out.println("Drawing Circle");
    }
}

class Square implements Shape {
    @Override
    public void draw() {
        System.out.println("Drawing Square");
    }
}

class ShapeFactory {

    // Static factory method
    public static Shape getShape(String type) {

        if (type.equalsIgnoreCase("circle")) {
            return new Circle();
        }

        if (type.equalsIgnoreCase("square")) {
            return new Square();
        }

        return null;
    }
}

public class Factory1 {
    public static void main(String[] args) {

        Shape s1 = ShapeFactory.getShape("circle");
        Shape s2 = ShapeFactory.getShape("square");

        s1.draw();
        s2.draw();
    }
}