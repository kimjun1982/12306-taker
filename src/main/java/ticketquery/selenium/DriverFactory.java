package ticketquery.selenium;

import defaultpackage.Config;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DriverFactory {
    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);

    private static WebDriver driver;

    public static WebDriver getDriver() {
        if (driver == null) {
            synchronized (DriverFactory.class) {
                if (driver == null) {
                    log.info("Chrome Driver Path: {}", Config.pathToChromeDriver);
                    System.setProperty("webdriver.chrome.driver", Config.pathToChromeDriver);
                    File driverExeFile = new File(Config.pathToChromeDriver);
                    if (!driverExeFile.canExecute()) {
                        driverExeFile.setExecutable(true);
                    }

                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--test-type");
                    chromeOptions.setBinary(Config.pathToChromeBrowser);

                    driver = new ChromeDriver(chromeOptions);
                }
            }
        }
        return driver;
    }
}
