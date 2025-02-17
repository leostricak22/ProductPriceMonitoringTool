module hr.tvz.productpricemonitoringtool {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.slf4j;
    requires java.naming;
    requires jdk.compiler;
    requires javafx.web;
    requires javafx.graphics;
    requires javafx.media;
    requires jdk.jsobject;

    opens hr.tvz.productpricemonitoringtool to javafx.fxml;
    opens hr.tvz.productpricemonitoringtool.util to javafx.web;
    exports hr.tvz.productpricemonitoringtool.main;
    exports hr.tvz.productpricemonitoringtool.controller;
}