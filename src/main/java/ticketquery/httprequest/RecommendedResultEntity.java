package ticketquery.httprequest;

public class RecommendedResultEntity {

    public int indexInAvailableList;
    public int ticketNumber;
    public String startTime;

    public RecommendedResultEntity(int indexInAvailableList, int ticketNumber, String startTime) {

        this.indexInAvailableList = indexInAvailableList;
        this.ticketNumber = ticketNumber;
        this.startTime = startTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(int ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public int getIndexInAvailableList() {
        return indexInAvailableList;
    }

    public void setIndexInAvailableList(int indexOfAvailableList) {
        this.indexInAvailableList = indexOfAvailableList;
    }

}
