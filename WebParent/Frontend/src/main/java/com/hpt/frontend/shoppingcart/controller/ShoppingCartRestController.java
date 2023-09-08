package com.hpt.frontend.shoppingcart.controller;

import com.hpt.common.entity.Customer;
import com.hpt.common.exception.CustomerNotFoundException;
import com.hpt.common.exception.ShoppingCartException;
import com.hpt.frontend.customer.CustomerService;
import com.hpt.frontend.setting.email.MailUtils;
import com.hpt.frontend.shoppingcart.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController

public class ShoppingCartRestController {
    @Autowired
    private ShoppingCartService cartService;
    @Autowired
    private CustomerService customerService;

    @PostMapping("/cart/add/{productId}/{quantity}")
    public String addProductToCart(@PathVariable("productId") Integer productId,
                                   @PathVariable("quantity") Integer quantity, HttpServletRequest request) {

        try {
            Customer customer = getAuthenticatedCustomer(request);
            Integer updatedQuantity = cartService.addProduct(productId, quantity, customer);

            return updatedQuantity + " sản phẩm này đã được thêm vào giỏ hàng của bạn.";
        } catch (CustomerNotFoundException ex) {
            return "Bạn phải đăng nhập để thêm sản phẩm này vào giỏ hàng.";
        } catch (ShoppingCartException ex) {
            return ex.getMessage();
        }
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = MailUtils.getEmailOfAuthenticatedCustomer(request);
        if (email == null) {
            throw new CustomerNotFoundException("No authenticated customer");
        }

        return customerService.getCustomerByEmail(email);
    }

    @PostMapping("/cart/update/{productId}/{quantity}")
    public String updateQuantity(@PathVariable("productId") Integer productId,
                                 @PathVariable("quantity") Integer quantity, HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            float subtotal = cartService.updateQuantity(productId, quantity, customer);

            return String.valueOf(subtotal);
        } catch (CustomerNotFoundException ex) {
            return "Bạn phải đăng nhập để thay đổi số lượng sản phẩm này.";
        }
    }

    @DeleteMapping("/cart/remove/{productId}")
    public String removeProduct(@PathVariable("productId") Integer productId, HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            cartService.removeProduct(productId, customer);

            return "Sản phẩm đã được xoá khỏi giỏ hàng.";
        } catch (CustomerNotFoundException e) {
            return "Bạn phải đăng nhập để thay đổi số lượng sản phẩm này.";
        }
    }
}
