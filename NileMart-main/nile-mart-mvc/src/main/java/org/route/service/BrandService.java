package org.route.service;

import org.route.exception.BusinessRuleException;
import org.route.model.Brand;
import org.route.repository.BrandRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    public List<Brand> findActive() {
        return brandRepository.findAll().stream().filter(Brand::isActive).toList();
    }

    public Optional<Brand> findBySlug(String slug) {
        if (slug == null || slug.isBlank()) {
            return Optional.empty();
        }
        return brandRepository.findBySlug(slug);
    }

    public Brand create(Brand brand) {
        String name = brand.getName().trim();
        String slug = brand.getSlug().trim();
        brand.setName(name);
        brand.setSlug(slug);

        Map<String, String> errors = new LinkedHashMap<>();
        boolean nameTaken = brandRepository.findAll().stream()
                .anyMatch(b -> b.getName().equalsIgnoreCase(name));
        if (nameTaken) {
            errors.put("name", "Brand name already exists");
        }
        if (brandRepository.findBySlug(slug).isPresent()) {
            errors.put("slug", "Slug already exists");
        }
        if (!errors.isEmpty()) {
            throw new BusinessRuleException(errors);
        }
        return brandRepository.save(brand);
    }
}
