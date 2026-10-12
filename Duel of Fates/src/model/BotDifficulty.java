package model;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Výčtový typ BotDifficulty definuje rôzne úrovne obtiažnosti pre robota v hre.
 */
public enum BotDifficulty {
    
    // Jednotlivé úrovne obtiažnosti s ich vlastnosťami
    TEST(0.01, Color.GRAY, 200),       // Testovacia obtiažnosť s časovačom na 5 sekúnd
    EASY(0.3, Color.GREEN, 100),       // Ľahká obtiažnosť s časovačom na 20 sekúnd
    MEDIUM(0.6, Color.ORANGE, 60),     // Stredná obtiažnosť s časovačom na 15 sekúnd
    HARD(0.9, Color.RED, 30),          // Ťažká obtiažnosť s časovačom na 10 sekúnd
    IMPOSSIBLE(0.99, Color.DARKRED, 15); // Nemožná obtiažnosť s časovačom na 5 sekúnd

    private final double correctProbability; // Pravdepodobnosť správnej odpovede
    private final Color color;               // Farba úrovne obtiažnosti
    private final int timerDuration;         // Dĺžka trvania časovača v sekundách

    /**
     * Konštruktor pre vytvorenie jednotlivých úrovní obtiažnosti.
     * @param correctProbability Pravdepodobnosť správnej odpovede.
     * @param color Farba úrovne obtiažnosti.
     * @param timerDuration Dĺžka trvania časovača v sekundách.
     */
    BotDifficulty(double correctProbability, Color color, int timerDuration) {
        this.correctProbability = correctProbability;
        this.color = color;
        this.timerDuration = timerDuration;
    }

    /**
     * Metóda vráti pravdepodobnosť správnej odpovede pre danú úroveň obtiažnosti.
     * @return Pravdepodobnosť správnej odpovede.
     */
    public double getCorrectProbability() {
        return correctProbability;
    }

    /**
     * Metóda vráti farbu pre danú úroveň obtiažnosti.
     * @return Farba úrovne obtiažnosti.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Metóda vráti dĺžku trvania časovača pre danú úroveň obtiažnosti.
     * @return Dĺžka trvania časovača v sekundách.
     */
    public int getTimerDuration() {
        return timerDuration;
    }

    // Vlastná metóda toString pre vrátenie Text s určenou farbou
    @Override
    public String toString() {
        Text text = new Text(name());
        text.setFill(color);
        return text.getText();
    }
}
