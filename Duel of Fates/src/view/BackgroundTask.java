package view;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.Tips;

/**
 * Trieda BackgroundTask reprezentuje úlohu spúšťanú na pozadí s načítacím panelom.
 * Obsahuje animáciu načítavacieho panela a zobrazuje tipy pre hru.
 */
public class BackgroundTask {
    private Task<Void> task;
    private ProgressBar progressBar;
    private VBox loadingPane;
    private Runnable onTaskCompleted;
    private Tips tips;

    /**
     * Konštruktor triedy BackgroundTask.
     * Inicializuje načítavací panel a spúšťa úlohu načítavania.
     */
    public BackgroundTask() {
        tips = new Tips();
        progressBar = new ProgressBar();
        task = createTask();
        bindProgress();
        createLoadingPane();
        executeTask();
    }

    /**
     * Vytvára novú úlohu na pozadí.
     * 
     * @return Nová úloha na pozadí.
     */
    private Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i < 100; i++) {
                    Thread.sleep(60);
                    updateProgress(i + 1, 100);
                }
                return null;
            }
        };
    }

    /**
     * Viazanie vlastnosti priebehu úlohy na priebeh prepojenia.
     */
    private void bindProgress() {
        progressBar.progressProperty().bind(task.progressProperty());
        progressBar.styleProperty().bind(progressBarStyleBinding());
    }

    /**
     * Vytvára prepojenie medzi farbou priebehu a priebehom úlohy.
     * 
     * @return Viazanie pre farbu priebehu.
     */
    private StringBinding progressBarStyleBinding() {
        DoubleProperty hue = new SimpleDoubleProperty();
        hue.bind(task.progressProperty().multiply(120));

        return Bindings.createStringBinding(() -> {
            double h = hue.get();
            double red = Math.abs(Math.sin(Math.toRadians(h - 60))) * 255;
            double green = Math.abs(Math.sin(Math.toRadians(h))) * 255;
            double blue = Math.abs(Math.sin(Math.toRadians(h + 60))) * 255;
            return String.format("-fx-accent: rgb(%.0f, %.0f, %.0f);", red, green, blue);
        }, hue);
    }

    /**
     * Vytvára panel načítavania.
     */
    private void createLoadingPane() {
        loadingPane = new VBox(10);
        loadingPane.setAlignment(Pos.CENTER);
        loadingPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9);");

        progressBar.setPrefWidth(400);
        progressBar.setPrefHeight(40);

        Label loadingLabel = new Label("Načítavanie...");
        loadingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        Label progressLabel = new Label();
        progressLabel.textProperty().bind(task.progressProperty().multiply(100).asString("%.0f%%"));
        progressLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        Label tipsLabel = new Label(tips.getNextTip());
        tipsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        loadingPane.getChildren().addAll(loadingLabel, progressBar, progressLabel, tipsLabel);

        Timeline tipsTimeline = new Timeline(new KeyFrame(Duration.seconds(4), event -> {
            String nextTip = tips.getNextTip();
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), tipsLabel);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(fadeOutEvent -> {
                tipsLabel.setText(nextTip);
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), tipsLabel);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeOut.play();
        }));
        tipsTimeline.setCycleCount(Timeline.INDEFINITE);
        tipsTimeline.play();
    }

    /**
     * Spúšťa úlohu načítavania.
     */
    private void executeTask() {
        task.setOnSucceeded(event -> {
            if (onTaskCompleted != null) {
                onTaskCompleted.run();
            }
        });

        task.setOnFailed(event -> {
            System.out.println("Načítavanie zlyhalo: " + task.getException().getMessage());
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Metóda getLoadingPane() vráti načítavací panel.
     * @return Načítavací panel.
     */
    public VBox getLoadingPane() {
        return loadingPane;
    }

    /**
     * Metóda setOnTaskCompleted() nastaví akciu, ktorá sa vykoná po dokončení úlohy.
     * @param onTaskCompleted Akcia, ktorá sa vykoná po dokončení úlohy.
     */
    public void setOnTaskCompleted(Runnable onTaskCompleted) {
        this.onTaskCompleted = onTaskCompleted;
    }
}
