package hr.tvz.productpricemonitoringtool.util;

import hr.tvz.productpricemonitoringtool.exception.FileSaveException;
import hr.tvz.productpricemonitoringtool.main.ProductPriceMonitoringToolApplication;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

/**
 * FileUtil class.
 * Contains methods for file operations.
 */
public class FileUtil {

    private FileUtil() {}

    /**
     * Method for picking file.
     * @param extensions List of file extensions.
     */
    public static Optional<File> pickFile(List<String> extensions) {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Files", extensions);
        fileChooser.getExtensionFilters().add(filter);

        File selectedFile = fileChooser.showOpenDialog(ProductPriceMonitoringToolApplication.getMainStage());

        if (isNull(selectedFile)) {
            return Optional.empty();
        }

        return Optional.of(selectedFile);
    }

    /**
     * Method for saving image.
     * @param sourcePathString Source path.
     *                         Path of the source image.
     * @param destinationPathString Destination path.
     *                              Path of the destination image.
     */
    public static void saveImage(String sourcePathString, String destinationPathString) {
        try {
            deleteImage(destinationPathString);

            String decodedSourceUrl = URLDecoder.decode(sourcePathString, StandardCharsets.UTF_8);
            Path source = Paths.get(decodedSourceUrl.substring(6));
            Path destination = Paths.get(destinationPathString.toLowerCase());

            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileSaveException("Error while saving image.", e);
        }
    }

    /**
     * Method for deleting image.
     * @param path Path.
     *             Path of the image.
     */
    public static void deleteImage(String path) throws IOException {
        path = path.substring(0, path.lastIndexOf("."));

        for (String extension : Constants.IMAGE_EXTENSIONS) {
            Path filePath = Path.of(path + extension.substring(1));
            Files.deleteIfExists(filePath);
        }
    }

    /**
     * Method for cropping image to square.
     * @param image Image.
     *              Image to be cropped.
     * @return Image.
     *         Cropped image.
     */
    public static Image cropImageToSquare(Image image) {
        double originalWidth = image.getWidth();
        double originalHeight = image.getHeight();

        double squareSize = Math.min(originalWidth, originalHeight);

        double x = (originalWidth - squareSize) / 2;
        double y = (originalHeight - squareSize) / 2;

        PixelReader pixelReader = image.getPixelReader();

        return new WritableImage(
                pixelReader,
                (int) x,
                (int) y,
                (int) squareSize,
                (int) squareSize
        );
    }
}
