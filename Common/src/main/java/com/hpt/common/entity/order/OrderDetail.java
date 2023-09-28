package com.hpt.common.entity.order;

import com.hpt.common.entity.IdBasedEntity;
import com.hpt.common.entity.product.Product;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "order_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail extends IdBasedEntity {
    private int quantity;
    private float productCost;
    private float shippingCost;
    private float unitPrice;
    private float subtotal;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Override
    public String toString() {
        return "OrderDetail{" + "id=" + id + ", quantity=" + quantity + ", productCost=" + productCost +
                ", shippingCost=" + shippingCost + ", unitPrice=" + unitPrice + ", subtotal=" + subtotal + '}';
    }
}
