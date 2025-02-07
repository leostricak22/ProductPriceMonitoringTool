module hr.tvz.productpricemonitoringtool {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.slf4j;
    requires java.naming;
    requires jdk.compiler;


    opens hr.tvz.productpricemonitoringtool to javafx.fxml;
    exports hr.tvz.productpricemonitoringtool.main;
    exports hr.tvz.productpricemonitoringtool.controller;
}