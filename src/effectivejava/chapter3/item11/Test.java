package effectivejava.chapter3.item11;


import java.util.HashSet;


class Person {

    String name;

    Person(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (!(o instanceof Person))
            return false;

        Person person = (Person) o;

        return name.equals(person.name);
    }


//    solve
    @Override
    public int hashCode() {

        int result = 17;

        result = 31 * result + name.hashCode();

        return result;
    }
}

public class Test {
    public static void main(String[] args) {
        HashSet<Person> set = new HashSet<>();

        Person p1 = new Person("John");
        System.out.println(p1.hashCode());
        set.add(p1);


        // false
//       if not overide hashcode it inherted from Object class with different hashcode each object
//        new Person("John") herer have diff hashcode compare to p1 then it will false because the Person not overide the hashCode function
//       hash collections first check hashcode then compare equals
        System.out.println(
                set.contains(new Person("John"))
        );
    }
}
