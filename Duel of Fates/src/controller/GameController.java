package controller;

import javafx.application.Application;
import javafx.stage.Stage;
import view.StartMenuView;

/**
 * Trieda GameController je hlavnou triedou riadiacou hru.
 * Rozširuje triedu {@link javafx.application.Application}, aby umožnila spustenie JavaFX aplikácie.
 */
public class GameController extends Application {
	
    /**
     * Default constructor 
     */
    public GameController() {
        // Default constructor
    }
    
    /**
     * Metóda main, spúšťa aplikáciu.
     * @param args argumenty príkazového riadku
     */
    public static void main(String[] args) {
        launch(args); // Spustí aplikáciu
    }

    /**
     * Metóda start, volaná pri spustení aplikácie.
     * Inicializuje hlavné menu a zobrazuje ho na obrazovke.
     * @param primaryStage hlavné okno aplikácie
     */
    @Override
    public void start(Stage primaryStage) {
        // Vytvorenie hlavného menu
        StartMenuView mainMenu = new StartMenuView();
        
        // Nastavenie názvu okna
        primaryStage.setTitle("Hlavné menu hry");
        
        // Nastavenie scény
        primaryStage.setScene(mainMenu.getScene());
        
        // Nastavenie na celú obrazovku
        primaryStage.setFullScreen(true);
        
        // Zobrazenie hlavného menu
        primaryStage.show();
    }
}
