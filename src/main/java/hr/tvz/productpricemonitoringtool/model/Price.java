package hr.tvz.productpricemonitoringtool.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Price model.
 * Represents the price in the model.
 */
public record Price(BigDecimal value) implements Serializable {

}