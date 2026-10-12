package model;

/**
 * Trieda BotPlayer je odvodená od triedy Player a reprezentuje hráča typu bot.
 */
public class BotPlayer extends Player {
    private boolean isBot; // Indikátor, či je hráč botom

    /**
     * Konštruktor vytvára hráča typu bot s daným menom a stavom botu.
     * @param name Meno hráča.
     * @param isBot Stav, či je hráč botom.
     */
    public BotPlayer(String name, boolean isBot) {
        super(name); // Volá konštruktor nadradenej triedy Player
        this.isBot = isBot;
    }

    /**
     * Metóda vráti stav, či je hráč botom.
     * @return True, ak je hráč bot, inak false.
     */
    public boolean isBot() {
        return isBot;
    }

    /**
     * Metóda nastaví stav, či je hráč botom.
     * @param isBot True, ak je hráč bot, inak false.
     */
    public void setBot(boolean isBot) {
        this.isBot = isBot;
    }
}
