package hr.tvz.productpricemonitoringtool.main;

import hr.tvz.productpricemonitoringtool.model.AuditLogManager;
import hr.tvz.productpricemonitoringtool.model.Price;
import hr.tvz.productpricemonitoringtool.model.User;

import java.math.BigDecimal;

public class AuditLogTest {

    public static void main(String[] args) {
        AuditLogManager.load();

        AuditLogManager.logChange("Product Price", 10.99, 12.49, new User.Builder(1L, "Leo").build());
        AuditLogManager.logChange("Stock Status", new Price(new BigDecimal(123)), "Out of Stock", new User.Builder(2L, "Ivan").build());

        AuditLogManager.getLogs().forEach(System.out::println);
    }
}
