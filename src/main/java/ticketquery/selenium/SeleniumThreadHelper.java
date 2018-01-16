package ticketquery.selenium;

import defaultpackage.TravelInfo;
import model.QueryTicketRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class SeleniumThreadHelper {
    private static final Logger log = LoggerFactory.getLogger(SeleniumThreadHelper.class);
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");


    public static void sleep(int i) {//in ms
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void increasedSleepTime(int happensCounter) {
        if (happensCounter > 0 && happensCounter % 5 == 0) {
            try {
                log.info(happensCounter + "th network busy hit, sleep 2 seconds");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static int findFirstWithinTimeRange(List<QueryTicketRow> queryTicketResultRows, TravelInfo travelInfo) {
        LocalTime expectedDepartureFrom = LocalTime.parse(travelInfo.getExpectedDepartureFromTime(), timeFormatter);
        LocalTime expectedDepartureTo = LocalTime.parse(travelInfo.getExpectedDepartureToTime(), timeFormatter);

        for (int index = 0; index < queryTicketResultRows.size(); index++) {
            QueryTicketRow thisRow = queryTicketResultRows.get(index);

            if (thisRow.getAvailableTicketCount() <= 0) continue;

            if (inSkipList(thisRow, travelInfo)) continue;

            LocalTime actualDeparture = LocalTime.parse(thisRow.getDepartureTime(), timeFormatter);

            if (isMatchPreferredTimeRange(actualDeparture, expectedDepartureFrom, expectedDepartureTo)) {
                return index;
            }
        }
        return -1;
    }

    public static int findSpecifiedTrain(List<String> trainNumberList, String trainOnlyAccept) {
        return trainNumberList.indexOf(trainOnlyAccept);
    }

    private static boolean inSkipList(QueryTicketRow thisRow, TravelInfo travelInfo) {
        String[] skips = travelInfo.getSkips();
        for (int i = 0; i < skips.length; i++) {
            if (thisRow.getTrainNumber().trim().equals(skips[i].trim())) return true;
        }
        return false;
    }

    public static int findLastWithinTimeRange(List<QueryTicketRow> queryTicketResultRows, TravelInfo travelInfo) {
        LocalTime expectedDepartureFrom = LocalTime.parse(travelInfo.getExpectedDepartureFromTime(), timeFormatter);
        LocalTime expectedDepartureTo = LocalTime.parse(travelInfo.getExpectedDepartureToTime(), timeFormatter);

        for (int index = queryTicketResultRows.size() - 1; index >= 0; index++) {
            QueryTicketRow thisRow = queryTicketResultRows.get(index);

            if (thisRow.getAvailableTicketCount() <= 0) continue;

            if (inSkipList(thisRow, travelInfo)) continue;

            LocalTime actualDeparture = LocalTime.parse(thisRow.getDepartureTime(), timeFormatter);

            if (isMatchPreferredTimeRange(actualDeparture, expectedDepartureFrom, expectedDepartureTo)) {
                return index;
            }
        }
        return -1;
    }

    public static int findMostSeatsWithinTimeRange(List<QueryTicketRow> queryTicketResultRows, TravelInfo travelInfo) {
        LocalTime expectedDepartureFrom = LocalTime.parse(travelInfo.getExpectedDepartureFromTime(), timeFormatter);
        LocalTime expectedDepartureTo = LocalTime.parse(travelInfo.getExpectedDepartureToTime(), timeFormatter);

        int tempCount = 0;
        int resultIndex = -1;
        for (int index = 0; index < queryTicketResultRows.size(); index++) {
            QueryTicketRow thisRow = queryTicketResultRows.get(index);

            if (thisRow.getAvailableTicketCount() <= 0) continue;

            if (inSkipList(thisRow, travelInfo)) continue;

            LocalTime actualDeparture = LocalTime.parse(thisRow.getDepartureTime(), timeFormatter);

            if (isMatchPreferredTimeRange(actualDeparture, expectedDepartureFrom, expectedDepartureTo)) {
                if (thisRow.getAvailableTicketCount() > tempCount) {
                    tempCount = thisRow.getAvailableTicketCount();
                    resultIndex = index;
                }
            }
        }
        return resultIndex;
    }

    public static int findMostSeatsIgnoreTimeRange(List<QueryTicketRow> queryTicketResultRows) {
        int tempCount = 0;
        int resultIndex = -1;
        for (int index = 0; index < queryTicketResultRows.size(); index++) {
            QueryTicketRow thisRow = queryTicketResultRows.get(index);
            if (thisRow.getAvailableTicketCount() > tempCount) {
                tempCount = thisRow.getAvailableTicketCount();
                resultIndex = index;
            }
        }
        return resultIndex;
    }


    public static boolean isMatchPreferredTimeRange(
            String actualFromTime,
            String expectedFromLower,
            String expectedFromUpper) {
        LocalTime actualLocalTime = LocalTime.parse(actualFromTime, timeFormatter);
        LocalTime expectedLocalTimeLower = LocalTime.parse(expectedFromLower, timeFormatter);
        LocalTime expectedLocalTimeUpper = LocalTime.parse(expectedFromUpper, timeFormatter);
        return expectedLocalTimeLower.compareTo(actualLocalTime) <= 0
                && actualLocalTime.compareTo(expectedLocalTimeUpper) <= 0;
    }

    public static boolean isMatchPreferredTimeRange(
            LocalTime actualFromTime,
            LocalTime expectedFromLower,
            LocalTime expectedFromUpper) {
        return expectedFromLower.compareTo(actualFromTime) <= 0
                && actualFromTime.compareTo(expectedFromUpper) <= 0;
    }
}