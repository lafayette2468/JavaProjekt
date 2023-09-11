package erpproject.model;

public enum Vat {
    VAT_0(0), VAT_5(0.5), VAT_18(0.18), VAT_27(0.27);

    private double value;

    Vat(double value) {
        this.value = value;
    }
}
