package com.hpt.frontend.shoppingcart.controller;

import com.hpt.common.entity.CartItem;
import com.hpt.common.entity.Category;
import com.hpt.common.entity.Customer;
import com.hpt.frontend.category.CategoryService;
import com.hpt.frontend.customer.CustomerService;
import com.hpt.frontend.setting.email.MailUtils;
import com.hpt.frontend.shoppingcart.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ShoppingCartController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ShoppingCartService cartService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/cart")
    public String viewCart(Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        List<CartItem> cartItems = cartService.listCartItems(customer);
        List<Category> listCategories = categoryService.listRootCategories();

        float estimatedTotal = 0.0F;

        for (CartItem item : cartItems) {
            estimatedTotal += item.getSubtotal();
        }

        model.addAttribute("listCategories", listCategories);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("estimatedTotal", estimatedTotal);

        return "cart/cart";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = MailUtils.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(email);
    }
}
