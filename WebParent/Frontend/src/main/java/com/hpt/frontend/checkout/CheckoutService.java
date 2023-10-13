package com.hpt.frontend.checkout;

import com.hpt.common.entity.CartItem;
import com.hpt.common.entity.ShippingRate;
import com.hpt.common.entity.product.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckoutService {
    // Divisor lấy theo đơn vị sử dụng của FedEx
    private static final int DIM_DIVISOR = 139;

    /**
     * Return information checkout including product total, shipping cost total, payment total, product cost,
     * deliver days and COD supported
     *
     * @param cartItems    List of cart items have been added to the cart
     * @param shippingRate Shipping rate of the area where the customer lives
     * @return Information checkout
     */
    public CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate) {
        CheckoutInfo checkoutInfo = new CheckoutInfo();

        float productTotal = calculateProductTotal(cartItems);
        float shippingCostTotal = calculateShippingCost(cartItems, shippingRate);
        float paymentTotal = productTotal + shippingCostTotal;
        float productCost = calculateProductCost(cartItems);

        checkoutInfo.setProductTotal(productTotal);
        checkoutInfo.setShippingCostTotal(shippingCostTotal);
        checkoutInfo.setPaymentTotal(paymentTotal);
        checkoutInfo.setProductCost(productCost);

        checkoutInfo.setDeliverDays(shippingRate.getDays());
        checkoutInfo.setCodSupported(shippingRate.isCodSupported());

        return checkoutInfo;
    }

    /**
     * Return total cost of all products in the cart without shipping cost
     *
     * @param cartItems List of cart items have been added to the cart
     * @return Total cost of all products
     */
    private float calculateProductTotal(List<CartItem> cartItems) {
        float total = 0.0f;

        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }

        return total;
    }

    /**
     * Calculate shipping cost of all products in the cart and set shipping cost for each item
     *
     * @param cartItems    List of cart items have been added to the cart
     * @param shippingRate Shipping rate of the area where the customer lives
     * @return Total shipping cost of all products in the cart
     */
    private float calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {
        float shippingCostTotal = 0.0f;

        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
            float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;
            float shippingCost = finalWeight * item.getQuantity() * shippingRate.getRate();

            item.setShippingCost(shippingCost); // Set shipping cost for each item to display in the cart

            shippingCostTotal += shippingCost;
        }

        return shippingCostTotal;
    }

    /**
     * Calculate the total original price of all products in the shopping cart without shipping cost
     *
     * @param cartItems List of cart items have been added to the cart
     * @return Total original price of all products in the shopping cart
     */
    private float calculateProductCost(List<CartItem> cartItems) {
        float cost = 0.0f;

        for (CartItem item : cartItems) {
            cost += item.getQuantity() * item.getProduct().getCost();
        }

        return cost;
    }
}
