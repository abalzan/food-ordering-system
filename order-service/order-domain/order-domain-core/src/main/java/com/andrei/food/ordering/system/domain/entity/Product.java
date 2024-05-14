package com.andrei.food.ordering.system.domain.entity;

import com.andrei.food.ordering.system.domain.domain.entity.BaseEntity;
import com.andrei.food.ordering.system.domain.domain.valueobject.Money;
import com.andrei.food.ordering.system.domain.domain.valueobject.ProductId;

public class Product extends BaseEntity<ProductId> {
    private String name;
    private Money price;

    public Product(ProductId productId, String name, Money price) {
        super.setId(productId);
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }
}