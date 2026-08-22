package effectivejava.StringBuffervsString;

public class StringBuffervsStringExample {
    // 
    public static void main(String[] args) {
        // --- String (Immutable) ---
        String str = "Hello";
        str = str.concat(" World"); // This creates a new string, but we don't save it
        
        System.out.println("String result: " + str); // Still prints "Hello"


        // --- StringBuffer (Mutable) --- 
        // sizing len + 16 
        StringBuffer buffer = new StringBuffer("Hello");
        // append string len + current length  (current_len + 16) = a, if current_len < a, update new len (capacity)
        // then update by System.arraycopy using asci array byte to implemnt append character 
        buffer.append(" World"); // This modifies the original buffer
        
        System.out.println("StringBuffer result: " + buffer); // Prints "Hello World"
    }
}
