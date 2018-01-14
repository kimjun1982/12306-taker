package defaultpackage;


import org.openqa.selenium.WebDriver;
import ticketquery.selenium.DriverFactory;
import ticketquery.selenium.SeleniumThread;

public class SeleniumRunner {
    public static void main(String args[]) throws Exception {
        WebDriver driver = DriverFactory.getDriver();
        TravelInfo travelInfo = TravelInfo.getTravelInfo();

        Thread seleniumThread = new Thread(new SeleniumThread(driver, travelInfo));
        seleniumThread.start();
    }
}
