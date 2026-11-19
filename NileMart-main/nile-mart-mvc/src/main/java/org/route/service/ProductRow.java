package org.route.service;

import lombok.Value;
import org.route.model.Product;

@Value
public class ProductRow {
    Product product;
    String categoryName;
    String brandName;
}
