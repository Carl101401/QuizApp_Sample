package com.example.myquizapplication.Models;

public class CategoryModel2 {

    private String categoryName, categoryImage, key;
    int setNum;

    public CategoryModel2(String categoryName, String categoryImage, String key, int setNum) {
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
        this.key = key;
        this.setNum = setNum;
    }

    public CategoryModel2(){
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getSetNum() {
        return setNum;
    }

    public void setSetNum(int setNum) {
        this.setNum = setNum;
    }
}
