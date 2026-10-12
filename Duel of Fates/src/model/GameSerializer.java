package model;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import view.GameView;

/**
 * Trieda GameSerializer poskytuje funkcionalitu na ukladanie stavu hry a štatistiky do súboru.
 */
public class GameSerializer {
    /**
     * Metóda saveGame() ukladá stav hry a štatistiky do zadaného súboru.
     * 
     * @param filename             Názov súboru, do ktorého sa má uložiť stav hry a štatistiky.
     * @param game                 Referenčný objekt typu GameView reprezentujúci aktuálnu hru.
     * @param votes                Mapa obsahujúca hlasovanie hráčov.
     * @param optionVotesByPlayer  Mapa obsahujúca hlasovanie podľa možností pre každého hráča.
     * @param votedPlayers         Množina obsahujúca mená hráčov, ktorí hlasovali.
     * @param excludedPlayers      Množina obsahujúca mená hráčov, ktorí boli vylúčení zo hry.
     * @param currentPromptNumber  Číslo aktuálnej otázky.
     * @param currentPlayerIndex   Index aktuálneho posledného hráča.
     * @param questionCount        Počet otázok v poradí.
     * @param snapshotNumber       Číslo snímky pre uloženie štatistiky.
     */
    public void saveGame(String filename, GameView game, Map<String, Integer> votes,
            Map<String, List<String>> optionVotesByPlayer, Set<String> votedPlayers,
            Set<String> excludedPlayers, int currentPromptNumber, int currentPlayerIndex,
            int questionCount, int snapshotNumber) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, true)))) {
            PrintWriter printWriter = new PrintWriter(writer);
            printWriter.println("Uloženie štatistiky na konci otázky " + snapshotNumber + ":");
            printWriter.println("---------------");
            printWriter.println("Hra: " + game);
            printWriter.println();
            printWriter.println("Hlasy:");
            for (Map.Entry<String, Integer> entry : votes.entrySet()) {
                printWriter.println(entry.getKey() + ": " + entry.getValue());
            }
            printWriter.println();
            printWriter.println("Hlasovania podľa hlasov:");
            for (Map.Entry<String, List<String>> entry : optionVotesByPlayer.entrySet()) {
                printWriter.println(entry.getKey() + ": " + entry.getValue());
            }
            printWriter.println();
            printWriter.println("Hlasovali: " + votedPlayers);
            printWriter.println("Vylúčený: " + excludedPlayers);
            printWriter.println("Momentálna otázka je: " + currentPromptNumber);
            printWriter.println("Momentálny posledný hráč je: " + currentPlayerIndex);
            printWriter.println("Počet otázky v poradí: " + questionCount);
            printWriter.println();
            printWriter.flush();
            System.out.printf("Štatistika %d bola úspešne uložená v súbore: %s\n", snapshotNumber, filename);
        } catch (IOException e) {
            System.err.println("Štatistiku sa nepodarilo uložiť: " + e.getMessage());
        }
    }
}
