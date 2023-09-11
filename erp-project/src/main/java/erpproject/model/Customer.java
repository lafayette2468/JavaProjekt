package erpproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "customers")
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "tax_number")
    private String taxNumber;
    @Column(name = "zip_code")
    private String zipCode;
    private String city;
    private String address;
    @OneToMany(mappedBy = "customer")
    private List<Order> orders;


    public Customer(String name, String taxNumber, String zipCode, String city, String address) {
        this.name = name;
        this.taxNumber = taxNumber;
        this.zipCode = zipCode;
        this.city = city;
        this.address = address;
    }
}
