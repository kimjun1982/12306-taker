package ticketquery.selenium;

public class TicketQueryNotReturnException extends RuntimeException {
    public TicketQueryNotReturnException(String message, Throwable cause) {
        super(message, cause);
    }

    public TicketQueryNotReturnException(String message) {
        super(message);
    }


}
