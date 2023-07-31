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
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 128, nullable = false, unique = true)
    private String name;
    @Column(length = 64, nullable = false, unique = true)
    private String alias;
    @Column(name = "image_path", length = 128)
    private String imagePath;
    private boolean enabled;
    @Column(name = "all_parent_ids", length = 256)
    private String allParentIDs;
    @OneToOne
    @JoinColumn(name = "parent_id")
    private Category parent;
    @OneToMany(mappedBy = "parent")
    @OrderBy("name asc")
    private Set<Category> children = new HashSet<>();
    @Transient
    private boolean hasChildren;

    public static Category copyIdAndName(Category category) {
        Category copyCategory = new Category();
        copyCategory.setId(category.getId());
        copyCategory.setName(category.getName());

        return copyCategory;
    }

    public static Category copyIdAndName(Integer id, String name) {
        Category copyCategory = new Category();
        copyCategory.setId(id);
        copyCategory.setName(name);

        return copyCategory;
    }

    public static Category copyFull(Category category) {
        Category copyCategory = new Category();
        copyCategory.setId(category.getId());
        copyCategory.setName(category.getName());
        copyCategory.setAlias(category.getAlias());
        copyCategory.setImagePath(category.getImagePath());
        copyCategory.setEnabled(category.isEnabled());
        // Mục đích là nếu category có thể loại con thì nó sẽ true lúc này sẽ không hiển thị icon xoá đợi xoá hết con mới xoá được
        copyCategory.setHasChildren(category.getChildren().size() > 0);

        return copyCategory;
    }

    public static Category copyFull(Category category, String name) {
        Category copyCategory = copyFull(category);
        copyCategory.setName(name);

        return copyCategory;
    }

    @Override
    public String toString() {
        return "Category{" + "id=" + id + ", name='" + name + '\'' + ", alias='" + alias + '\'' +
                ", imagePath='" + imagePath + '\'' + ", enabled=" + enabled + '}';
    }

    @Transient
    public String getPhotosImagePath() {
        if (id == null || imagePath == null) return "/images/image-thumbnail.png";

        return "/category-photos/" + this.id + "/" + this.imagePath;
    }
}