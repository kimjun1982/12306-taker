package ticketquery.httprequest;

import defaultpackage.TravelInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Helper {
    private static final Logger log = LoggerFactory.getLogger(Helper.class);

    public static void waitToStartUntil(String startTime) {
        if ("0".equals(startTime) || "".equals(startTime) || startTime == null) return;

        LocalDateTime future;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            future = LocalDateTime.parse(startTime, formatter);
        } catch (Throwable t) {
            throw new RuntimeException("The date time string must be of format yyyy-MM-dd HH:mm:ss");
        }

        long sleepingPeriod = future.toInstant(ZoneOffset.of("+8")).toEpochMilli() - System.currentTimeMillis();
        if (sleepingPeriod <= 0) {
            log.info("Time cannot be earlier than now, so to start in right now");
            return;
        }
        try {
            log.info("Thread is sleeping until: {}", future);
            Thread.sleep(sleepingPeriod);
            log.info("Starting to query...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getStationCode(String stationInChinese) {
    /*
     * 怀集FAQ 广州南IZQ 潮汕CBQ 广州东GGQ,,from = 上海南 to = 武昌
     * hard code here temporarily, better to get the full list of mappings and save to HashMap
	 */
        switch (stationInChinese) {
            case "怀集":
                return "FAQ";
            case "广州南":
                return "IZQ";
            case "潮汕":
                return "CBQ";
            case "广州东":
                return "GGQ";
            case "长沙南":
                return "CWQ";
            case "上海虹桥":
                return "AOH";
            case "上海南":
                return "SNH";
            case "武昌":
                return "WCN";
            case "北京北":
                return "WCN";
            case "长春":
                return "CCT";
            case "北京":
                return "BJP";
            default:
                throw new RuntimeException("Cannot find the mapping code, please add");
        }
    }

    public static URL getTicketQueryURL(TravelInfo travelInfo, String requestBaseURL) throws MalformedURLException {
        String departureDate = travelInfo.getDepartureDate();
        String fromStation = travelInfo.getFromStation();
        String toStation = travelInfo.getToStation();

        String fromStationCode = Helper.getStationCode(fromStation);
        String toStationCode = Helper.getStationCode(toStation);

        String urlStr = requestBaseURL + "?leftTicketDTO.train_date=" + departureDate +
                "&leftTicketDTO.from_station=" + fromStationCode +
                "&leftTicketDTO.to_station=" + toStationCode +
                "&purpose_codes=ADULT";

        URL url = new URL(urlStr);
        if ("https".equalsIgnoreCase(url.getProtocol())) {
            try {
                SslUtils.ignoreSsl();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("Ticket Query URL: {}", url);
        return url;
    }

    private static LocalTime expectedFromTime;
    private static LocalTime expectedToTime;

    static {
        TravelInfo travelInfo = TravelInfo.getTravelInfo();
        String[] fromTimeSplits = travelInfo.getExpectedDepartureFromTime().split(":");
        String[] toTimeSplits = travelInfo.getExpectedDepartureToTime().split(":");
        expectedFromTime = LocalTime.of(Integer.parseInt(fromTimeSplits[0]), Integer.parseInt(fromTimeSplits[1])).minusSeconds(1);
        expectedToTime = LocalTime.of(Integer.parseInt(toTimeSplits[0]), Integer.parseInt(toTimeSplits[1])).plusSeconds(1);
    }

    public static boolean isTrainTimeFitted(ResultDataEntity resultEntity) throws Exception {
        String[] trainTimeSplits = resultEntity.getStart_time().split(":");
        LocalTime trainStartTime = LocalTime.of(Integer.parseInt(trainTimeSplits[0]),
                Integer.parseInt(trainTimeSplits[1]));

        return trainStartTime.isAfter(expectedFromTime) && trainStartTime.isBefore(expectedToTime);
    }

    public static boolean isStationMatched(ResultDataEntity resultEntity) {
        TravelInfo travelInfo = TravelInfo.getTravelInfo();

        String fromStationCode = Helper.getStationCode(travelInfo.getFromStation());
        String toStationCode = Helper.getStationCode(travelInfo.getToStation());

        return fromStationCode.equalsIgnoreCase(resultEntity.getFrom_station_telecode())
                && toStationCode.equalsIgnoreCase(resultEntity.getTo_station_telecode());
    }
}
