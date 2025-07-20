package com.spring.springbootapplication.dto;

public class ChartDataDto {

    private String categoryName;

    private String yearMonth;

    private int totalHours;

    public ChartDataDto(String categoryName, String yearMonth, int totalHours) {
        this.categoryName = categoryName;
        this.yearMonth = yearMonth;
        this.totalHours = totalHours;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }
}
