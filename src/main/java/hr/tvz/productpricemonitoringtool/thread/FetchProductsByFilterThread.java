package hr.tvz.productpricemonitoringtool.thread;

import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.FilterSearch;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.repository.CompanyProductRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FetchProductsByFilterThread implements Runnable {

    private final FilterSearch filterSearch;
    private final CompanyProductRepository companyProductRepository;
    private final List<Product> products;

    public FetchProductsByFilterThread(FilterSearch filterSearch, CompanyProductRepository companyProductRepository, List<Product> products) {
        this.filterSearch = filterSearch;
        this.companyProductRepository = companyProductRepository;
        this.products = products;
    }

    @Override
    public void run() {
        try {
            Set<Long> productIds = new HashSet<>();
            Set<Product> uniqueProducts = new HashSet<>();

            companyProductRepository.findAll().stream()
                    .filter(companyProduct -> filterSearch.getCompaniesInRadius().isEmpty() ||
                            filterSearch.getCompaniesInRadius().get().stream()
                                    .anyMatch(company -> company.getId().equals(companyProduct.getCompany().getId())))
                    .filter(companyProduct ->
                            filterSearch.getPriceFromValue().isEmpty() ||
                                    companyProduct.getPrice().value().compareTo(
                                            filterSearch.getPriceFromValue().get()) >= 0)
                    .filter(companyProduct ->
                            filterSearch.getPriceToValue().isEmpty() ||
                                    companyProduct.getPrice().value().compareTo(
                                            filterSearch.getPriceToValue().get()) <= 0)
                    .filter(companyProduct ->
                            filterSearch.getProductName().isEmpty() ||
                                    companyProduct.getProduct().getName().toLowerCase().contains(
                                            filterSearch.getProductName().get().toLowerCase()))
                    .forEach(companyProduct -> {
                        Product product = companyProduct.getProduct();
                        if (productIds.add(product.getId())) {
                            uniqueProducts.add(product);
                        }
                    });

            products.clear();
            products.addAll(uniqueProducts);
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Failed to fetch data from database.");
        }
    }
}