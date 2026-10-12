package model;

import java.io.Serializable;

/**
 * Trieda Choice reprezentuje možnosť vo voľbe v hre.
 */
public class Choice implements Serializable {
    private String description; // Popis možnosti
    private String promptText; // Text promptu
    private Action action; // Akcia spojená s možnosťou
    private boolean isCorrect;  // Indikátor, či je možnosť správna

    /**
     * Konštruktor inicializuje atribúty možnosti.
     * @param description Popis možnosti.
     * @param promptText Text promptu.
     * @param action Akcia spojená s možnosťou.
     * @param isCorrect Indikátor, či je možnosť správna.
     */
    public Choice(String description, String promptText, Action action, boolean isCorrect) {
        this.description = description;
        this.promptText = promptText;
        this.action = action;
        this.isCorrect = isCorrect;
    }

    /**
     * Metóda vráti popis možnosti.
     * @return Popis možnosti.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Metóda vráti text promptu.
     * @return Text promptu.
     */
    public String getPromptText() {
        return promptText;
    }

    /**
     * Metóda vráti akciu spojenú s možnosťou.
     * @return Akcia spojená s možnosťou.
     */
    public Action getAction() {
        return action;
    }

    /**
     * Metóda vráti informáciu, či je možnosť správna.
     * @return True, ak je možnosť správna, inak false.
     */
    public boolean isCorrect() {
        return isCorrect;
    }
}
