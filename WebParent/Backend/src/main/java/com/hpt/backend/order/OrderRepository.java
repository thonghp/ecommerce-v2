package com.hpt.backend.order;

import com.hpt.backend.paging.SearchRepository;
import com.hpt.common.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepository extends SearchRepository<Order, Integer> {
    @Query("SELECT o FROM Order o WHERE CONCAT('#', o.id) LIKE %?1% OR "
            + " CONCAT(o.firstName, ' ', o.lastName) LIKE %?1% OR"
            + " o.firstName LIKE %?1% OR"
            + " o.lastName LIKE %?1% OR o.phoneNumber LIKE %?1% OR"
            + " o.addressLine1 LIKE %?1% OR o.addressLine2 LIKE %?1% OR"
            + " o.postalCode LIKE %?1% OR o.city LIKE %?1% OR"
            + " o.state LIKE %?1% OR o.country LIKE %?1% OR"
            + " o.paymentMethod LIKE %?1% OR o.status LIKE %?1% OR"
            + " o.customer.firstName LIKE %?1% OR"
            + " o.customer.lastName LIKE %?1%")
    Page<Order> findAll(String keyword, Pageable pageable);

    Long countById(Integer id);
}
