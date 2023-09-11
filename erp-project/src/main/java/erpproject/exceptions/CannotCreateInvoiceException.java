package erpproject.exceptions;

public class CannotCreateInvoiceException extends RuntimeException {
    public CannotCreateInvoiceException() {
        super("Invoice already created or credited.");
    }
}
