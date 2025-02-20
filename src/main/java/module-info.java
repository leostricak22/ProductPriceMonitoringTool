module hr.tvz.productpricemonitoringtool {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires java.naming;
    requires jdk.compiler;
    requires javafx.web;
    requires javafx.graphics;
    requires javafx.media;
    requires jdk.jsobject;
    requires com.h2database;
    requires java.sql;
    requires com.google.gson;
    requires jbcrypt;

    opens hr.tvz.productpricemonitoringtool to javafx.fxml;
    opens hr.tvz.productpricemonitoringtool.util to javafx.web;
    exports hr.tvz.productpricemonitoringtool.main;
    exports hr.tvz.productpricemonitoringtool.controller;
    opens hr.tvz.productpricemonitoringtool.model to com.google.gson;
    opens hr.tvz.productpricemonitoringtool.model.json to com.google.gson;
    exports hr.tvz.productpricemonitoringtool.controller.admin;
}