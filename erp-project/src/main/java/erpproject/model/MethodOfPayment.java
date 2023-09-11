package erpproject.model;

public enum MethodOfPayment {
    CASH("Készpénz"), BANK_TRANSFER("Átutalás"), CREDIT_CARD("Bankkártya");

    private String description;

    MethodOfPayment(String description) {
        this.description = description;
    }
}
