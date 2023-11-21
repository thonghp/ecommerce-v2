package com.hpt.backend.order;

import com.hpt.backend.paging.PagingAndSortingHelper;
import com.hpt.backend.setting.country.CountryRepository;
import com.hpt.common.entity.Country;
import com.hpt.common.entity.order.Order;
import com.hpt.common.entity.order.OrderStatus;
import com.hpt.common.entity.order.OrderTrack;
import com.hpt.common.exception.OrderNotFoundException;
import com.hpt.common.utils.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static com.hpt.common.utils.CommonUtils.ASCENDING;

@Service
@Transactional
public class OrderService {
    private static final int ORDERS_PER_PAGE = 10;
    @Autowired
    private OrderRepository repo;
    @Autowired
    private CountryRepository countryRepo;

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
     * Returns an order by ID
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

    /**
     * Returns a list of countries sorted by name in ascending order
     *
     * @return a list of countries
     */
    public List<Country> listAllCountries() {
        return countryRepo.findAllByOrderByNameAsc();
    }

    /**
     * Saves an order to the database
     *
     * @param orderInForm The order to be saved
     */
    public void save(Order orderInForm) {
        Order orderInDB = repo.findById(orderInForm.getId()).get();
        orderInForm.setOrderTime(orderInDB.getOrderTime());
        orderInForm.setCustomer(orderInDB.getCustomer());

        repo.save(orderInForm);
    }

    /**
     * Updates the status of the order specified by ID
     *
     * @param orderId The ID of the order to be updated
     * @param status  The new status of the order
     */
    public void updateStatus(Integer orderId, String status) {
        Order orderInDB = repo.findById(orderId).get();
        OrderStatus statusToUpdate = OrderStatus.valueOf(status);

        if (!orderInDB.hasStatus(statusToUpdate)) {
            List<OrderTrack> orderTracks = orderInDB.getOrderTracks();

            OrderTrack track = new OrderTrack();
            track.setOrder(orderInDB);
            track.setStatus(statusToUpdate);
            track.setUpdatedTime(new Date());
            track.setNotes(statusToUpdate.defaultDescription());

            orderTracks.add(track);

            orderInDB.setStatus(statusToUpdate);

            repo.save(orderInDB);
        }
    }
}
