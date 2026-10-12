package model;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Rozhranie GameViewInterface definuje metódy, ktoré implementuje trieda, ktorá reprezentuje pohľad na hru...teda gameview
 */
public interface GameViewInterface {
    public void handleNext();
    public void moveToNextPlayer();
    public void endGame();
    public void winGame(); 
    public Button createStyledButton(String text);
    public void handleTryAgainButtonClick();
    public String determineWinningOption();
    public void useSpecialCard(String playerName);
    public StackPane getPane();
    public Scene getScene();
    public void createPane();
    public Choice getCorrectOption();
    public void startTimer(VBox contentBox);
    public void handleBotChoice(String botName);
    public void addOptionButton(VBox contentBox, Choice choice);
    public void addSpecialCardButton(VBox contentBox);
    
    /**
     * Spracováva udalosť kliknutia na tlačidlo pre špeciálnu kartu...default kriterium
     *
     * @param playerName Meno hráča, ktorý klikol na tlačidlo.
     */
    public default void handleSpecialCardButtonClick(String playerName) {
        useSpecialCard(playerName);
    }
}
