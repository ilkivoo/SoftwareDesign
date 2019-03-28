
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.hse.spb.interpreter.Interpreter;

public class Main {
    public static void main(String[] args) {
        PropertyConfigurator.configure("/Users/alyokhina-o/SoftwareDesign/01.CLI/src/main/resources/spring/log4j.xml");
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/beans.xml");
        Interpreter interpreter = (Interpreter) context.getBean("Interpreter");
        interpreter.run();
    }
}
