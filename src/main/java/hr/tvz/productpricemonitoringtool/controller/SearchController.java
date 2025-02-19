package hr.tvz.productpricemonitoringtool.controller;

public interface SearchController {

    void filter();
    void initialize();
    void removeFilters();

    void handleAddNewButtonClick();
    void handleEditButtonClick();
    void handleDeleteButtonClick();
}
