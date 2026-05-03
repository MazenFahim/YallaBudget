package com.mazenfahim.YallaBudget.model;

public class Category {
    private int id;
    private String categoryName;
    private String description;

    public Category(int id, String name, String description) {
        this.id = id;
        this.categoryName = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return categoryName;
    }
}
