package hr.tvz.productpricemonitoringtool.controller;

import hr.tvz.productpricemonitoringtool.model.Company;
import hr.tvz.productpricemonitoringtool.util.SceneLoader;
import hr.tvz.productpricemonitoringtool.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Optional;

public class CompanyDashboardController {

    @FXML
    public Label companyNameLabel;


    public void initialize() {
        Company selectedCompany;
        selectedCompany = Session.getSelectedCompany().orElseThrow(() -> new RuntimeException("No company selected."));
        companyNameLabel.setText(selectedCompany.getName());
    }

    public void handleCompanyLogout() {
        Session.setSelectedCompany(Optional.empty());
        SceneLoader.loadScene("dashboard", "Dashboard");
    }
}
