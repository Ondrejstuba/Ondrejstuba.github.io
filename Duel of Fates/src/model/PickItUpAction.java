package model;

import java.io.Serializable;

/**
 * Trieda PickItUpAction reprezentuje akciu, pri ktorej hráč vyberá aktuálnu možnosť.
 * Rozširuje triedu {@link Action} a implementuje rozhranie {@link Serializable}.
 */
public class PickItUpAction extends Action implements Serializable {

    /**
     * Implementácia metódy performAction z abstraktnej triedy Action.
     * Vypíše správu oznamujúcu, že hráč sa rozhodol vybrať aktuálnu možnosť.
     */
    @Override
    public void performAction() {
        System.out.println("Vybrali ste ju!");
    }
}
