package erpproject.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(long id) {
        super(String.format("Order not found with id %d", id));
    }
}
