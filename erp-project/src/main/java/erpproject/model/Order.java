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
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_number", unique = true)
    private String orderNumber;
    @Column(name = "order_date")
    private LocalDate orderDate;
    @Column(name = "item_description")
    private String itemDescription;
    @Column(name = "net_unit_price")
    private int netUnitPrice;
    private int quantity;
    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;
    @OneToOne (mappedBy = "order")
    private Invoice invoice;

    public Order(String orderNumber, LocalDate orderDate, String itemDescription, int netUnitPrice, int quantity) {
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.itemDescription = itemDescription;
        this.netUnitPrice = netUnitPrice;
        this.quantity = quantity;
    }
}
