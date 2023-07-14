package com.hpt.backend.category;

import com.hpt.common.entity.Category;
import com.hpt.common.exception.CategoryNotFoundException;
import com.hpt.common.utils.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

import static com.hpt.common.utils.CommonUtils.*;

@Service
@Transactional
public class CategoryService {

    public static final String SORT_FIELD_NAME = "name";
    public static final int ROOT_CATEGORIES_PER_PAGE = 4;
    @Autowired
    private CategoryRepository repo;

    /**
     * Returns a list of categories in hierarchical order and sorted ascending or descending by the name field for
     * display in the list view
     *
     * @return List of categories are sorted ascending or descending by the name field
     */
    public List<Category> listAll(String sortType) {
        Sort sort = Sort.by(SORT_FIELD_NAME);
        sort = sortType.equals(ASCENDING) ? sort.ascending() : sort.descending();

        List<Category> rootCategories = repo.findByParentIsNull(sort);

        return listHierarchicalCategories(rootCategories, sortType);
    }

    /**
     * Returns a list of categories in hierarchical order and sorted ascending or descending
     *
     * @return List of hierarchical categories are sorted ascending or descending
     */
    private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortType) {
        List<Category> hierarchicalCategories = new ArrayList<>();

        for (Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.copyFull(rootCategory));
            Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortType);

            for (Category subCategory : children) {
                String name = "--" + subCategory.getName();
                hierarchicalCategories.add(Category.copyFull(subCategory, name));

                listSubCategories(hierarchicalCategories, subCategory, 1, sortType);
            }
        }

        return hierarchicalCategories;
    }


    /**
     * Returns a list of children categories in hierarchical order and sorted ascending or descending
     *
     * @param categories List of categories
     * @param parent     Parent category
     * @param subLevel   Sub level
     */
    public void listSubCategories(List<Category> categories, Category parent, int subLevel, String sortType) {
        Set<Category> children = sortSubCategories(parent.getChildren(), sortType);
        int newSubLevel = subLevel + 1;

        for (Category subCategory : children) {
            String name = "";

            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }

            categories.add(Category.copyFull(subCategory, name + subCategory.getName()));

            listSubCategories(categories, subCategory, newSubLevel, sortType);
        }
    }

    /**
     * Returns a list of categories with only id and name in hierarchical order and sorted ascending by name field for
     * use in the form
     *
     * @return List of hierarchical categories with only id and name are sorted ascending by name field
     */
    public List<Category> listHierarchicalCategoriesInform() {
        List<Category> hierarchicalCategories = new ArrayList<>();
        List<Category> rootCategories = repo.findByParentIsNull(Sort.by(SORT_FIELD_NAME).ascending());

        for (Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.copyIdAndName(rootCategory));
            Set<Category> children = sortSubCategories(rootCategory.getChildren());

            for (Category subCategory : children) {
                Integer id = subCategory.getId();
                String name = "--" + subCategory.getName();
                hierarchicalCategories.add(Category.copyIdAndName(id, name));

                listSubCategoriesInform(hierarchicalCategories, subCategory, 1);
            }
        }

        return hierarchicalCategories;
    }

    /**
     * Returns a list of children categories with only id and name in hierarchical order to use in the form
     *
     * @param categories List of categories
     * @param parent     Parent category
     * @param subLevel   Sub level
     */
    public void listSubCategoriesInform(List<Category> categories, Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = sortSubCategories(parent.getChildren());

        for (Category subCategory : children) {
            String name = "";

            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }

            Category child = Category.copyIdAndName(subCategory.getId(), name + subCategory.getName());
            categories.add(child);

            listSubCategoriesInform(categories, subCategory, newSubLevel);
        }
    }

    /**
     * Save category information. If the id does not exist, save the category, otherwise the id already exists,
     * update the category
     *
     * @param category category object to save
     * @return saved category object
     */
    public Category save(Category category) {
        if (category.getAlias() == null || category.getAlias().isEmpty()) {
            String defaultAlias = category.getName().replaceAll(" ", "-");
            category.setAlias(defaultAlias);
        } else {
            category.setAlias(category.getAlias().replaceAll(" ", "-"));
        }
        Category parent = category.getParent();
        if (parent != null) {
            String allParentIds = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
            allParentIds += parent.getId() + "-";
            category.setAllParentIDs(allParentIds);
        }

        return repo.save(category);
    }

    /**
     * Get a category by id
     *
     * @param id id of the category
     * @return category object corresponding to id
     * @throws CategoryNotFoundException if the category does not exist
     */
    public Category get(Integer id) throws CategoryNotFoundException {
        try {
            return repo.findById(id).get();
        } catch (Exception ex) {
            throw new CategoryNotFoundException("Không tìm thấy thể loại có id là " + id);
        }
    }

    /**
     * Check category name and alias is unique
     *
     * @param id    id of the category
     * @param name  name of the category
     * @param alias alias of the category
     * @return String "OK" if the category name and alias is unique, otherwise return "DuplicateName" or
     * "DuplicateAlias" if the name or alias is not unique
     */
    public String checkUnique(Integer id, String name, String alias) {
        boolean isCreatingNew = (id == null || id == 0);

        Category categoryByName = repo.findByName(name);

        if (isCreatingNew) {
            if (categoryByName != null) {
                return "Duplicate name";
            } else {
                Category categoryByAlias = repo.findByAlias(alias);
                if (categoryByAlias != null) return "Duplicate alias";
            }
        } else {
            if (categoryByName != null && categoryByName.getId() != id) return "Duplicate name";

            Category categoryByAlias = repo.findByAlias(alias);
            if (categoryByAlias != null && categoryByAlias.getId() != id) return "Duplicate alias";
        }

        return "OK";
    }

    /**
     * Sorts a set of categories by name in ascending or descending order
     *
     * @param children Set of categories to sort
     * @param sortType Sort type (asc or desc)
     * @return Sorted set of categories
     */
    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortType) {
        SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
            @Override
            public int compare(Category cat1, Category cat2) {
                if (sortType.equals(ASCENDING)) {
                    return cat1.getName().compareTo(cat2.getName());
                } else {
                    return cat2.getName().compareTo(cat1.getName());
                }
            }
        });

        sortedChildren.addAll(children);

        return sortedChildren;
    }

    /**
     * Sorts a set of categories by name in ascending order
     *
     * @param children Set of categories to sort
     * @return Sorted set of categories in ascending order
     */
    private SortedSet<Category> sortSubCategories(Set<Category> children) {
        return sortSubCategories(children, ASCENDING);
    }

    /**
     * Update the enabled status of a category
     *
     * @param id      id of the category
     * @param enabled enabled status
     */
    public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
        repo.updateEnabledStatus(id, enabled);
    }

    /**
     * Delete a category by id
     *
     * @param id id of the category you want to delete
     * @throws CategoryNotFoundException if the category does not exist
     */
    public void delete(Integer id) throws CategoryNotFoundException {
        Long countById = repo.countById(id);
        if (countById == null || countById == 0) {
            throw new CategoryNotFoundException("Không tìm thấy thể loại có id là " + id);
        }
        repo.deleteById(id);
    }

    /**
     * Returns 1 page of hierarchical category list sorted ascending or descending and paginated
     *
     * @param pageInfo object containing pagination information
     * @param pageNum  Page number
     * @param sortType Sort type (asc or desc)
     * @param keyword  Keyword to search
     * @return 1 page of hierarchical category list sorted and paginated
     */
    public List<Category> listByPage(PageInfo pageInfo, int pageNum, String sortType, String keyword) {
        Sort sort = Sort.by(SORT_FIELD_NAME);
        sort = sortType.equals(ASCENDING) ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);

        Page<Category> pageCategories = (keyword != null && !keyword.isEmpty()) ? repo.search(keyword, pageable) :
                repo.findByParentIsNull(pageable);

        List<Category> rootCategories = pageCategories.getContent();

        pageInfo.setTotalPages(pageCategories.getTotalPages());
        pageInfo.setTotalElements(pageCategories.getTotalElements());

        if (keyword != null && !keyword.isEmpty()) {
            List<Category> searchResult = pageCategories.getContent();
            for (Category category : searchResult) {
// Nếu không set thì category cha cũng hiển thị nút xoá khi category con còn tồn tại trong danh sách trả về khi tìm kiếm
                category.setHasChildren(category.getChildren().size() > 0);
            }

            return searchResult;

        } else {
            return listHierarchicalCategories(rootCategories, sortType);
        }
    }
}
