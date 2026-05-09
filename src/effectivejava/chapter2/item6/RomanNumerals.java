package effectivejava.chapter2.item6;
import java.util.regex.Pattern;

// Reusing expensive object for improved performance (Pages 22 and 23)
public class RomanNumerals {
    // Performance can be greatly improved! (Page 22)

//   when you go deep down to implementation you will see  public static Pattern compile(String regex) {
//        return new Pattern(regex, 0);
//    }
//    create again patttern each call -> exausted
     static boolean isRomanNumeralSlow(String s) {
        return s.matches("^(?=.)M*(C[MD]|D?C{0,3})"
                + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
    }

    // Reusing expensive object for improved performance (Page 23)
    private static final Pattern ROMAN = Pattern.compile(
            "^(?=.)M*(C[MD]|D?C{0,3})"
                    + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");

    static boolean isRomanNumeralFast(String s) {
        return ROMAN.matcher(s).matches();
    }

    public static void main(String[] args) throws InterruptedException {
        int numSets = 100;
        int numReps = 100;
        boolean b = false;
        Thread.sleep(10000);

        long start = System.nanoTime();
        for (int i = 0; i < numSets; i++) {
            for (int j = 0; j < numReps; j++) {
//                29.49 mb, 50ms
//                b ^= isRomanNumeralSlow("MCMLXXVI");  // Change Slow to Fast to see performance difference
//                profiler: 10ms  3.1mb
                b ^= isRomanNumeralFast("MCMLXXVI");  // Change Slow to Fast to see performance difference
            }
        }
        long end = System.nanoTime();
        System.out.println(((end - start) / (1_000. * numReps)) + " μs.");



        Thread.sleep(100000);
        // Prevents VM from optimizing away everything.
        if (!b)
            System.out.println();
    }
}

