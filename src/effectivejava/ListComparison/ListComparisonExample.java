package effectivejava.ListComparison;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListComparisonExample {
    public static void main(String[] args) {
        
        // =========================================================
        // 1. ArrayList
        // fixed array with x1.5 capacity (perf growth)

        // oldCapacity = 10
        // minCapacity = 15

        // requiredGrowth  = 5
        // preferredGrowth = 5

        // newCapacity ≈ 15




        // oldCapacity = 10
        // minCapacity = 12
        // requiredGrowth  = 2
        // preferredGrowth = 5
        // newCapacity = 15

        List<String> arrayList = new ArrayList<>();
        arrayList.add("Apple");
        arrayList.add("Banana");
        arrayList.add("Ch");
        
        // Instant access because it knows exactly where index 1 is in memory
        System.out.println("ArrayList access (Fast): " + arrayList.get(1)); 


        // =========================================================
        // 2. LinkedList
        // =========================================================
        // Under the hood: Uses "nodes" scattered in memory that point to each other like a chain.
        // PRO: Very FAST at adding/removing data in the beginning or middle
        //      (it just changes where the chain links point, no shifting required).
        // CON: SLOW at reading data (e.g., getting the 5th item means it has to 
        //      start at the 1st item and follow the chain all the way down).
        List<String> linkedList = new LinkedList<>();
        linkedList.add("Apple");
        linkedList.add("Banana");
        linkedList.add("Cherry");
        
        // Adding to the very beginning. 
        // If we did this in an ArrayList, Banana and Cherry would have to shift positions.
        // In a LinkedList, it just creates Mango and points it to Apple. Much faster!
        linkedList.add(0, "Mango"); 
        
        System.out.println("LinkedList after fast insertion at front: " + linkedList);
    }
}
