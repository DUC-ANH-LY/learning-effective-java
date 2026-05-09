# Effective Java, Third Edition
![EJ3e Book Cover](https://www.pearsonhighered.com/assets/bigcovers/0/1/3/4/0134685997.jpg)
## Hot News! Source code finally available on GitHub. Happy Hacking!
- Item 1: static Factory instead of constructor
    - they have name (maintanable) 
    - can cache ( don't require to create new object each time -- like singleton) 
    - return an object of any subtype of their return type
      - [ex](src/effectivejava/chapter2/item1/Factory1.java)
    - static factories is that the class of the returned
      object can vary from call to call as a function of the input parameters
      - [ex](src/effectivejava/chapter2/item1/Factory1.java)
    - A fifth advantage of static factories is that the class of the returned object
      need not exist when the class containing the method is written. 
      - [ex](src/effectivejava/chapter2/item1/ex) -- some [reflection](https://viblo.asia/p/javahuong-dan-java-reflection-djeZ1bMglWz) required
    - **Limitation**:
      - something 

- Item 2: Builder pattern
    - solve constructor with too many params (maintainable, hard to read)  
    - we can use set through app but it not thread-safe (multi thread can access to this and cause race condition -- can give field as final to solve)
    - required and optional need too much overload to implemented 
      - ![img_2.png](img_2.png)
- Item 3: Enforce the singleton property with a privaet constructor or an enum type
  - Making a class a singleton can make it difficult to test its clients
- Item 4: create private constructor over abstract class if not wanted inheritanced
- Item 5: prefer depenedeny injection (spring container, bean)
- Item 6: Avoid creating unnessary object 
  - String heap 
    - [ex](src/effectivejava/chapter2/item6/Test.java)
    - ![img_3.png](img_3.png)
    - ![img_4.png](img_4.png) 
    - [ex](src/effectivejava/chapter2/item6/Sum.java)
- Item 7: Eliminate obsolete object reference 
- Garbage Collector
  - set up garbage log 
  - ![img_5.png](img_5.png)
  - ![img_7.png](img_7.png)
  - ![img_8.png](img_8.png)
  - divide to multi region for perfomane, gc not to scan entire 
  - ![img_9.png](img_9.png)
  - ![img_10.png](img_10.png)
  - Enterprise gc is too hard to read and learn with fast demoable
  - Read here for gc from scratch [may be easier approach](https://buildx.substack.com/p/lets-build-a-garbage-collector-gc)
- Item 8: not too considered
- Item 9: Prefer try with resource to try finally
- Item 10: Prefer overide equals 
  - default equals inherited from Object class with == (reference compare) internally
  - ![img_11.png](img_11.png)
- Item 11: Prefer overide hashCode, on hash collection, if not overide same value object can be have different hashCode as default Object class then overide it with actual object value with prime number for minized collision 
  - [ex](src/effectivejava/chapter3/item11/Test.java)
- Item 12: Prefer overide toString (easy)
- Item 13: Prefer clone via constructor or factory method
  - clone method just copy reference, not actual create the new one as expected, some mutable case is very careful
  - ![img_12.png](img_12.png)
  - ![img_13.png](img_13.png)
- Item 14: Implement Comparable for sort function
- 