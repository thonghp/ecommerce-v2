package com.hpt.backend.brand.controller;

import com.hpt.backend.brand.BrandService;
import com.hpt.common.entity.Brand;
import com.hpt.common.utils.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.hpt.common.utils.CommonUtils.ASCENDING;
import static com.hpt.common.utils.CommonUtils.DESCENDING;

@Controller
public class BrandController {
    @Autowired
    private BrandService service;

    private final static String SORT_FIELD_NAME = "id";

    @GetMapping("/brands")
    public String listFirstPage(Model model) {
        return listByPage(1, model, SORT_FIELD_NAME, ASCENDING, null);
    }

    @GetMapping("/brands/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") Integer pageNum, Model model,
                             @RequestParam(name = "sortField") String sortField,
                             @RequestParam(name = "sortType") String sortType,
                             @RequestParam(name = "keyword", required = false) String keyword) {
        PageInfo pageInfo = new PageInfo();
        List<Brand> brands = service.listByPage(pageInfo, pageNum, sortField, sortType, keyword);
        String reverseSortType = sortType.equals(ASCENDING) ? DESCENDING : ASCENDING;

        long startCount = pageInfo.getStartCount(pageNum, BrandService.BRANDS_PER_PAGE);
        long endCount = pageInfo.getEndCount(pageNum, BrandService.BRANDS_PER_PAGE);

        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalItems", pageInfo.getTotalElements());
        model.addAttribute("totalPages", pageInfo.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortType", sortType);
        model.addAttribute("reverseSortType", reverseSortType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("users", brands);

        return "brands/brands";
    }
}
