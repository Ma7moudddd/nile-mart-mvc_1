package org.route.service;

import org.route.exception.BusinessRuleException;
import org.route.model.Category;
import org.route.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CategoryService {

    private static final Comparator<Category> BY_ORDER = Comparator.comparingInt(Category::getDisplayOrder);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public boolean isTopLevel(Category category) {
        return category.getParentSlug() == null || category.getParentSlug().isEmpty();
    }

    public Optional<Category> findBySlug(String slug) {
        if (slug == null || slug.isBlank()) {
            return Optional.empty();
        }
        return categoryRepository.findBySlug(slug);
    }

    public List<Category> findTopLevel() {
        return categoryRepository.findAll().stream()
                .filter(this::isTopLevel)
                .sorted(BY_ORDER)
                .toList();
    }

    public List<Category> findSubCategories() {
        List<Category> result = new ArrayList<>();
        for (Category top : findTopLevel()) {
            result.addAll(findSubCategoriesOf(top.getSlug()));
        }
        return result;
    }

    public List<Category> findSubCategoriesOf(String parentSlug) {
        return categoryRepository.findAll().stream()
                .filter(c -> parentSlug.equals(c.getParentSlug()))
                .sorted(BY_ORDER)
                .toList();
    }

    public boolean isSubCategory(String slug) {
        return findBySlug(slug).map(c -> !isTopLevel(c)).orElse(false);
    }

    public String nameOf(String slug) {
        return findBySlug(slug).map(Category::getName).orElse("—");
    }

    public List<CategoryRow> listRows() {
        List<CategoryRow> rows = new ArrayList<>();
        for (Category top : findTopLevel()) {
            rows.add(new CategoryRow(top, top.getName(), "—"));
            for (Category sub : findSubCategoriesOf(top.getSlug())) {
                rows.add(new CategoryRow(sub, "— " + sub.getName(), top.getName()));
            }
        }
        return rows;
    }

    public Category create(Category category) {
        String name = category.getName().trim();
        String slug = category.getSlug().trim();
        String parentSlug = category.getParentSlug() == null ? "" : category.getParentSlug().trim();
        category.setName(name);
        category.setSlug(slug);
        category.setParentSlug(parentSlug);

        Map<String, String> errors = new LinkedHashMap<>();

        if (categoryRepository.findBySlug(slug).isPresent()) {
            errors.put("slug", "Slug already exists");
        }

        String parentName = null;
        if (!parentSlug.isEmpty()) {
            Optional<Category> parent = categoryRepository.findBySlug(parentSlug);
            if (parent.isEmpty()) {
                errors.put("parentSlug", "Parent category does not exist");
            } else if (!isTopLevel(parent.get())) {
                errors.put("parentSlug", "Parent must be a top-level category");
            } else {
                parentName = parent.get().getName();
            }
        }

        if (!errors.containsKey("parentSlug")) {
            boolean nameTaken = categoryRepository.findAll().stream()
                    .filter(c -> parentSlug.equals(c.getParentSlug() == null ? "" : c.getParentSlug()))
                    .anyMatch(c -> c.getName().equalsIgnoreCase(name));
            if (nameTaken) {
                String where = parentName == null ? "the top level" : parentName;
                errors.put("name", "A category with this name already exists under " + where);
            }
        }

        if (!errors.isEmpty()) {
            throw new BusinessRuleException(errors);
        }
        return categoryRepository.save(category);
    }
}
