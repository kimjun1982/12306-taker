package ticketquery.selenium;

import com.google.common.base.Stopwatch;
import model.QueryTicketRow;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class PageObject {
    private static final Logger log = LoggerFactory.getLogger(PageObject.class);

    @FindBy(css = ".i-hi")
    public WebElement indicatorForLogonSuccessfully;

    @FindBy(css = "#selectYuding a")
    public WebElement buttonToTicketReservationPage;

    @FindBy(css = "#normal_passenger_id")
    public WebElement indicatorOfPassengerListLoaded;

    @FindBy(css = "label[for^=\"normalPassenger\"]")
    public List<WebElement> fullListOfPassengers;

    @FindBy(css = "#fromStationText")
    public WebElement fromStationInputBox;

    @FindBy(css = "#toStationText")
    public WebElement toStationInputBox;

    @FindBy(css = "div[id^=\"citem_\"]")
    public List<WebElement> stationInputSuggestDropDown;

    @FindBy(css = "#date_icon_1")
    public WebElement travelDatePicker;

    @FindBy(css = "#train_date")
    public WebElement travelDateInputBox;

    @FindBy(css = "ifream[width=\"97\"]")
    public WebElement datePickerFrame;

    @FindBy(css = "input.yminput")
    public List<WebElement> monthYearFieldInDatePicker;

    @FindBy(className = "WdayTable")
    public List<WebElement> calendarTableInDatePicker;

    @FindBy(css = "td")
    public List<WebElement> dateCellsInDatePicker;

    @FindBy(css = "div[id^=\"prior_seat\"] > span > a")
    public WebElement seatClassSelectorInMoreOptions;

    @FindBy(css = "#qd_closeDefaultWarningWindowDialog_id")
    public WebElement networkBusyPopupWindow;

    @FindBy(css = "#cc_start_time")
    public WebElement departureTimeRangeOption;

    @FindBy(css = "tr[id^=ticket_]")
    public List<WebElement> listOfTrainReturnedByQuery;

    @FindBy(css = ".train[id^=\"ticket\"] div a")
    public List<WebElement> trainNumberElements;

    @FindBy(css = ".no-br")
    public List<WebElement> buttonsOfBookTicket;

    @FindBy(css = "div[id^=\"train_num\"]")
    public WebElement indicatorOfQueryResultReturned;

    @FindBy(css = "#query_ticket")
    public WebElement queryTicketBtn;

    @FindBy(css = "#submitOrder_id")
    public WebElement submitOrder;

    @FindBy(css = ".dhtmlx_window_active:not([style*=\"display: none;\"])")
    public WebElement confirmOrderPopup;

    @FindBy(css = ".dhtmlx_window_active:not([style*=\"display: none;\"]) #qr_submit_id")
    public WebElement confirmOrderBtn;

    @FindBy(css = "#payButton")
    public WebElement buttonLinkToPayment;


    private WebDriver driver;
    private static PageObject INSTANCE;

    public static PageObject getPageObj(WebDriver driver) {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        synchronized (PageObject.class) {
            if (INSTANCE == null) {
                INSTANCE = new PageObject(driver);
            }
        }
        return INSTANCE;
    }

    private PageObject(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void loadUrl(String url) {
        driver.get(url);
    }

    public void logonManually(int reservedTime) {
        WebElement goodLogin = null;
        while (true) {
            try {
                goodLogin = new WebDriverWait(driver, reservedTime)
                        .until(ExpectedConditions.visibilityOf(indicatorForLogonSuccessfully));
            } catch (Throwable ignored) {
            }

            if (goodLogin != null) {
                log.info("Logon successfully");
                break;
            } else {
                log.info(" Still logon FAILURE! please try again");
            }
        }
    }

    public void navigateToTicketReservationPage() {
        if (buttonToTicketReservationPage.isDisplayed()) {
            buttonToTicketReservationPage.click();
        } else {
            log.info("Button for ticket query doesn't display!");
        }
    }

    public void setFromToStation(String from, String to) {
        WebElement fromStation = (new WebDriverWait(driver, 30))
                .until(ExpectedConditions.visibilityOf(fromStationInputBox));
        fromStation.click();
        fromStation.sendKeys(from);

        List<WebElement> citySuggestList;
        citySuggestList = (new WebDriverWait(driver, 30))
                .until(ExpectedConditions.visibilityOfAllElements(stationInputSuggestDropDown));

        for (WebElement city : citySuggestList) {
            if (city.getText().contains(from)) {
                city.click();
                break;
            }
        }

        WebElement toStation = (new WebDriverWait(driver, 30))
                .until(ExpectedConditions.visibilityOf(toStationInputBox));
        toStation.click();
        toStation.sendKeys(to);

        citySuggestList = (new WebDriverWait(driver, 30))
                .until(ExpectedConditions.visibilityOfAllElements(stationInputSuggestDropDown));

        for (WebElement city : citySuggestList) {
            if (city.getText().contains(to)) {
                city.click();
                break;
            }
        }
    }

    public void setDepartureDate(String date) {
        WebElement travelDate = (new WebDriverWait(driver, 30)).until(ExpectedConditions.visibilityOf(travelDateInputBox));

        while (!travelDate.getAttribute("value").equals(date)) {
            String js = "var setDate=document.getElementById(\"train_date\"); setDate.removeAttribute('readonly');";
            ((JavascriptExecutor) driver).executeScript(js);
            travelDate.clear();
            travelDate.sendKeys(date);
            travelDate = (new WebDriverWait(driver, 30)).until(ExpectedConditions.visibilityOf(travelDateInputBox));
        }
        travelDate.click(); // so as to blur the pop up calendar
        travelDate.sendKeys(Keys.TAB);
    }

    public void waitUntilQueryButtonEnabled(int time) {
        new WebDriverWait(driver, time).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                try {
                    WebElement seatResult = driver.findElement(By.cssSelector("#sear-result"));
                    String style = seatResult.getAttribute("style");
                    return (null == style || "".equalsIgnoreCase(style) || style.contains("display: block;"));
                } catch (NoSuchElementException t) {
                    return true;
                }
            }
        });

        new WebDriverWait(driver, time).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return !(queryTicketBtn.getAttribute("class").contains("btn-disabled"));
            }
        });
    }

    private boolean isLoadingDisplay() {
        By loadingIconBy = By.cssSelector(".dhx_modal_cover");
        WebElement loadingIcon = driver.findElement(loadingIconBy);
        String style = loadingIcon.getAttribute("style").trim();
        return style.contains("display: inline-block;");
        //<div class="dhx_modal_cover" style="display: none;"></div>
        //<div class="dhx_modal_cover" style="display: inline-block;"></div>
    }

    public void simplyTicketQuery() {
        log.info("Simply do a single click on the ticket query button");
        SeleniumUtil.waitUntilClickableThenClick(driver, this.queryTicketBtn);
    }

    public List<QueryTicketRow> clickQueryAndParseTicketDtoList(int timeIntervalBetweenSeleniumQuery) {
        log.info("Selenium Query - clickQueryAndParseTicketDtoList:: Click query button and to check if tickets result returns");
        SeleniumUtil.waitUntilClickableThenClick(driver, queryTicketBtn);

        Stopwatch stopwatch = Stopwatch.createStarted();
        while (true) {
            try {
                while (isLoadingDisplay()) {
                    Thread.sleep(10);
                }

                List<QueryTicketRow> ticketRows = getTicketRowDtoList();
                if (ticketRows.size() > 0) {
                    log.info("Timer: clickQueryAndParseTicketDtoList=={} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
                    return ticketRows;
                } else {
                    log.info("No ticket query result, click query again");
                    Thread.sleep(timeIntervalBetweenSeleniumQuery);
                    SeleniumUtil.waitUntilClickableThenClick(driver, queryTicketBtn);
                }
            } catch (TicketQueryNotReturnException e) {
                e.printStackTrace();
            } catch (InterruptedException ignored) {
            }
        }
    }

    public List<String[]> clickQueryAndParseTrainNumSeatList(int timeIntervalBetweenSeleniumQuery) {
        log.info("Selenium Query - clickQueryAndParseTrainNumSeatList:: Click query button and to check if tickets result returns");
        SeleniumUtil.waitUntilClickableThenClick(driver, queryTicketBtn);

        Stopwatch stopwatch = Stopwatch.createStarted();
        while (true) {
            try {
                while (isLoadingDisplay()) {
                    Thread.sleep(10);
                }

                List<String[]> trainNumberList = getTrainNumAndSeatCountList();
                if (trainNumberList.size() > 0) {
                    log.info("Timer: clickQueryAndParseTrainNumSeatList=={} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
                    return trainNumberList;
                } else {
                    log.info("No ticket query result, click query again");
                    Thread.sleep(timeIntervalBetweenSeleniumQuery);
                    SeleniumUtil.waitUntilClickableThenClick(driver, queryTicketBtn);
                }
            } catch (TicketQueryNotReturnException e) {
                e.printStackTrace();
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void clickBookButtonByDriver(int bookingIndex) {
        log.info("Selenium Query:: Going to click book button in top down {}", bookingIndex);

        int bookableButtons = buttonsOfBookTicket.size();
        if (bookableButtons > 0) {
            if (bookingIndex < bookableButtons) {
                WebElement bookBtn = buttonsOfBookTicket.get(bookingIndex);
                SeleniumUtil.waitUntilClickableThenClick(driver, bookBtn);
            } else {
                throw new RuntimeException("Incorrect booking index, must re-query");
            }
        } else {
            throw new RuntimeException("Pity!!!!!! There is NO bookable button enabled");
        }
    }

    public void clickBookButtonByJS(int bookingIndex) {
        log.info("JS execution:: Going to click book button in top down {}", bookingIndex);
        String className = "btn72";

        String js = "function clickBooking(cn,index){var buttons =document.getElementsByClassName(cn);"
                + "var b=buttons[index];b.click();};clickBooking(arguments[0], arguments[1]);";
        JavascriptExecutor jExecutor = (JavascriptExecutor) driver;
        jExecutor.executeScript(js, className, String.valueOf(bookingIndex));
    }

    public int selectPassengers(String[] passengers) {
        new WebDriverWait(driver, 30).until(ExpectedConditions.visibilityOf(indicatorOfPassengerListLoaded));
        By passengerListLocator = By.cssSelector("#normal_passenger_id");
        By passengerCheckboxLocator = By.cssSelector("#normal_passenger_id li input");
        String nameLabelSelector = "li label";

        Document document = Jsoup.parse("<ul>" + driver.findElement(passengerListLocator).getAttribute("innerHTML") + "</ul>");
        Elements elements = document.select(nameLabelSelector);

        int[] passengerIndex = new int[passengers.length];
        int k = 0;
        for (String p : passengers) {
            for (int i = 0; i < elements.size(); i++) {
                if (elements.get(i).text().trim().equalsIgnoreCase(p)) {
                    passengerIndex[k++] = i;
                    break;
                }
            }
        }

        if (k != passengers.length) {
            throw new RuntimeException("Cannot find some name from the passenger available list");
        }

        List<WebElement> checkboxList = driver.findElements(passengerCheckboxLocator);
        int selected = 0;
        for (int i : passengerIndex) {
            checkboxList.get(i).click();
            selected++;
        }
        return selected;
    }

    public void submitOrder() {
        try {
            new WebDriverWait(driver, 30).until(ExpectedConditions.elementToBeClickable(submitOrder));
            submitOrder.click();
            new WebDriverWait(driver, 30).until(ExpectedConditions.visibilityOf(confirmOrderPopup));
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void confirmOrder() {
        Stopwatch stopWatch = Stopwatch.createStarted();

        By confirmOrderBtnBy = By.cssSelector(".dhtmlx_window_active:not([style*=\"display: none;\"]) #qr_submit_id");
        int i = 0;
        while (stopWatch.elapsed(TimeUnit.SECONDS) < 10) {
            i++;
            try {
                /*Thread.sleep(200);*/
                confirmOrderBtn.click();  //if button not exist, will throw exception, then continue inner loop
                if (driver.findElements(confirmOrderBtnBy).size() == 0) {
                    log.info("Count of clicking confirm order button: {}", i);
                    log.info("Clicking confirm order button spend: {} seconds", stopWatch.elapsed(TimeUnit.MILLISECONDS));
                    return;
                }
                log.info("confirmOrder click seems ok, but not ----------------------");
            } catch (Exception e) {
                log.info("confirmOrder fall into exception block after clicking --------------------");
            }
        }
    }

    public boolean hasProceededToPayment(int time) {
        try {
            new WebDriverWait(driver, time).until(ExpectedConditions.visibilityOf(buttonLinkToPayment));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public List<QueryTicketRow> getTicketRowDtoList() throws TicketQueryNotReturnException {
        By queryLeftTable_locator = By.cssSelector("#queryLeftTable");
        String cell_selector = "tr[id^=\"ticket\"] td";
        String document;

        try {
            WebElement queryLeftTable = driver.findElement(queryLeftTable_locator);
            document = queryLeftTable.getAttribute("innerHTML");
            if ("".equals(document)) {
                log.info("Empty ticket query result");
                return Collections.emptyList();
            }
        } catch (StaleElementReferenceException | NoSuchElementException e) {
            throw new TicketQueryNotReturnException(e.getMessage(), e);
        }

        Document ticketsTable = Jsoup.parse("<table><tbody>" + document + "</tbody></table>");
        Elements cells = ticketsTable.select(cell_selector);

        int tdSizeEachRow = 13;
        int rowSize = cells.size() / tdSizeEachRow;
        IntStream intStream = IntStream.range(0, rowSize);

        List<QueryTicketRow> list = new ArrayList<>(rowSize);
        for (int i = 0; i < rowSize; i++) {
            list.add(null);
        }

        intStream.parallel().forEach(rowIndex -> {
            Element firstTd = cells.get(rowIndex * tdSizeEachRow);
            String trainNumber = getTrainNumber(firstTd);
            String[] departureArrivalTimes = getTrainDepartureArrivalTime(firstTd);

            int seatCountStartIndex = rowIndex * tdSizeEachRow + 1;
            int seatCountEndIndex = rowIndex * tdSizeEachRow + 11;
            Integer availableCount = getAvailableTicketNumber(cells, seatCountStartIndex, seatCountEndIndex);

            Boolean isBookable = (availableCount > 0);
            list.set(rowIndex, new QueryTicketRow(trainNumber, departureArrivalTimes[0],
                    departureArrivalTimes[1], availableCount, isBookable));
        });

        list.forEach(System.out::println);
        return list;
    }

    public List<String[]> getTrainNumAndSeatCountList() throws TicketQueryNotReturnException {
        By queryLeftTable_locator = By.cssSelector("#queryLeftTable");
        String cell_selector = "tr[id^=\"ticket\"] td";
        String document;

        try {
            WebElement queryLeftTable = driver.findElement(queryLeftTable_locator);
            document = queryLeftTable.getAttribute("innerHTML");
            if ("".equals(document)) {
                log.info("Empty ticket query result");
                return Collections.emptyList();
            }
        } catch (StaleElementReferenceException | NoSuchElementException e) {
            throw new TicketQueryNotReturnException(e.getMessage(), e);
        }

        Document ticketsTable = Jsoup.parse("<table><tbody>" + document + "</tbody></table>");
        Elements cells = ticketsTable.select(cell_selector);

        int tdSizeEachRow = 13;
        int rowSize = cells.size() / tdSizeEachRow;
        IntStream intStream = IntStream.range(0, rowSize);

        List<String[]> list = new ArrayList<>(rowSize);
        for (int i = 0; i < rowSize; i++) {
            list.add(null);
        }

        intStream.parallel().forEach(rowIndex -> {
            Element firstTd = cells.get(rowIndex * tdSizeEachRow);
            String trainNumber = getTrainNumber(firstTd);

            int seatCountStartIndex = rowIndex * tdSizeEachRow + 1;
            int seatCountEndIndex = rowIndex * tdSizeEachRow + 11;
            Integer availableCount = getAvailableTicketNumber(cells, seatCountStartIndex, seatCountEndIndex);
            list.set(rowIndex, new String[]{ trainNumber, String.valueOf(availableCount)});
        });

        list.forEach(item -> System.out.println(item[0] +" - " + item[1]));
        return list;
    }

    private String getTrainNumber(Element tableTdNode) {
        String selector = ".train .number";
        return tableTdNode.select(selector).text().trim();
    }

    private String[] getTrainDepartureArrivalTime(Element tableTdNode) {
        String selector = ".cds strong";
        Elements elements = tableTdNode.select(selector);
        return new String[]{elements.get(0).text().trim(), elements.get(1).text().trim()};
    }

    private int getAvailableTicketNumber(List<Element> fullListOfTdNodes, int startIndex, int endIndex) {
        return IntStream.range(startIndex, endIndex + 1)
                .parallel()
                .map(i -> {
                    String cellText = fullListOfTdNodes.get(i).text().trim();

                    if ("有".equalsIgnoreCase(cellText)) {
                        return 1000;
                    } else if ("--".equalsIgnoreCase(cellText)
                            || "0".equalsIgnoreCase(cellText)
                            || "无".equalsIgnoreCase(cellText)) {
                        return 0;
                    } else {
                        try {
                            return Integer.parseInt(cellText);
                        } catch (NumberFormatException e) {
                            /*log.error("cellText {} cannot parse to integer for calculating available ticket count", cellText);*/
                            return 0;
                        }
                    }
                })
                .sum();
    }
}
