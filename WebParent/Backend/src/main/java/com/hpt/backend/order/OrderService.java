package com.hpt.backend.order;

import com.hpt.backend.paging.PagingAndSortingHelper;
import com.hpt.common.entity.order.Order;
import com.hpt.common.exception.OrderNotFoundException;
import com.hpt.common.utils.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

import static com.hpt.common.utils.CommonUtils.ASCENDING;

@Service
@Transactional
public class OrderService {
    private static final int ORDERS_PER_PAGE = 10;
    @Autowired
    private OrderRepository repo;

    /**
     * Returns a list of orders by page number and sorted ascending or descending by the specified column.
     *
     * @param pageNum The page requests to return data
     * @param helper  The helper to get sorting type, sorting field and keyword
     * @return a list of orders
     */
    public List<Order> listByPage(int pageNum, PagingAndSortingHelper helper) {
        String sortField = helper.getSortField();
        String sortType = helper.getSortType();
        String keyword = helper.getKeyword();

        Sort sort = null;
        if ("destination".equals(sortField)) {
            sort = Sort.by("country").and(Sort.by("state")).and(Sort.by("city"));
        } else {
            sort = Sort.by(sortField);
        }
        sort = sortType.equals(ASCENDING) ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);
        Page<Order> page = (keyword != null) ? repo.findAll(keyword, pageable) : repo.findAll(pageable);
        PageInfo pageInfo = new PageInfo();

        pageInfo.setTotalPages(page.getTotalPages());
        pageInfo.setTotalElements(page.getTotalElements());

        List<Order> results = page.getContent();

        helper.passPaginationAttribute(pageNum, results, pageInfo, ORDERS_PER_PAGE);

        return results;
    }

    /**
     * Returns a order by ID
     *
     * @param id The ID of the order to return
     * @return a order
     * @throws OrderNotFoundException if no orders found with the given ID
     */
    public Order get(Integer id) throws OrderNotFoundException {
        try {
            return repo.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new OrderNotFoundException("Could not find any orders with ID " + id);
        }
    }

    /**
     * delete a order by ID
     *
     * @param id The ID of the order to delete
     * @throws OrderNotFoundException if no orders found with the given ID
     */
    public void delete(Integer id) throws OrderNotFoundException {
        Long count = repo.countById(id);
        if (count == null || count == 0) {
            throw new OrderNotFoundException("Could not find any orders with ID " + id);
        }

        repo.deleteById(id);
    }
}