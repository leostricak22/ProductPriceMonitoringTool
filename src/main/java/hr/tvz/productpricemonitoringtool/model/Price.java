package hr.tvz.productpricemonitoringtool.model;

import java.io.Serializable;
import java.math.BigDecimal;

public record Price(BigDecimal value) implements Serializable {

}