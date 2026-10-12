package model;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Enum typ QuestionLength definuje rôzne možnosti dĺžky otázok v hre.
 */
public enum QuestionLength {
    TEST("Test | 1", 1, Color.GRAY), // Krátka otázka na testovanie
    VERY_SHORT("Veľmi Krátka | 3", 3, Color.LIGHTBLUE), // Veľmi krátka otázka
    SHORT("Krátka | 5", 5, Color.BLUE), // Krátka otázka
    MEDIUM("Stredná | 10", 10, Color.GREEN), // Stredne dlhá otázka
    LONG("Dlhá | 25", 25, Color.ORANGE), // Dlhá otázka
    VERY_LONG("Veľmi Dlhá | 50", 50, Color.RED), // Veľmi dlhá otázka
    EXTREMELY_LONG("Extrémne Dlhá | 100", 100, Color.PURPLE), // Extrémne dlhá otázka
    INSANELY_LONG("Nezmyselne Dlhá | 200", 200, Color.DARKRED), // Nezmyselne dlhá otázka
    ENDLESS("Neobmedzená", Integer.MAX_VALUE, Color.BLACK); // Neobmedzená otázka

    private final String displayName; // Názov otázky, zobrazený pre hráča
    private final int length; // Dĺžka otázky
    private final Color color; // Farba textu otázky

    /**
     * Konštruktor pre enum QuestionLength.
     * @param displayName Názov otázky pre zobrazenie.
     * @param length Dĺžka otázky.
     * @param color Farba textu otázky.
     */
    QuestionLength(String displayName, int length, Color color) {
        this.displayName = displayName;
        this.length = length;
        this.color = color;
    }

    /**
     * Metóda vráti názov otázky.
     * @return Názov otázky.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Metóda vráti dĺžku otázky.
     * @return Dĺžka otázky.
     */
    public int getLength() {
        return length;
    }

    /**
     * Metóda vráti farbu textu otázky.
     * @return Farba textu otázky.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Metóda pre reprezentáciu enum v textovej podobe.
     * @return Textová reprezentácia enumu.
     */
    @Override
    public String toString() {
        Text text = new Text(displayName);
        text.setFill(color);
        return text.getText();
    }
}
