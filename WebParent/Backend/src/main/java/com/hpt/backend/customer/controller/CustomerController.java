package com.hpt.backend.customer.controller;

import com.hpt.backend.customer.CustomerService;
import com.hpt.common.entity.Country;
import com.hpt.common.entity.Customer;
import com.hpt.common.exception.CustomerNotFoundException;
import com.hpt.common.utils.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.hpt.common.utils.CommonUtils.ASCENDING;
import static com.hpt.common.utils.CommonUtils.DESCENDING;

@Controller
public class CustomerController {
    @Autowired
    private CustomerService service;

    private final static String SORT_FIELD_NAME = "firstName";

    @GetMapping("/customers")
    public String listFirstPage(Model model) {
        return listByPage(model, 1, SORT_FIELD_NAME, ASCENDING, null);
    }

    @GetMapping("/customers/page/{pageNum}")
    public String listByPage(Model model, @PathVariable(name = "pageNum") int pageNum,
                             @RequestParam(name = "sortField") String sortField,
                             @RequestParam(name = "sortType") String sortType,
                             @RequestParam(name = "keyword", required = false) String keyword) {
        PageInfo pageInfo = new PageInfo();
        List<Customer> listCustomers = service.listByPage(pageInfo, pageNum, sortField, sortType, keyword);
        String reverseSortType = sortType.equals(ASCENDING) ? DESCENDING : ASCENDING;

        long startCount = pageInfo.getStartCount(pageNum, CustomerService.CUSTOMERS_PER_PAGE);
        long endCount = pageInfo.getEndCount(pageNum, CustomerService.CUSTOMERS_PER_PAGE);

        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalItems", pageInfo.getTotalElements());
        model.addAttribute("totalPages", pageInfo.getTotalPages());
        model.addAttribute("listCustomers", listCustomers);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortType", sortType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("reverseSortType", reverseSortType);
        model.addAttribute("moduleURL", "/customers");

        return "customers/customers";
    }

    @GetMapping("/customers/{id}/enabled/{status}")
    public String updateCustomerEnabledStatus(@PathVariable("id") Integer id,
                                              @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        service.updateCustomerEnabledStatus(id, enabled);
        String status = enabled ? " được kích hoạt" : " bị vô hiệu hoá";
        String message = "Khách hàng có id là " + id + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/customers";
    }

    @GetMapping("/customers/detail/{id}")
    public String viewCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Customer customer = service.get(id);
            model.addAttribute("customer", customer);

            return "customers/customer_detail_modal";
        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/customers";
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

            return "redirect:/customers";
        }
    }

    @PostMapping("/customers/save")
    public String saveCustomer(Customer customer, Model model, RedirectAttributes ra) {
        service.save(customer);
        String fullName = customer.getLastName() + " " + customer.getFirstName();
        ra.addFlashAttribute("message", "Khách hàng " + fullName + " đã được lưu thành công");

        return "redirect:/customers";
    }

    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            service.delete(id);
            ra.addFlashAttribute("message", "Khách hàng có id là " + id + " đã được xóa thành công");

        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }

        return "redirect:/customers";
    }
}
