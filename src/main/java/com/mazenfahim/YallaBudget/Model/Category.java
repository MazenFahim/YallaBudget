package com.mazenfahim.YallaBudget.Model;

public class Category {
    private int Id;
    private String CategoryName;
    private String Description;

    public Category(int id,String name,String Description){
        this.Id=id;
        this.CategoryName=name;
        this.Description=Description;
    }

    public int getId() {
        return Id;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public String getDescription() {
        return Description;
    }
}
