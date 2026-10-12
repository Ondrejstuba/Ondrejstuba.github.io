package view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import model.Tips;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

/**
 * Trieda EndGameView predstavuje pohľad na koniec hry.
 */
public class EndGameView {
    private final StackPane pane;
    private Tips tips;
    private Label tipsLabel;

    /**
     * Konštruktor pre koncový pohľad na koniec hry.
     * 
     * @param paneWidth  Šírka panelu.
     * @param paneHeight Výška panelu.
     */
    public EndGameView(double paneWidth, double paneHeight) {
        pane = new StackPane();
        pane.setMinSize(paneWidth, paneHeight);

        tips = new Tips();

        // Create semi-transparent overlay panel
        StackPane overlayPane = new StackPane();
        overlayPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9);");
        overlayPane.setMinSize(paneWidth, paneHeight);

        // Create content for the end game screen
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);

        Text endText = new Text("Koniec hry");
        endText.getStyleClass().add("result-text");

        // Create buttons for options
        Button quitButton = createStyledButton("Ukončiť hru");
        quitButton.setOnAction(e -> Platform.exit());

        Button tryAgainButton = createStyledButton("Skúsiť znova");
        tryAgainButton.setOnAction(e -> handleTryAgainButtonClick());

        contentBox.getChildren().addAll(endText, quitButton, tryAgainButton);

        // Add content and overlay panel to the main pane
        pane.getChildren().addAll(overlayPane, contentBox);

        // Create tips label
        tipsLabel = new Label(tips.getNextTip());
        tipsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        contentBox.getChildren().add(tipsLabel);

        // Set up timeline to switch tips every 6 seconds
        Timeline tipsTimeline = new Timeline(new KeyFrame(Duration.seconds(6), event -> {
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
     * Vytvára tlačidlo so štýlom pre použitie v koncovom pohľade.
     * 
     * @param text Text tlačidla.
     * @return Tlačidlo s nastaveným štýlom.
     */
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("styled-button");

        button.setOnMouseEntered(e -> {
            button.getStyleClass().add("hovered");
            button.setScaleX(1.1);
            button.setScaleY(1.1);
        });

        button.setOnMouseExited(e -> {
            button.getStyleClass().remove("hovered");
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });

        return button;
    }

    /**
     * Obsluha udalosti kliknutia na tlačidlo "Skúsiť znova".
     */
    private void handleTryAgainButtonClick() {
        StartMenuView startMenu = new StartMenuView();
        pane.getScene().setRoot(startMenu.getPane());
    }

    /**
     * Vráti panel pre koncový pohľad.
     * 
     * @return Panel pre koncový pohľad.
     */
    public StackPane getPane() {
        return pane;
    }
}
