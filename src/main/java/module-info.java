module hr.tvz.productpricemonitoringtool {
    requires javafx.controls;
    requires javafx.fxml;


    opens hr.tvz.productpricemonitoringtool to javafx.fxml;
    exports hr.tvz.productpricemonitoringtool;
}