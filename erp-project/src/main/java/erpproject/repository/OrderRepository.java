package erpproject.repository;

import erpproject.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o left join o.invoice i where i is null")
    List<Order> getUninvoicedOrders();


    Optional<Order> findOrderByInvoiceId(long invoiceId);

    Optional<Order> findOrderByOrderNumber(String orderNumber);
}
