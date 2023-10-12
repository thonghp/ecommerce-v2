package com.hpt.frontend.checkout;

import com.hpt.common.entity.*;
import com.hpt.frontend.address.AddressService;
import com.hpt.frontend.category.CategoryService;
import com.hpt.frontend.customer.CustomerService;
import com.hpt.frontend.setting.email.MailUtils;
import com.hpt.frontend.shipping.ShippingRateService;
import com.hpt.frontend.shoppingcart.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CheckoutController {
    @Autowired
    private CheckoutService checkoutService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ShippingRateService shipService;
    @Autowired
    private ShoppingCartService cartService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);

        Address defaultReplaceAddress = addressService.getDefaultAddress(customer);
        List<Category> listCategories = categoryService.listRootCategories();
        ShippingRate shippingRate = null;

        if (defaultReplaceAddress != null) {
            model.addAttribute("shippingAddress", defaultReplaceAddress.toString());
            shippingRate = shipService.getShippingRateForAddress(defaultReplaceAddress);
        } else {
            model.addAttribute("shippingAddress", customer.toString());
            shippingRate = shipService.getShippingRateForCustomer(customer);
        }

        if (shippingRate == null) {
            return "redirect:/cart";
        }

        List<CartItem> cartItems = cartService.listCartItems(customer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);

        model.addAttribute("checkoutInfo", checkoutInfo);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("listCategories", listCategories);

        return "checkout/checkout";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = MailUtils.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(email);
    }
}
