package hr.tvz.productpricemonitoringtool.model;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public class FilterSearch {

    private Optional<BigDecimal> priceFromValue = Optional.empty();
    private Optional<BigDecimal> priceToValue = Optional.empty();
    private Optional<Set<Company>> companiesInRadius = Optional.empty();
    private Optional<String> productName = Optional.empty();

    public Optional<BigDecimal> getPriceFromValue() {
        return priceFromValue;
    }

    public void setPriceFromValue(Optional<BigDecimal> priceFromValue) {
        this.priceFromValue = priceFromValue;
    }

    public Optional<BigDecimal> getPriceToValue() {
        return priceToValue;
    }

    public void setPriceToValue(Optional<BigDecimal> priceToValue) {
        this.priceToValue = priceToValue;
    }

    public Optional<Set<Company>> getCompaniesInRadius() {
        return companiesInRadius;
    }

    public void setCompaniesInRadius(Optional<Set<Company>> companiesInRadius) {
        this.companiesInRadius = companiesInRadius;
    }

    public Optional<String> getProductName() {
        return productName;
    }

    public void setProductName(Optional<String> productName) {
        this.productName = productName;
    }
}
