package erpproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "invoice_number", unique = true)
    private String invoiceNumber;
    @Column(name = "invoice_date")
    private LocalDate invoiceDate;
    @Column(name = "fulfilment_date")
    private LocalDate fulfilmentDate;
    @Column(name = "due_date")
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    private Vat vat;
    @Enumerated(EnumType.STRING)
    @Column(name = "method_of_payment")
    private MethodOfPayment methodOfPayment;
    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    public Invoice(String invoiceNumber, LocalDate invoiceDate, LocalDate fulfilmentDate, LocalDate dueDate, Vat vat, MethodOfPayment methodOfPayment) {
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.fulfilmentDate = fulfilmentDate;
        this.dueDate = dueDate;
        this.vat = vat;
        this.methodOfPayment = methodOfPayment;
    }
}
