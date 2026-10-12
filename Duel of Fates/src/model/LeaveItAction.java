package model;

import java.io.Serializable;

/**
 * Trieda LeaveItAction reprezentuje akciu, pri ktorej hráč rozhodne nevybrať aktuálnu možnosť.
 * Rozširuje triedu {@link Action} a implementuje rozhranie {@link Serializable}.
 */
public class LeaveItAction extends Action implements Serializable {

    // Implementácia metódy performAction z abstraktnej triedy Action.
    @Override
    public void performAction() {
        // Vypíše správu oznamujúcu, že hráč sa rozhodol nevybrať aktuálnu možnosť.
        System.out.println("Rozhodli ste sa ju nevybrať.");
    }
}

