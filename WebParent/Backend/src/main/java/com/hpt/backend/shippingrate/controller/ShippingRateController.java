package com.hpt.backend.shippingrate.controller;

import com.hpt.backend.paging.PagingAndSortingHelper;
import com.hpt.backend.paging.PagingAndSortingParam;
import com.hpt.backend.shippingrate.ShippingRateService;
import com.hpt.common.entity.Country;
import com.hpt.common.entity.ShippingRate;
import com.hpt.common.exception.ShippingRateAlreadyExistsException;
import com.hpt.common.exception.ShippingRateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ShippingRateController {
    private String defaultRedirectURL = "redirect:/shipping_rates/page/1?sortField=country&sortType=asc";

    @Autowired
    private ShippingRateService service;

    @GetMapping("/shipping_rates")
    public String listFirstPage() {
        return defaultRedirectURL;
    }

    @GetMapping("/shipping_rates/page/{pageNum}")
    public String listByPage(@PagingAndSortingParam(listName = "shippingRates",
            moduleURL = "/shipping_rates") PagingAndSortingHelper helper,
                             @PathVariable(name = "pageNum") int pageNum) {
        service.listByPage(pageNum, helper);

        return "shipping_rates/shipping_rates";
    }

    @GetMapping("/shipping_rates/new")
    public String newRate(Model model) {
        List<Country> listCountries = service.listAllCountries();

        model.addAttribute("rate", new ShippingRate());
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("pageTitle", "Thêm phí ship");

        return "shipping_rates/shipping_rate_form";
    }

    @PostMapping("/shipping_rates/save")
    public String saveRate(ShippingRate rate, RedirectAttributes ra) {
        try {
            service.save(rate);
            ra.addFlashAttribute("message", "Phí giao hàng đã được lưu thành công !.");
        } catch (ShippingRateAlreadyExistsException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/shipping_rates/edit/{id}")
    public String editRate(@PathVariable(name = "id") Integer id,
                           Model model, RedirectAttributes ra) {
        try {
            ShippingRate rate = service.get(id);
            List<Country> listCountries = service.listAllCountries();

            model.addAttribute("listCountries", listCountries);
            model.addAttribute("rate", rate);
            model.addAttribute("pageTitle", "Chỉnh sửa phí ship");

            return "shipping_rates/shipping_rate_form";
        } catch (ShippingRateNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return defaultRedirectURL;
        }
    }

    @GetMapping("/shipping_rates/cod/{id}/enabled/{supported}")
    public String updateCODSupport(@PathVariable(name = "id") Integer id,
                                   @PathVariable(name = "supported") Boolean supported,
                                   Model model, RedirectAttributes ra) {
        try {
            service.updateCODSupport(id, supported);
            String status = supported ? "Hỗ trợ" : "Không hỗ trợ";
            String message = status + " COD cho phí ship có ID: " + id;
            ra.addFlashAttribute("message", message);
        } catch (ShippingRateNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/shipping_rates/delete/{id}")
    public String deleteRate(@PathVariable(name = "id") Integer id,
                             Model model, RedirectAttributes ra) {
        try {
            service.delete(id);
            ra.addFlashAttribute("message", "Phí ship của id " + id + " đã được xoá.");
        } catch (ShippingRateNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return defaultRedirectURL;
    }
}
