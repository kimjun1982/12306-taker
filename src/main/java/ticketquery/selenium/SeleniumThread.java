package ticketquery.selenium;

import defaultpackage.Config;
import defaultpackage.TravelInfo;
import model.QueryTicketRow;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SeleniumThread implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(SeleniumThread.class);
    private TravelInfo travelInfo;
    private PageObject pageObject;
    private WebDriver driver;
    private volatile static boolean isBookingThreadInProgress = false;
    private final int queryTimeInterval;


    public SeleniumThread(WebDriver driver, TravelInfo travelInfo) {
        this.driver = driver;
        this.travelInfo = travelInfo;
        this.pageObject = PageObject.getPageObj(this.driver);
        queryTimeInterval = travelInfo.getTimeIntervalBetweenSeleniumQuery();
    }

    private static void pauseHttpRequestThread() {
        isBookingThreadInProgress = true;
    }

    private static void resumeHttpRequestThread() {
        isBookingThreadInProgress = false;
    }

    public static boolean isSeleniumBookingThreadInProgress() {
        return isBookingThreadInProgress;
    }

    @Override
    public void run() {
        this.initialSteps();
        this.timeWaiter();

        while (true) {
            try {
                log.info("Selenium Request:: Consuming arrived data");
                int bookingIndex;

                if (!"".equals(travelInfo.getTrainOnlyAccept())) {
                    List<String> trainNumberList = pageObject.clickQueryButtonAndParseTrainNumList(queryTimeInterval);
                    bookingIndex = SeleniumThreadHelper.findSpecifiedTrain(trainNumberList, travelInfo.getTrainOnlyAccept());
                } else {
                    List<QueryTicketRow> queryResultList = pageObject.clickQueryButtonAndParseTicketDetails(queryTimeInterval);
                    bookingIndex = SeleniumThreadHelper.findFirstWithinTimeRange(queryResultList, travelInfo);
                }

                if (bookingIndex <= -1) {
                    Thread.sleep(travelInfo.getTimeIntervalBetweenSeleniumQuery());
                    continue;
                }

                pageObject.clickBookButtonByDriver(bookingIndex);

                pageObject.selectPassengers(travelInfo.getPassengerList());

                pageObject.submitOrder();

                pageObject.confirmOrder();

                if (pageObject.hasProceededToPayment(120)) {
                    log.info(" Congratulations, book successfully, pls do payment manually");
                    Thread.sleep(Integer.MAX_VALUE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initialSteps() {
        pageObject.loadUrl(Config.url);

        log.info("Reserve some time for manual login...");

        pageObject.logonManually(120);

        pageObject.navigateToTicketReservationPage();

        pageObject.setFromToStation(travelInfo.getFromStation(), travelInfo.getToStation());

        pageObject.setDepartureDate(travelInfo.getDepartureDate());

        pageObject.simplyTicketQuery();
    }

    private void timeWaiter() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(travelInfo.getStartTimeToQuery(), formatter);
        log.info("Waiting until {}, now is {}", startTime, LocalDateTime.now());

        int counter = 0;
        long WAIT_TIME = 5L;
        while (LocalDateTime.now().isBefore(startTime)) {
            try {
                Thread.sleep(WAIT_TIME);
                counter++;
                /**
                 * every 10 seconds to make simple query in case of being forced to logoff
                 * but not do that if too close to start time
                 */
                if (counter * WAIT_TIME > 10 * 1000) {
                    counter = 0;
                    if (LocalDateTime.now().plusSeconds(10).isBefore(startTime)) {
                        pageObject.simplyTicketQuery();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("Selenium Runner resumes at {}", LocalDateTime.now());
    }
}
