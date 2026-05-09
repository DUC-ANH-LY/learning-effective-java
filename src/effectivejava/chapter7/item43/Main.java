package effectivejava.chapter7.item43;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.*;

public class Main {

    public static void main(String[] args) {

        List<Student> students = Arrays.asList(
                new Student("An", 85),
                new Student("Binh", 70),
                new Student("Cuong", 95),
                new Student("Dung", 60)
        );

        // 1. FILTER students with score >= 80
        List<Student> topStudents = students.stream()
                .filter(s -> s.getScore() >= 80)   // lambda
                .toList();

        // 2. SORT by score (METHOD REFERENCE STYLE MIXED WITH LAMBDA)
        List<Student> sorted = topStudents.stream()
                .sorted(Comparator.comparing(Student::getScore)) // method reference
                .toList();

        // 3. PRINT students (method reference)
        sorted.forEach(System.out::println);

        // 4. TRANSFORM names to uppercase (method reference)
        List<String> names = students.stream()
                .map(Student::getName)   // method reference
                .map(String::toUpperCase)
                .toList();

        System.out.println(names);

        // 5. CONSTRUCTOR REFERENCE example
        Supplier<List<Student>> supplier = ArrayList::new;

        List<Student> newList = supplier.get();
        newList.add(new Student("Test", 100));

        System.out.println(newList);
    }
}