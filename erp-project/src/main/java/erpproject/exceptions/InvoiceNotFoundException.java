package erpproject.exceptions;

public class InvoiceNotFoundException extends RuntimeException {
    public InvoiceNotFoundException(long id) {
        super(String.format("Invoice not found with id %d", id));
    }
}
