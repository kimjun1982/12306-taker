package defaultpackage;

import org.openqa.selenium.WebDriver;
import ticketquery.httprequest.HttpRequestThread;
import ticketquery.selenium.DriverFactory;
import zlocalfiles.NetworkBusyHandler;
import ticketquery.selenium.SeleniumThread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LegacyRunner {

    public static BlockingQueue<Integer> blockingQueue = new LinkedBlockingQueue<>(1);

    public static void main(String args[]) throws Exception {
        WebDriver driver = DriverFactory.getDriver();

        Thread seleniumThread = new Thread(new SeleniumThread());

        Thread httpRequestThread = new Thread(new HttpRequestThread(blockingQueue));
        Thread networkBusyChecker = new Thread(new NetworkBusyHandler(driver));

//        httpRequestThread.start();
        seleniumThread.start();
        networkBusyChecker.start();
    }
}
