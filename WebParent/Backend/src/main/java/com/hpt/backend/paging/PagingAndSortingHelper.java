package com.hpt.backend.paging;

import com.hpt.common.utils.PageInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

import static com.hpt.common.utils.CommonUtils.ASCENDING;

public class PagingAndSortingHelper {
    private ModelAndViewContainer model;
    private String listName;
    private String sortField;
    private String sortType;
    private String keyword;

    public PagingAndSortingHelper(ModelAndViewContainer mavContainer, String listName, String sortField,
                                  String sortType, String keyword) {
        this.model = mavContainer;
        this.listName = listName;
        this.sortField = sortField;
        this.sortType = sortType;
        this.keyword = keyword;
    }

    /**
     * Returns a paginated list of default or search object and sorted ascending or descending by the specified column.
     *
     * @param pageNum  The page requests to return data
     * @param pageSize The number of records per page
     * @param repo     The repository to query
     * @return a list of object
     */
    public List<?> listByPage(int pageNum, int pageSize, SearchRepository<?, Integer> repo) {
        Sort sort = Sort.by(sortField);
        sort = sortType.equals(ASCENDING) ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<?> page = (keyword != null) ? repo.findAll(keyword, pageable) : repo.findAll(pageable);
        PageInfo pageInfo = new PageInfo();

        pageInfo.setTotalPages(page.getTotalPages());
        pageInfo.setTotalElements(page.getTotalElements());

        List<?> results = page.getContent();

        passPaginationAttribute(pageNum, results, pageInfo, pageSize);

        return page.getContent();
    }

    public void passPaginationAttribute(int pageNum, List<?> page, PageInfo pageInfo, int pageSize) {
        long startCount = pageInfo.getStartCount(pageNum, pageSize);
        long endCount = pageInfo.getEndCount(pageNum, pageSize);

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", pageInfo.getTotalPages());
        model.addAttribute("totalItems", pageInfo.getTotalElements());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute(listName, page);
    }

    public String getSortField() {
        return sortField;
    }

    public String getSortType() {
        return sortType;
    }

    public String getKeyword() {
        return keyword;
    }
}
