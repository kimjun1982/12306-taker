package defaultpackage;

import java.io.InputStream;
import java.util.Properties;

public class TravelInfo {
    private String fromStation;
    private String toStation;
    private String departureDate;
    private String[] passengerList;
    private String expectedDepartureFromTime;
    private String expectedDepartureToTime;
    private int timeIntervalBetweenSeleniumQuery;
    private int timeIntervalBetweenHttpRequestQuery;
    private String startTimeToQuery;
    private String[] skips;
    private String travelConfigFile;
    private String trainOnlyAccept;

    private static TravelInfo INSTANCE = new TravelInfo(Config.travelInfoClasspath);

    public static TravelInfo getTravelInfo() {
        return INSTANCE;
    }

    private TravelInfo(String travelConfigFile) {
        Properties properties = new Properties();
        this.travelConfigFile = travelConfigFile;
        try {
            InputStream is = TravelInfo.class.getClassLoader().getResourceAsStream(this.travelConfigFile);
            properties.load(is);
            fromStation = getPropertyAsString(properties, "from");
            toStation = getPropertyAsString(properties, "to");
            passengerList = getPropertyAsString(properties, "passengerName").split(",");
            departureDate = getPropertyAsString(properties, "date");
            expectedDepartureFromTime = getPropertyAsString(properties, "targetDepartureTimeFrom");
            expectedDepartureToTime = getPropertyAsString(properties, "targetDepartureTimeTo");

            timeIntervalBetweenSeleniumQuery = getPropertyAsInt(properties, "timeIntervalBetweenSeleniumQuery");
            timeIntervalBetweenHttpRequestQuery = getPropertyAsInt(properties, "timeIntervalBetweenHttpRequestQuery");
            startTimeToQuery = getPropertyAsString(properties, "whenToStartQuery");
            skips = getPropertyAsString(properties, "skips").split(",");
            trainOnlyAccept = getPropertyAsString(properties, "trainOnlyAccept");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String getPropertyAsString(Properties properties, String key) throws Exception {
        return new String(properties.getProperty(key).getBytes("ISO-8859-1"), "utf8");
    }

    private static int getPropertyAsInt(Properties properties, String key) throws Exception {
        return Integer.parseInt(properties.getProperty(key));
    }

    public String getFromStation() {
        return fromStation;
    }

    public void setFromStation(String fromStation) {
        this.fromStation = fromStation;
    }

    public String getToStation() {
        return toStation;
    }

    public void setToStation(String toStation) {
        this.toStation = toStation;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String[] getPassengerList() {
        return passengerList;
    }

    public void setPassengerList(String[] passengerList) {
        this.passengerList = passengerList;
    }

    public String getExpectedDepartureFromTime() {
        return expectedDepartureFromTime;
    }

    public void setExpectedDepartureFromTime(String targetTimeFrom) {
        this.expectedDepartureFromTime = targetTimeFrom;
    }

    public String getExpectedDepartureToTime() {
        return expectedDepartureToTime;
    }

    public void setExpectedDepartureToTime(String targetTimeTo) {
        this.expectedDepartureToTime = targetTimeTo;
    }

    public int getTimeIntervalBetweenSeleniumQuery() {
        return timeIntervalBetweenSeleniumQuery;
    }

    public void setTimeIntervalBetweenSeleniumQuery(int timeIntervalBetweenSeleniumQuery) {
        this.timeIntervalBetweenSeleniumQuery = timeIntervalBetweenSeleniumQuery;
    }

    public int getTimeIntervalBetweenHttpRequestQuery() {
        return timeIntervalBetweenHttpRequestQuery;
    }

    public void setTimeIntervalBetweenHttpRequestQuery(int timeIntervalBetweenHttpRequestQuery) {
        this.timeIntervalBetweenHttpRequestQuery = timeIntervalBetweenHttpRequestQuery;
    }

    public String getStartTimeToQuery() {
        return startTimeToQuery;
    }

    public void setStartTimeToQuery(String startTimeToQuery) {
        this.startTimeToQuery = startTimeToQuery;
    }

    public String[] getSkips() {
        return skips;
    }

    public void setSkips(String[] skips) {
        this.skips = skips;
    }


    public String getTrainOnlyAccept() {
        return trainOnlyAccept;
    }

    public void setTrainOnlyAccept(String trainOnlyAccept) {
        this.trainOnlyAccept = trainOnlyAccept;
    }
}
