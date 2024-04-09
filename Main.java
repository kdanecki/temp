package org.example;

<< HEAD
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        MageRepository repository = new MageRepository();
        MageController controller = new MageController(repository);

        //assertEquals("done", controller.save("Gandalf", 20));
        //verify(repository).save(new Mage("Gandalf", 20));
        controller.save("Gandalf", 20);
==
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
> origin/master
    }
}
