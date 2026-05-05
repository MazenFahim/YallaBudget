package com.mazenfahim.YallaBudget.model;

/**
 * Represents an expense category such as Food or Transportation.
 */
public class Category {
    /**
     * Database identifier for the category.
     */
    private int id;
    /**
     * Display name of the category.
     */
    private String categoryName;
    /**
     * Optional description of the category.
     */
    private String description;

    /**
     * Creates a category with id, name, and description.
     *
     * @param id database identifier
     * @param name category name
     * @param description category description
     */
    public Category(int id, String name, String description) {
        this.id = id;
        this.categoryName = name;
        this.description = description;
    }

    /**
     * Returns the database identifier.
     *
     * @return category id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the display name of the category.
     *
     * @return category name
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Returns the category description.
     *
     * @return description text
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the category name for UI display.
     *
     * @return category name
     */
    @Override
    public String toString() {
        return categoryName;
    }
}
