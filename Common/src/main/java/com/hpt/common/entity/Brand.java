package com.hpt.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "brands")
public class Brand extends IdBasedEntity {
    @Column(nullable = false, length = 45, unique = true)
    private String name;

    @Column(nullable = false, length = 128)
    private String imagePath;

    private boolean enabled;

    @ManyToMany
    @JoinTable(name = "brand_category",
            joinColumns = @JoinColumn(name = "brand_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    public Brand(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Brand{" + "id=" + id + ", name='" + name + '\'' + ", imagePath='" + imagePath + '\'' +
                ", enabled=" + enabled + '}';
    }

    @Transient
    public String getPhotosImagePath() {
        if (id == null || imagePath == null) return "/images/image-thumbnail.png";

        return "/brand-photos/" + this.id + "/" + this.imagePath;
    }
}
