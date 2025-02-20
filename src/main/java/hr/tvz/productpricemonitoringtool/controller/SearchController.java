package hr.tvz.productpricemonitoringtool.controller;

/**
 * Interface for the search controller.
 * Contains methods for filtering, initializing and removing filters.
 */
public interface SearchController {

    void filter();
    void initialize();
    void removeFilters();

    void handleAddNewButtonClick();
    void handleEditButtonClick();
    void handleDeleteButtonClick();
}
