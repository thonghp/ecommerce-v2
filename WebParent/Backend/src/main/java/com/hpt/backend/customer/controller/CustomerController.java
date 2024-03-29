package com.hpt.backend.customer.controller;

import com.hpt.backend.customer.CustomerService;
import com.hpt.backend.paging.PagingAndSortingHelper;
import com.hpt.backend.paging.PagingAndSortingParam;
import com.hpt.common.entity.Country;
import com.hpt.common.entity.Customer;
import com.hpt.common.exception.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CustomerController {
    @Autowired
    private CustomerService service;

    private String defaultRedirectURL = "redirect:/customers/page/1?sortField=firstName&sortType=asc";

    @GetMapping("/customers")
    public String listFirstPage() {
        return defaultRedirectURL;
    }

    @GetMapping("/customers/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum,
                             @PagingAndSortingParam(listName = "listCustomers", moduleURL = "/customers") PagingAndSortingHelper helper) {
        service.listByPage(pageNum, helper);

        return "customers/customers";
    }

    @GetMapping("/customers/{id}/enabled/{status}")
    public String updateCustomerEnabledStatus(@PathVariable("id") Integer id,
                                              @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        service.updateCustomerEnabledStatus(id, enabled);
        String status = enabled ? " được kích hoạt" : " bị vô hiệu hoá";
        String message = "Khách hàng có id là " + id + status;
        redirectAttributes.addFlashAttribute("message", message);

        return defaultRedirectURL;
    }

    @GetMapping("/customers/detail/{id}")
    public String viewCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Customer customer = service.get(id);
            model.addAttribute("customer", customer);

            return "customers/customer_detail_modal";
        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return defaultRedirectURL;
        }
    }

    @GetMapping("/customers/edit/{id}")
    public String editCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Customer customer = service.get(id);
            List<Country> countries = service.listAllCountries();

            model.addAttribute("listCountries", countries);
            model.addAttribute("customer", customer);
            model.addAttribute("pageTitle", "Chỉnh Sửa Khách Hàng");

            return "customers/customer_form";

        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());

            return defaultRedirectURL;
        }
    }

    @PostMapping("/customers/save")
    public String saveCustomer(Customer customer, Model model, RedirectAttributes ra) {
        service.save(customer);
        String fullName = customer.getLastName() + " " + customer.getFirstName();
        ra.addFlashAttribute("message", "Khách hàng " + fullName + " đã được lưu thành công");

        return defaultRedirectURL;
    }

    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            service.delete(id);
            ra.addFlashAttribute("message", "Khách hàng có id là " + id + " đã được xóa thành công");

        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }

        return defaultRedirectURL;
    }
}
