package erpproject.repository;


import erpproject.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findCustomerByNameContains(String part);

    @Query("select distinct c from Customer c left join fetch c.orders where c.id = :id")
    Optional<Customer> findCustomerWithOrders(@Param("id") long id);
}
