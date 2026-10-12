package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import model.Tips;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;

/**
 * Trieda WinGameView predstavuje pohľad na koniec hry, keď hráč vyhrá.
 */
public class WinGameView {
    private final StackPane pane;
    private Tips tips;
    private Text tipsLabel;
    
    

    /**
     * Vytvorí nový pohľad na koniec hry.
     * 
     * @param paneWidth   Šírka hlavného kontajnera.
     * @param paneHeight  Výška hlavného kontajnera.
     * @param playersLeft Počet hráčov, ktorí vyhrali.
     * @param winners     Zoznam výhercov.
     */
    public WinGameView(double paneWidth, double paneHeight, int playersLeft, List<String> winners) {
        pane = new StackPane();
        pane.setMinSize(paneWidth, paneHeight);
        
        // Create canvas for particle effects
        Canvas canvas = new Canvas(paneWidth, paneHeight);
        pane.getChildren().add(canvas);

        // Start the animation
        startParticleAnimation(canvas);

        tips = new Tips();

        // Create semi-transparent overlay panel
        StackPane overlayPane = new StackPane();
        overlayPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
        overlayPane.setMinSize(paneWidth, paneHeight);

        // Create content for the win game screen
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);

        Text winText = new Text("Vyhrali ste!");
        winText.getStyleClass().add("result-text");

        // Show the number of players left
        Text playersLeftText = new Text("Počet hráčov, ktorý vyhrali hru je: " + playersLeft);
        playersLeftText.getStyleClass().add("tips-text");
        contentBox.getChildren().addAll(winText, playersLeftText);

        StackPane winnersPane = new StackPane();
     // Set the background color of the StackPane with transparency
     winnersPane.setStyle("-fx-background-color: rgba(60, 40, 20, 0.8); -fx-background-radius: 4px; -fx-border-radius: 3px; -fx-background: none; -fx-background-insets: 0; -fx-padding: 10px; -fx-margin: 20px");
     // Create a ListView to display winners
     ListView<String> winnersListView = new ListView<>();
     // Add the list of winners to the ListView
     winnersListView.getItems().addAll(winners);
     // Set style for the ListView to have transparent background, 16px font size, and white text color
     winnersListView.setStyle("-fx-background-color: rgba(60, 40, 90, 0.5); -fx-font-size: 16px; -fx-text-fill: white; -fx-control-inner-background: transparent; -fx-selection-bar-non-focused: yellow; -fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-font-style: italic;");



     // Add the ListView to the StackPane
     winnersPane.getChildren().add(winnersListView);
     // Limit the maximum width of the StackPane to 20% of the provided width
     winnersPane.setMaxWidth(paneWidth * 0.2);
     winnersPane.setMaxHeight(paneHeight * 0.3);
     
     
     
     

     // Center align the content of the StackPane
     winnersPane.setAlignment(Pos.CENTER);


        // Create buttons for options
        Button quitButton = createStyledButton("Vypnúť");
        quitButton.setOnAction(e -> handleQuitButtonClick());

        Button returnButton = createStyledButton("Znova hrať");
        returnButton.setOnAction(e -> handleReturnButtonClick());

        contentBox.getChildren().addAll(winnersPane, quitButton, returnButton);

        // Create tips label
        tipsLabel = new Text(tips.getNextTip());
        tipsLabel.getStyleClass().add("tips-text");

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

        // Add content to the main pane
        pane.getChildren().addAll(overlayPane, contentBox);
        
        
        
        
    }

    /**
     * Vytvorí tlačidlo s definovaným textom a štýlom.
     * 
     * @param text Text na tlačidle.
     * @return Tlačidlo so štýlom.
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
     * Spracuje udalosť kliknutia na tlačidlo ukončenia hry.
     */
    private void handleQuitButtonClick() {
        System.exit(0); // Exit the application
    }

    /**
     * Spracuje udalosť kliknutia na tlačidlo pre návrat k novému začiatku hry.
     */
    private void handleReturnButtonClick() {
        StartMenuView startMenu = new StartMenuView();
        pane.getScene().setRoot(startMenu.getPane());
    }

    public StackPane getPane() {
        return pane;
    }
    
    
    /**
     * Spustí animáciu častíc na zadanom plátne.
     * 
     * @param canvas Plátno pre animáciu častíc.
     */
    private void startParticleAnimation(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        List<Particle> particles = new ArrayList<>();

        // Create particles
        for (int i = 0; i < 100; i++) {
            particles.add(new Particle(canvas.getWidth() / 2, canvas.getHeight() / 2));
        }

        // Animation timer for updating and rendering particles
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.setGlobalAlpha(0.2);
                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                gc.setGlobalAlpha(1);

                gc.setGlobalBlendMode(BlendMode.ADD);

                for (Particle particle : particles) {
                    particle.update(canvas.getWidth(), canvas.getHeight());
                    particle.render(gc);
                }
            }
        };
        timer.start();
    }

    /**
     * Trieda reprezentuje časticu v animácii.
     */
    private static class Particle {
        private double x;
        private double y;
        private double vx;
        private double vy;
        private double size;

        public Particle(double x, double y) {
            this.x = x;
            this.y = y;
            Random random = new Random();
            this.vx = (random.nextDouble() * 2 - 1) * 2; // Random velocity between -2 and 2
            this.vy = (random.nextDouble() * 2 - 1) * 2; // Random velocity between -2 and 2
            this.size = random.nextDouble() * 2 + 1; // Random size between 1 and 4
        }

        public void update(double canvasWidth, double canvasHeight) {
            x += vx;
            y += vy;
            
            // Wrap particles around the canvas if they move out of bounds
            if (x < 0) {
                x = canvasWidth;
            }
            if (x > canvasWidth) {
                x = 0;
            }
            if (y < 0) {
                y = canvasHeight;
            }
            if (y > canvasHeight) {
                y = 0;
            }
        }


        public void render(GraphicsContext gc) {
            // Generate a random number to determine which color render method to use
            Random random = new Random();
            int choice = random.nextInt(4); // Adjust the number based on how many color options you have

            switch (choice) {
                case 0:
                    renderYellow(gc);
                    break;
                case 1:
                    renderRed(gc);
                    break;
                case 2:
                    renderBlue(gc);
                    break;
                case 3:
                    renderGreen(gc);
                    break;
                default:
                    renderYellow(gc); // Default to yellow if choice is out of bounds
            }
        }

        private void renderYellow(GraphicsContext gc) {
            gc.setFill(Color.YELLOW);
            gc.fillOval(x, y, size, size);
        }

        private void renderRed(GraphicsContext gc) {
            gc.setFill(Color.RED);
            gc.fillOval(x, y, size, size);
        }

        private void renderBlue(GraphicsContext gc) {
            gc.setFill(Color.BLUE);
            gc.fillOval(x, y, size, size);
        }

        private void renderGreen(GraphicsContext gc) {
            gc.setFill(Color.GREEN);
            gc.fillOval(x, y, size, size);
        }

        // Add more render methods for additional colors

    }


}



