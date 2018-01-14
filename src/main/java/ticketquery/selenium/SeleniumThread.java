package ticketquery.selenium;

import defaultpackage.Config;
import defaultpackage.TravelInfo;
import model.QueryTicketRow;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SeleniumThread implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(SeleniumThread.class);
    private TravelInfo travelInfo;
    private PageObject pageObject;
    private WebDriver driver;
    private volatile static boolean isBookingThreadInProgress = false;
    private final int queryTimeInterval;


    public SeleniumThread() {
        this.driver = DriverFactory.getDriver();
        this.pageObject = PageObject.getPageObj(this.driver);
        this.travelInfo = TravelInfo.getTravelInfo();
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

        while (true) {
            try {
                log.info("Selenium Request:: Consuming arrived data");

                List<QueryTicketRow> queryResultList = pageObject.clickQueryButtonEnsureResultReturn(queryTimeInterval);

                int bookingIndex = SeleniumThreadHelper.findFirstWithinTimeRange(queryResultList, travelInfo);

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
    }
}
