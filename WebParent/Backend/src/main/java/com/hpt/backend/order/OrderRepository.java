package com.hpt.backend.order;

import com.hpt.common.entity.Order;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, Integer> {
}
