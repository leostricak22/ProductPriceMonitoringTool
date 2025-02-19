package hr.tvz.productpricemonitoringtool.util;

import hr.tvz.productpricemonitoringtool.model.Entity;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;

public class ComboBoxUtil {

    public static <T extends Entity> void comboBoxStringConverter(ComboBox<T> comboBox) {
        comboBox.setCellFactory(cellData -> new ListCell<T>() {
            public void updateItem(T object, boolean empty) {
                super.updateItem(object, empty);

                if (empty || object == null) {
                    setText(null);
                } else {
                    setText(object.getName());
                }
            }
        });

        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(T object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public T fromString(String string) {
                return comboBox.getItems().stream()
                        .filter(object -> object.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }
}