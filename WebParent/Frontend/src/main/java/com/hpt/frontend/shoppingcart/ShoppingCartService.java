package com.hpt.frontend.shoppingcart;

import com.hpt.common.entity.CartItem;
import com.hpt.common.entity.Customer;
import com.hpt.common.entity.Product;
import com.hpt.common.exception.ShoppingCartException;
import com.hpt.frontend.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ShoppingCartService {
    @Autowired
    private CartItemRepository cartRepo;
    @Autowired
    private ProductRepository productRepo;

    /**
     * Add a product to the shopping cart.
     *
     * @param productId The ID of the product to add.
     * @param quantity  The quantity of the product to add.
     * @param customer  The customer who adds the product.
     * @return The updated quantity of the product in the shopping cart.
     * @throws ShoppingCartException If the quantity of the product in the shopping cart exceeds 5.
     */
    public Integer addProduct(Integer productId, Integer quantity, Customer customer)
            throws ShoppingCartException {
        Integer updatedQuantity = quantity;
        Product product = new Product();
        product.setId(productId);

        CartItem cartItem = cartRepo.findByCustomerAndProduct(customer, product);

        if (cartItem != null) {
            updatedQuantity = cartItem.getQuantity() + quantity;

            if (updatedQuantity > 5) {
                throw new ShoppingCartException("Không thể thêm " + quantity + " sản phẩm này vì đã có " +
                        cartItem.getQuantity() + " sản phẩm trong giỏ hàng của bạn. Số lượng tối đa cho phép là 5.");
            }
        } else {
            cartItem = new CartItem();
            cartItem.setCustomer(customer);
            cartItem.setProduct(product);
        }

        cartItem.setQuantity(updatedQuantity);

        cartRepo.save(cartItem);

        return updatedQuantity;
    }

    /**
     * Return a list of items in the customer's cart.
     *
     * @param customer The customer who owns the cart.
     * @return a list of items
     */
    public List<CartItem> listCartItems(Customer customer) {
        return cartRepo.findByCustomer(customer);
    }

    /**
     * Update the quantity of a product and subtotal in the shopping cart.
     *
     * @param productId The ID of the product to update.
     * @param quantity  The new quantity of the product.
     * @param customer  The customer who owns the cart.
     * @return The new subtotal of the product.
     */
    public float updateQuantity(Integer productId, Integer quantity, Customer customer) {
        cartRepo.updateQuantity(quantity, customer.getId(), productId);
        Product product = productRepo.findById(productId).get();

        return product.getDiscountPrice() * quantity;
    }

    /**
     * Remove a product from the shopping cart.
     *
     * @param productId The ID of the product to remove.
     * @param customer  The customer who owns the cart.
     */
    public void removeProduct(Integer productId, Customer customer) {
        cartRepo.deleteByCustomerAndProduct(customer.getId(), productId);
    }
}
