package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.enumeration.CompanyProductRecordType;
import hr.tvz.productpricemonitoringtool.exception.DatabaseConnectionActiveException;
import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.model.CompanyProduct;
import hr.tvz.productpricemonitoringtool.model.Product;
import hr.tvz.productpricemonitoringtool.repository.CompanyProductReadRepository;
import hr.tvz.productpricemonitoringtool.util.AlertDialog;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CompanyProductChartController {

    @FXML public LineChart<String, BigDecimal> productPriceLineChart;
    @FXML public Label chartNameLabel;

    private final CompanyProductReadRepository companyProductReadRepository = new CompanyProductReadRepository();

    public void initialize(Company company) {
        if (Session.getSelectedProduct().isEmpty()) {
            AlertDialog.showErrorDialog("Please select a product first.");
            SceneLoader.loadScene("dashboard", "Dashboard");
        }
        
        Product selectedProduct = Session.getSelectedProduct().get();
        List<CompanyProduct> companyProducts = new ArrayList<>();

        try {
            companyProducts = companyProductReadRepository.findByProductIdAndCompanyId(selectedProduct.getId(),
                            company.getId(),
                            CompanyProductRecordType.ALL_RECORDS)
                    .stream().sorted(Comparator.comparing(CompanyProduct::getCreatedAt))
                    .toList();
        } catch (DatabaseConnectionActiveException e) {
            AlertDialog.showErrorDialog("Please try again later.");
            SceneLoader.loadScene("dashboard", "Dashboard");
        }

        createChart(companyProducts, company, selectedProduct);
    }

    private void createChart(List<CompanyProduct> companyProducts, Company company, Product selectedProduct) {
        XYChart.Series<String, BigDecimal> series = new XYChart.Series<>();
        for (CompanyProduct companyProduct : companyProducts) {
            String formattedDate = companyProduct.getCreatedAt().format(
                    DateTimeFormatter.ofPattern("dd.MM.yy. HH:mm"));
            series.getData().add(new XYChart.Data<>(
                    formattedDate,
                    companyProduct.getPrice().value()
            ));
        }

        productPriceLineChart.getData().add(series);
        chartNameLabel.setText(selectedProduct.getName() + " in " + company.getName());
    }
}