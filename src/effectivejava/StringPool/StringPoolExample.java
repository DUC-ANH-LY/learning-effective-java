package effectivejava.StringPool;

public class StringPoolExample {
    public static void main(String[] args) {
        // String literals are automatically interned (placed in the String pool)
        String s1 = "Hello";
        String s2 = "Hello";

        // s1 and s2 point to the same object in the String pool
        System.out.println("s1 == s2 : " + (s1 == s2)); // true

        // Creating a String using the 'new' keyword creates a new object in the heap space,
        // not in the String pool.
        String s3 = new String("Hello");
        System.out.println("s1 == s3 : " + (s1 == s3)); // false
        
        // We can use the intern() method to get the string from the pool 
        // (or add it to the pool if it's not already there)
        String s4 = s3.intern();
        System.out.println("s1 == s4 : " + (s1 == s4)); // true
    }
}
