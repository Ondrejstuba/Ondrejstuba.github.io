package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Rozhranie pre triedy, ktoré chcú sledovať zmeny v VoteCounter.
 */
interface VoteCounterObserver {
    void onWinningOptionChanged(String winningOption);
}

/**
 * Trieda VoteCounter určuje víťaznú možnosť na základe poskytnutých hlasov.
 */
public class VoteCounter {
    private Map<String, Integer> votes;
    private List<VoteCounterObserver> observers = new ArrayList<>();

    /**
     * Konštruktor pre inicializáciu VoteCounter s hlasmi.
     * @param votes Mapa možností a ich počet hlasov.
     */
    public VoteCounter(Map<String, Integer> votes) {
        this.votes = votes;
    }

    /**
     * Pridá observer.
     * @param observer Observer, ktorý sa má pridať.
     */
    public void addObserver(VoteCounterObserver observer) {
        observers.add(observer);
    }

    /**
     * Odoberie observer.
     * @param observer Observer, ktorý sa má odobrať.
     */
    public void removeObserver(VoteCounterObserver observer) {
        observers.remove(observer);
    }

    /**
     * Určuje víťaznú možnosť na základe hlasov.
     * @return Víťazná možnosť alebo "----" pri remíze.
     */
    public String determineWinningOption() {
        List<String> winningOptions = new ArrayList<>();
        int maxVotes = Integer.MIN_VALUE;

        // Nájdenie maximálneho počtu hlasov
        for (int voteCount : votes.values()) {
            maxVotes = Math.max(maxVotes, voteCount);
        }

        // Nájdenie možností s maximálnym počtom hlasov
        for (Map.Entry<String, Integer> entry : votes.entrySet()) {
            if (entry.getValue() == maxVotes) {
                winningOptions.add(entry.getKey());
            }
        }

        // Kontrola, či ide o remízu
        String winningOption;
        if (winningOptions.size() > 1) {
            winningOption = "----";
        } else {
            winningOption = winningOptions.get(0);
        }

        // Notifikácia observerov
        notifyObservers(winningOption);

        return winningOption;
    }

    /**
     * Notifikuje všetkých observerov o zmene výsledku.
     * @param winningOption Nová víťazná možnosť.
     */
    private void notifyObservers(String winningOption) {
        for (VoteCounterObserver observer : observers) {
            observer.onWinningOptionChanged(winningOption);
        }
    }
}
