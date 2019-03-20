import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.hse.spb.interpreter.Interpreter;

import java.io.PrintStream;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/beans.xml");
        Interpreter interpreter = (Interpreter) context.getBean("Interpreter");
        interpreter.run();
    }
}
