package org.route.nilemartspringboot.mapper;

import javax.annotation.processing.Generated;
import org.route.nilemartspringboot.dto.category.AddCategoryRequest;
import org.route.nilemartspringboot.dto.category.CategoryResponse;
import org.route.nilemartspringboot.model.Category;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-19T19:32:48+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26.0.1 (Oracle Corporation)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public CategoryResponse toCategoryResponse(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryResponse categoryResponse = new CategoryResponse();

        categoryResponse.setIsActive( category.isActive() );
        categoryResponse.setId( category.getId() );
        categoryResponse.setName( category.getName() );
        categoryResponse.setSlug( category.getSlug() );

        return categoryResponse;
    }

    @Override
    public Category toCategory(AddCategoryRequest request) {
        if ( request == null ) {
            return null;
        }

        Category.CategoryBuilder category = Category.builder();

        category.id( request.getId() );
        category.name( request.getName() );
        category.slug( request.getSlug() );
        category.active( request.isActive() );

        return category.build();
    }
}
