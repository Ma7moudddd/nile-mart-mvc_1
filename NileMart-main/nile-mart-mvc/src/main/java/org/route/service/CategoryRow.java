package org.route.service;

import lombok.Value;
import org.route.model.Category;

@Value
public class CategoryRow {
    Category category;
    String displayName;
    String parentName;
}
