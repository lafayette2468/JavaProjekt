package erpproject.exceptions;

public class CannotModifyOrderException extends RuntimeException {
    public CannotModifyOrderException(long id) {
        super(String.format("Order with id %d cannot be modified, already invoiced" , id));
    }
}
