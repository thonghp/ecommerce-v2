package com.hpt.frontend.shoppingcart;

import com.hpt.common.entity.CartItem;
import com.hpt.common.entity.Customer;
import com.hpt.common.entity.product.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends CrudRepository<CartItem, Integer> {
    List<CartItem> findByCustomer(Customer customer);

    CartItem findByCustomerAndProduct(Customer customer, Product product);

    @Modifying
    @Query("UPDATE CartItem c SET c.quantity = ?1 WHERE c.customer.id = ?2 AND c.product.id = ?3")
    void updateQuantity(Integer quantity, Integer customerId, Integer productId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.customer.id = ?1 AND c.product.id = ?2")
    void deleteByCustomerAndProduct(Integer customerId, Integer productId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.customer.id = ?1")
    void deleteByCustomer(Integer id);
}
