package hr.tvz.productpricemonitoringtool.util;

import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.math.BigDecimal;

/**
 * ProgressBarUtil class.
 * Contains methods for creating, updating and removing progress bar.
 */
public class ProgressBarUtil {

    private ProgressBar progressBar;
    private StackPane progressBarStackPane;
    private final GridPane mainPane;

    public ProgressBarUtil(GridPane mainPane) {
        this.mainPane = mainPane;
        create();
    }

    /**
     * Method for creating progress bar.
     */
    public void create() {
        progressBarStackPane = new StackPane();
        progressBarStackPane.setPrefSize(Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);
        progressBarStackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        progressBar = new ProgressBar();
        progressBar.setPrefSize(200, 20);
        progressBar.setProgress(0);
        progressBarStackPane.getChildren().add(progressBar);

        mainPane.add(progressBarStackPane, 0, 0);
        GridPane.setColumnSpan(progressBarStackPane, 2);
    }

    /**
     * Method for updating progress bar.
     * @param progress Progress.
     */
    public void update(BigDecimal progress) {
        progressBar.setProgress(progress.doubleValue());
    }

    /**
     * Method for removing progress bar.
     */
    public void remove() {
        mainPane.getChildren().remove(progressBarStackPane);
    }

    /**
     * Method for imitating progress counter.
     * @param counter Counter.
     * @return BigDecimal.
     *         Imitated progress counter.
     */
    public static BigDecimal imitateProgressCounter(BigDecimal counter) {
        if(counter.compareTo(BigDecimal.valueOf(30)) < 0)
            return counter.add(BigDecimal.valueOf(1));
        else if(counter.compareTo(BigDecimal.valueOf(60)) < 0)
            return counter.add(BigDecimal.valueOf(0.5));
        else if(counter.compareTo(BigDecimal.valueOf(80)) < 0)
            return counter.add(BigDecimal.valueOf(0.25));
        else if(counter.compareTo(BigDecimal.valueOf(90)) < 0)
            return counter.add(BigDecimal.valueOf(1.5));
        else
            return counter.add(BigDecimal.valueOf(0.5));
    }
}