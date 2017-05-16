package model;


public class QueryTicketRow {
    private String trainNumber;
    private String departureTime;
    private String arrivalTime;
    private Integer availableTicketCount;
    private Boolean isBookable;

    public QueryTicketRow(
            String trainNumber,
            String departureTime,
            String arrivalTime,
            Integer availableTicketCount,
            Boolean isBookable) {
        this.trainNumber = trainNumber;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.availableTicketCount = availableTicketCount;
        this.isBookable = isBookable;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Integer getAvailableTicketCount() {
        return availableTicketCount;
    }

    public void setAvailableTicketCount(Integer availableTicketCount) {
        this.availableTicketCount = availableTicketCount;
    }

    public Boolean getBookable() {
        return isBookable;
    }

    public void setBookable(Boolean bookable) {
        isBookable = bookable;
    }

    @Override
    public String toString() {
        return "QueryTicketRow{" +
                "trainNumber='" + trainNumber + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                ", availableTicketCount=" + availableTicketCount +
                ", isBookable=" + isBookable +
                '}';
    }
}
