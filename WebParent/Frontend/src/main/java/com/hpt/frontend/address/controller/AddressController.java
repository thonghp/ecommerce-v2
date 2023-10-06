package com.hpt.frontend.address.controller;

import com.hpt.common.entity.Address;
import com.hpt.common.entity.Category;
import com.hpt.common.entity.Country;
import com.hpt.common.entity.Customer;
import com.hpt.common.exception.AddressNotFoundException;
import com.hpt.frontend.address.AddressService;
import com.hpt.frontend.category.CategoryService;
import com.hpt.frontend.customer.CustomerService;
import com.hpt.frontend.setting.email.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class AddressController {
    @Autowired
    private AddressService addressService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/address_book")
    public String showAddressBook(Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        List<Address> listAddresses = addressService.listAddressBook(customer);
        List<Category> listCategories = categoryService.listRootCategories();

        // Nếu không có địa chỉ nào được đánh dấu là mặc định thì đánh dấu địa chỉ khi tạo là mặc định
        boolean usePrimaryAddressAsDefault = true;
        for (Address address : listAddresses) {
            if (address.isDefaultForShipping()) {
                usePrimaryAddressAsDefault = false;
                break;
            }
        }

        model.addAttribute("listCategories", listCategories);
        model.addAttribute("listAddresses", listAddresses);
        model.addAttribute("customer", customer);
        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);

        return "address_book/addresses";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = MailUtils.getEmailOfAuthenticatedCustomer(request);
        return customerService.getCustomerByEmail(email);
    }

    @GetMapping("/address_book/new")
    public String newAddress(Model model) {
        List<Category> listCategories = categoryService.listRootCategories();
        List<Country> listCountries = customerService.listAllCountries();

        model.addAttribute("listCategories", listCategories);
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("address", new Address());
        model.addAttribute("pageTitle", "Thêm Địa Chỉ");

        return "address_book/address_form";
    }

    @PostMapping("/address_book/save")
    public String saveAddress(Address address, HttpServletRequest request, RedirectAttributes ra) {
        Customer customer = getAuthenticatedCustomer(request);

        address.setCustomer(customer);
        addressService.save(address);

        ra.addFlashAttribute("message", "Địa chỉ đã được lưu thành công.");

        return "redirect:/address_book";
    }

    @GetMapping("/address_book/edit/{id}")
    public String editAddress(@PathVariable("id") Integer addressId, Model model, HttpServletRequest request, RedirectAttributes ra) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            List<Country> listCountries = customerService.listAllCountries();

            Address address = addressService.get(addressId, customer.getId());

            model.addAttribute("address", address);
            model.addAttribute("listCountries", listCountries);
            model.addAttribute("pageTitle", "Chỉnh Sửa Địa Chỉ");

            return "address_book/address_form";
        } catch (AddressNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());

            return "redirect:/address_book";
        }
    }

    @GetMapping("/address_book/delete/{id}")
    public String deleteAddress(@PathVariable("id") Integer addressId, RedirectAttributes ra,
                                HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        addressService.delete(addressId, customer.getId());

        ra.addFlashAttribute("message", "Địa chỉ đã được xoá thành công.");

        return "redirect:/address_book";
    }

    @GetMapping("/address_book/default/{id}")
    public String setDefaultAddress(@PathVariable("id") Integer addressId,
                                    HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        addressService.setDefaultAddress(addressId, customer.getId());

        String redirectOption = request.getParameter("redirect");
        String redirectURL = "redirect:/address_book";

        if ("cart".equals(redirectOption)) {
            redirectURL = "redirect:/cart";
        }

        return redirectURL;
    }
}
