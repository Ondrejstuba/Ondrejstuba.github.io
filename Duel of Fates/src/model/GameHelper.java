package model;

import java.util.Map;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;

/**
 * Trieda GameHelper poskytuje pomocné metódy pre eso v hre (eso je specialna karta, co moze hrac pouzit len raz v hre)
 */
public class GameHelper {
    private StackPane pane;

    /**
     * Konštruktor pre inicializáciu pomocného objektu.
     * @param pane Referencia na StackPane použitý v hre.
     */
    public GameHelper(StackPane pane) {
        this.pane = pane;
    }

    // Interface for special card functionality
    private interface SpecialCard {
        /**
         * Metóda useSpecialCard() umožňuje použiť špeciálnu kartu v hre.
         * @param playerName Meno hráča, ktorý používa špeciálnu kartu.
         * @param specialCardUsage Mapa uchovávajúca informácie o použití špeciálnych kariet pre jednotlivých hráčov.
         * @param correctOption Správna možnosť pre aktuálnu otázku.
         */
        void useSpecialCard(String playerName, Map<String, Boolean> specialCardUsage, Choice correctOption);
    }
    
    /**
     * Nested trieda ktora implementuje specialcard interface.
     * Tu sa poskytuje pouzitie specialnej karty, resp. implementacia
     */
    private class SpecialCardImpl implements SpecialCard {
        @Override
        public void useSpecialCard(String playerName, Map<String, Boolean> specialCardUsage, Choice correctOption) {
            // Check if the player has already used the special card
            if (!specialCardUsage.get(playerName)) {
                // Display the correct answer
                displayCorrectAnswer(correctOption);
                
                // Mark the special card as used for this player
                specialCardUsage.put(playerName, true);
            } else {
                // Notify the player that the special card has already been used
                displayNotification("Žial, ty už si svoju špeciálnu kartu použil. Je to už len v tvojich rukách...", pane);
            }
        }
    }

    // metoda na pouzitie spec. karty
    /**
     * Metóda useSpecialCard() umožňuje použiť špeciálnu kartu v hre.
     * @param playerName Meno hráča, ktorý používa špeciálnu kartu.
     * @param specialCardUsage Mapa uchovávajúca informácie o použití špeciálnych kariet pre jednotlivých hráčov.
     * @param correctOption Správna možnosť pre aktuálnu otázku.
     */
    public void useSpecialCard(String playerName, Map<String, Boolean> specialCardUsage, Choice correctOption) {
        SpecialCard specialCard = new SpecialCardImpl();
        //RTTI (DALSIE MAM AJ V GAMEVIEW...ALE TU SOM PRIDAL DALSIE)
        if (specialCard instanceof SpecialCardImpl) {
            ((SpecialCardImpl) specialCard).useSpecialCard(playerName, specialCardUsage, correctOption);
        } else {
            // Handle the case if the special card is not of type SpecialCardImpl
            displayNotification("CHYBA PRI PRACI S ESOM.", pane);
        }
    }

    // Method to display the correct answer to the player
    private void displayCorrectAnswer(Choice correctOption) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.initOwner(pane.getScene().getWindow()); // Set owner to the game's primary stage
        alert.setTitle("POUŽIL SI SVOJE ESO!");
        alert.setHeaderText("Speávna odpoveď je:");
        alert.setContentText(correctOption.getDescription());
        alert.showAndWait();
    }

    // Default method implementation for displaying notification messages
    private void displayNotification(String message, StackPane pane) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.initOwner(pane.getScene().getWindow()); // Set owner to the game's primary stage
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
