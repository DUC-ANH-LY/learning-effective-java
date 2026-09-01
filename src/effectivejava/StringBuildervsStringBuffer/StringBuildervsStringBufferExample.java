package effectivejava.StringBuildervsStringBuffer;

public class StringBuildervsStringBufferExample {
    public static void main(String[] args) {
        // --- StringBuffer (Thread-Safe, but slightly slower) (multithread) ---
        // Use this when multiple threads might be modifying the string at the same time.
        // synchronized internal
        StringBuffer buffer = new StringBuffer("Hello");
        buffer.append(" World");
        System.out.println("StringBuffer result: " + buffer);

        // --- StringBuilder (Not Thread-Safe, but faster) ---
        // Use this for almost everything else (single-threaded operations).
        // not sync 
        StringBuilder builder = new StringBuilder("Hello");
        builder.append(" World");
        System.out.println("StringBuilder result: " + builder);
    }
}
