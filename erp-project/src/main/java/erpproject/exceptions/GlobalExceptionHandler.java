package erpproject.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException e){
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getFieldError().getDefaultMessage());
        detail.setType(URI.create("validation/not-valid"));
        return detail;
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ProblemDetail handleIllegalArgumentException(CustomerNotFoundException e) {

        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        detail.setType(URI.create("customers/customer-not-found"));

        return detail;
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ProblemDetail handleIllegalArgumentException(OrderNotFoundException e) {

        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        detail.setType(URI.create("orders/order-not-found"));

        return detail;
    }

    @ExceptionHandler(InvoiceNotFoundException.class)
    public ProblemDetail handleIllegalArgumentException(InvoiceNotFoundException e) {

        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        detail.setType(URI.create("invoices/invoice-not-found"));

        return detail;
    }

    @ExceptionHandler(CannotModifyOrderException.class)
    public ProblemDetail handleIllegalArgumentException(CannotModifyOrderException e) {

        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage());
        detail.setType(URI.create("orders/order-cannot-modify"));

        return detail;
    }

    @ExceptionHandler(CannotCreateInvoiceException.class)
    public ProblemDetail handleIllegalArgumentException(CannotCreateInvoiceException e) {

        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage());
        detail.setType(URI.create("invoices/invoice-cannot-create"));

        return detail;
    }

}
