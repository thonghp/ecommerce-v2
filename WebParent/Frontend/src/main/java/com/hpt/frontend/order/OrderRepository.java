package com.hpt.frontend.order;

import com.hpt.common.entity.Customer;
import com.hpt.common.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<Order, Integer> {
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderDetails od JOIN od.product p "
            + "WHERE o.customer.id = ?2 "
            + "AND (p.name LIKE %?1% OR o.status LIKE %?1%)")
    Page<Order> findAll(String keyword, Integer customerId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.customer.id = ?1")
    Page<Order> findAll(Integer customerId, Pageable pageable);

    Order findByIdAndCustomer(Integer id, Customer customer);
}
