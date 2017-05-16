package ticketquery.selenium;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SeleniumUtil {
    private static final Logger log = LoggerFactory.getLogger(SeleniumUtil.class);

    public static void sleepHalfSecs(int halfSec) {
        try {
            Thread.sleep(halfSec * 500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WebElement waitUntilClickableThenClick(WebDriver driver, WebElement element) {
        WebElement webElement;
        for (int i = 0; i <= 6; i++) {
            try {
                webElement = new WebDriverWait(driver, 30).until(ExpectedConditions.elementToBeClickable(element));
                Thread.sleep(10);
                webElement.getText(); // Use getText method to test if such element is stale or not
                webElement.click();
                return webElement;
            } catch (Throwable t) {
                System.out.println("Failed in attempt No. " + i);
                sleepHalfSecs(i * 2);
            }
        }
        throw new RuntimeException("Element state is unknown or cannot find such element");
    }

    public static String waitUntilVisibleThenGetText(WebDriver driver, WebElement element) {
        WebElement webElement;
        for (int i = 0; i <= 6; i++) {
            try {
                webElement = new WebDriverWait(driver, 30).until(ExpectedConditions.visibilityOf(element));
                String text = webElement.getText();
                return text;
            } catch (Throwable t) {
                System.out.println("Failed in attempts No. " + i);
                sleepHalfSecs(i * 2);
            }
        }
        throw new RuntimeException("Element state is unknown or cannot find such element");
    }

    public static void waitUntilClickableThenSentKeys(WebDriver driver, WebElement element, String textToSend) {
        WebElement webElement;
        for (int i = 0; i <= 6; i++) {
            try {
                webElement = new WebDriverWait(driver, 30).until(ExpectedConditions.elementToBeClickable(element));
                webElement.clear();
                Thread.sleep(10);
                webElement.sendKeys(textToSend);
                return;
            } catch (Throwable t) {
                System.out.println("Failed in attempts No. " + i);
                sleepHalfSecs(i * 2);
            }
        }
        throw new RuntimeException("Element state is unknown or cannot find such element");
    }

    public static WebElement waitUtilVisible(WebDriver driver, WebElement element) {
        WebElement webElement;
        for (int i = 0; i <= 6; i++) {
            try {
                webElement = new WebDriverWait(driver, 30).until(ExpectedConditions.visibilityOf(element));
                webElement.getText(); // Use getText method to test if such element is stale or not
                return webElement;
            } catch (Throwable t) {
                System.out.println("Failed in attempts No. " + i);
                sleepHalfSecs(i * 2);

            }
        }
        throw new RuntimeException("Element state is unknown or cannot find such element");
    }

    public static WebElement waitUtilClickable(WebDriver driver, WebElement element) {
        WebElement webElement;
        for (int i = 0; i <= 6; i++) {
            try {
                webElement = new WebDriverWait(driver, 30).until(ExpectedConditions.elementToBeClickable(element));
                webElement.getText(); // Use getText method to test if such element is stale or not
                return webElement;
            } catch (Throwable t) {
                System.out.println("Failed in attempts No. " + i);
                sleepHalfSecs(i * 2);

            }
        }
        throw new RuntimeException("Element state is unknown or cannot find such element");
    }

    public static String waitUntilAnyInListVisibleThenGetText(WebDriver driver, List<WebElement> elements,
                                                              int... targetIndex) {
        WebElement targetElement = null;
        int size = elements.size();
        if (targetIndex.length == 0) {
            for (int j = 0; j < size; j++) {
                if (elements.get(j).isDisplayed()) {
                    targetElement = elements.get(j);
                    break;
                }
                if (j == size - 1) {
                    System.out.println("No element is the list is visible");
                    return null;
                }
            }
        } else {
            targetElement = elements.get(targetIndex[0] - 1); // Parameter index counting from 1
        }

        for (int i = 0; i < 6; i++) {
            try {
                targetElement = new WebDriverWait(driver, 30).until(ExpectedConditions.visibilityOf(targetElement));
                String text = targetElement.getText();
                return text;
            } catch (Throwable t) {
                System.out.println("Failed in attempts No. " + i);
                sleepHalfSecs(i * 2);

            }
        }
        throw new RuntimeException("Have tried several times, but failed as element state is unknown");

    }

    public static WebElement waitUntilAnyInListVisibleThenSendKeys(WebDriver driver, List<WebElement> elements,
                                                                   String keysToSend, int... targetIndex) {
        WebElement targetElement = null;
        int size = elements.size();
        if (targetIndex.length == 0) {
            for (int j = 0; j < size; j++) {
                if (elements.get(j).isDisplayed()) {
                    targetElement = elements.get(j);
                    break;
                }
                if (j == size - 1) {
                    System.out.println("No element is the list is visible");
                    return null;
                }
            }
        } else {
            targetElement = elements.get(targetIndex[0] - 1); // Parameter index counting from 1
        }

        for (int i = 0; i < 6; i++) {
            try {
                targetElement = new WebDriverWait(driver, 30).until(ExpectedConditions.visibilityOf(targetElement));
                targetElement.clear();
                Thread.sleep(10);
                targetElement.sendKeys(keysToSend);
                return targetElement;
            } catch (Throwable tt) {
                System.out.println("Failed in attempts No. " + i);
                sleepHalfSecs(i * 2);

            }
        }
        throw new RuntimeException("Have tried several times, but failed as element state is unknown");
    }

    public boolean waitAfterPageLoad(WebDriver driver) {
        new WebDriverWait(driver, 30).until(pageLoadedFunction());
        return true;

    }

    public boolean waitTextValueNotDisplay(WebDriver driver, WebElement element, String valuesGetNotDisplay) {
        boolean result;
        for (int i = 0; i <= 6; i++) {
            try {

                ExpectedCondition<Boolean> negativeCondition = ExpectedConditions
                        .not(ExpectedConditions.textToBePresentInElement(element, valuesGetNotDisplay));
                result = new WebDriverWait(driver, 30).until(negativeCondition);
                return result;
            } catch (Throwable t) {
                System.out.println("Failed in attempts No. " + i);
                sleepHalfSecs(i * 2);

            }
        }
        throw new RuntimeException("Timeouted while waiting element value to changes");
    }

    public boolean waitUtilAttributeGetExpectedValue(WebDriver driver, WebElement element, String attrNameToWait,
                                                     String expectedAttrValue) {
        boolean result;
        for (int i = 0; i <= 6; i++) {
            try {
                result = new WebDriverWait(driver, 30)
                        .until(waitAttributeValueCondition(driver, element, attrNameToWait, expectedAttrValue));
                return result;
            } catch (Throwable t) {
                System.out.println("Failed in attempts No. " + i);
                sleepHalfSecs(i * 2);

            }
        }
        throw new RuntimeException("Attribute of the waiting element does't equal to expected value before timeouted");
    }

    public static Function<WebDriver, Boolean> pageLoadedFunction() {
        return new Function<WebDriver, Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        };
    }

    public static Predicate<WebDriver> pageLoadedPredicate() {
        return new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        };
    }

    public static ExpectedCondition<Boolean> pageLoadedExpectedCondition() {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        };
    }

    public static ExpectedCondition<Boolean> waitAttributeValueCondition(WebDriver driver, final WebElement element,
                                                                         final String attrNameToWait, final String expectedAttrValue) {

        return new ExpectedCondition<Boolean>() {
            String actualAttrValue = null;

            @Override
            public Boolean apply(WebDriver input) {
                actualAttrValue = element.getAttribute(attrNameToWait);
                if (actualAttrValue == null && expectedAttrValue == null)
                    return true;
                else {
                    return actualAttrValue != null && expectedAttrValue != null && attrNameToWait.equals(expectedAttrValue);
                }

            }
        };
    }
}
