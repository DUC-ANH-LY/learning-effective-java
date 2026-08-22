package effectivejava.MultipleInheritance;

public class MultipleInheritanceExample {
    
    // ==============================================================================
    // 1. WHY CLASSES CAN ONLY EXTEND ONE CLASS (The "Diamond Problem")
    // ==============================================================================
    /* 
    Imagine if Java allowed a class to extend multiple classes:
    
    class Machine { 
        void start() { System.out.println("Machine starting with gears..."); } 
    }
    
    class Computer { 
        void start() { System.out.println("Computer starting with electricity..."); } 
    }
    
    // ILLEGAL IN JAVA!
    class Cyborg extends Machine, Computer { 
        public void start() {
            super.start();  <------------------- can not resolve which one ?????????????
            System.out.println("Cyborg starting with both gears and electricity...");
        }
    } 

    If you created a Cyborg and called `myCyborg.start()`, Java would be completely 
    confused. Which `start()` method should it run? The one from Machine or the 
    one from Computer? To prevent this ambiguity, Java forces you to extend only 1 class.
    */


    // ==============================================================================
    // 2. WHY CLASSES CAN IMPLEMENT MULTIPLE INTERFACES
    // ==============================================================================
    
    interface Flyer {
        void move(); // No implementation body. Just a contract saying "you must move"
    }

    interface Swimmer {
        void move(); // No implementation body here either.
    }

    // This is perfectly legal! 
    // Because neither interface actually provides the code for *how* to move, 
    // there is no conflict. The Duck class provides the single, unambiguous 
    // implementation for the move() method.
    static class Duck implements Flyer, Swimmer {
        @Override
        public void move() {
            System.out.println("The duck implements move() exactly once!");
        }
    }

    public static void main(String[] args) {
        System.out.println("Testing our Duck that implements two interfaces:");
        
        Duck donald = new Duck();
        donald.move();
        
        // It can be treated as either interface
        Flyer flyingDuck = donald;
        Swimmer swimmingDuck = donald;
        
        // Both point to the exact same implementation
        System.out.println("Is flyingDuck the same object as swimmingDuck? " + (flyingDuck == swimmingDuck));
    }
}
