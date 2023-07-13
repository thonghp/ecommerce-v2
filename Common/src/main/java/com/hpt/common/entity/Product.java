package com.hpt.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, length = 256, nullable = false)
    private String name;
    @Column(unique = true, length = 256, nullable = false)
    private String alias;
    @Column(length = 512, nullable = false, name = "short_description")
    private String shortDescription;
    @Column(length = 4096, nullable = false, name = "full_description")
    private String fullDescription;
    @Column(name = "created_time")
    private Date createdTime;
    @Column(name = "updated_time")
    private Date updatedTime;
    private boolean enabled;
    @Column(name = "in_stock")
    private boolean inStock;
    private float cost;
    private float price;
    @Column(name = "discount_percent")
    private float discountPercent;
    private float length;
    private float width;
    private float height;
    private float weight;
    @Column(name = "main_image", nullable = false)
    private String mainImage;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImage> images = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductDetail> details = new HashSet<>();

    public void addExtraImage(String imageName) {
        ProductImage images = new ProductImage();
        images.setName(imageName);
        images.setProduct(this);

        this.images.add(images);
    }

    public void addDetail(String name, String value) {
        ProductDetail detail = new ProductDetail();
        detail.setName(name);
        detail.setValue(value);
        detail.setProduct(this);

        this.details.add(detail);
    }

    public void addDetail(Integer id, String name, String value) {
        ProductDetail detail = new ProductDetail();
        detail.setId(id);
        detail.setName(name);
        detail.setValue(value);
        detail.setProduct(this);

        this.details.add(detail);
    }

    public boolean containsImageName(String imageName) {
        Iterator<ProductImage> iterator = images.iterator();

        while (iterator.hasNext()) {
            ProductImage image = iterator.next();
            if (image.getName().equals(imageName)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "Product{" + "id=" + id + ", name='" + name + '\'' + '}';
    }

    @Transient
    public String getMainImagePath() {
        if (id == null || mainImage == null) return "/images/image-thumbnail.png";

        return "/product-photos/" + this.id + "/" + this.mainImage;
    }

    @Transient
    public String getShortName() {
        if (name.length() > 70) {
            return name.substring(0, 70).concat("...");
        }
        return name;
    }
}
