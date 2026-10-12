package model;

/**
 * Trieda Player reprezentuje hráča v hre.
 */
public class Player {
    private String name; // Meno hráča

    /**
     * Konštruktor vytvorí hráča s prázdny názvom.
     */
    public Player() {
        this.name = "";
    }

    /**
     * Konštruktor vytvorí hráča s daným menom.
     * @param name Meno hráča.
     */
    public Player(String name) {
        this.name = name;
    }

    /**
     * Metóda vráti meno hráča.
     * @return Meno hráča.
     */
    public String getName() {
        return name;
    }

    /**
     * Metóda nastaví meno hráča.
     * @param name Nové meno hráča.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Metóda pre vytvorenie reťazca s informáciami o hráčovi.
     * @return Reťazec s informáciami o hráčovi.
     */
    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                '}';
    }
}
