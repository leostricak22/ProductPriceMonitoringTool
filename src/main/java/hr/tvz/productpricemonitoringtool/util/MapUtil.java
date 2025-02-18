package hr.tvz.productpricemonitoringtool.util;

import hr.tvz.productpricemonitoringtool.controller.MapPickerController;
import hr.tvz.productpricemonitoringtool.model.Address;
import hr.tvz.productpricemonitoringtool.model.Coordinates;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static java.util.Objects.isNull;

public class MapUtil {

    private MapUtil() {}

    public static Coordinates getCoordinatesFromMap(String data) {
        String[] dataArray = data.split(" ");
        String longitudeString = dataArray[0];
        String latitudeString = dataArray[1];

        if (isNull(longitudeString) || isNull(latitudeString)) {
            throw new IllegalArgumentException("Longitude and latitude must be provided.");
        }
        if (!ValidationUtil.isBigDecimal(longitudeString) || !ValidationUtil.isBigDecimal(latitudeString)) {
            throw new IllegalArgumentException("Longitude and latitude must be positive numbers.");
        }

        BigDecimal longitude = new BigDecimal(longitudeString);
        BigDecimal latitude = new BigDecimal(latitudeString);

        return new Coordinates.Builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    private static final double EARTH_RADIUS_KM = 6371.0;

    public static BigDecimal calculateDistance(Coordinates coordinates1, Coordinates coordinates2) {
        double lat1 = Math.toRadians(coordinates1.getLatitude().doubleValue());
        double lon1 = Math.toRadians(coordinates1.getLongitude().doubleValue());
        double lat2 = Math.toRadians(coordinates2.getLatitude().doubleValue());
        double lon2 = Math.toRadians(coordinates2.getLongitude().doubleValue());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS_KM * c;

        return BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP);
    }

    public static Optional<Address> handleMapPick(Address address, Label addressLabel) {
        Optional<FXMLLoader> loader = SceneLoader.loadMapPickerPopupScene(
                "map_picker", "Map Picker", Optional.ofNullable(address));

        if (loader.isEmpty()) {
            AlertDialog.showErrorDialog("Error fetching data from popup window");
            return Optional.empty();
        }

        MapPickerController controller = loader.get().getController();
        if(!isNull(controller.getAddress())) {
            address = controller.getAddress();
            addressLabel.setText(address.getAddress());
        }

        return Optional.of(address);
    }
}
