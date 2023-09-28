package com.hpt.common.entity.product;

import com.hpt.common.entity.IdBasedEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_details")
public class ProductDetail extends IdBasedEntity {
    @Column(nullable = false, length = 255)
    private String name;
    @Column(nullable = false, length = 255)
    private String value;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
