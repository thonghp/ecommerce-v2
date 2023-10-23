package com.hpt.frontend.order;

import com.hpt.common.entity.Address;
import com.hpt.common.entity.CartItem;
import com.hpt.common.entity.Customer;
import com.hpt.common.entity.order.Order;
import com.hpt.common.entity.order.OrderDetail;
import com.hpt.common.entity.order.OrderStatus;
import com.hpt.common.entity.order.PaymentMethod;
import com.hpt.common.entity.product.Product;
import com.hpt.frontend.checkout.CheckoutInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {
    @Autowired
    private OrderRepository repo;

    public Order createOrder(Customer customer, Address address, List<CartItem> cartItems,
                             PaymentMethod paymentMethod, CheckoutInfo checkoutInfo) {
        Order newOrder = new Order();
        newOrder.setOrderTime(new Date());
        newOrder.setStatus(OrderStatus.NEW);
        newOrder.setCustomer(customer);
        newOrder.setProductCost(checkoutInfo.getProductCost());
        newOrder.setSubtotal(checkoutInfo.getProductTotal());
        newOrder.setShippingCost(checkoutInfo.getShippingCostTotal());
        newOrder.setTax(0.0f);
        newOrder.setTotal(checkoutInfo.getPaymentTotal());
        newOrder.setPaymentMethod(paymentMethod);
        newOrder.setDeliverDays(checkoutInfo.getDeliverDays());
        newOrder.setDeliverDate(checkoutInfo.getDeliverDate());

        if (address == null) {
            newOrder.copyAddressFromCustomer();
        } else {
            newOrder.copyShippingAddress(address);
        }

        Set<OrderDetail> orderDetails = newOrder.getOrderDetails();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(newOrder);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setUnitPrice(product.getDiscountPrice());
            orderDetail.setProductCost(product.getCost() * cartItem.getQuantity());
            orderDetail.setSubtotal(cartItem.getSubtotal());
            orderDetail.setShippingCost(cartItem.getShippingCost());

            orderDetails.add(orderDetail);
        }


        return repo.save(newOrder);
    }
}
