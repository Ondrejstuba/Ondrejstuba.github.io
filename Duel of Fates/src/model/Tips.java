package model;

import java.util.Arrays;
import java.util.Collections;

/**
 * Trieda Tips poskytuje rôzne tipy pre hru, ktoré použijeme napríklad pri načítavaní.
 */
public class Tips {
    private String[] tips = {
        "TIP: Využi všetok dostupný čas, čo máš na otázku!",
        "TIP: Petrík, ty si ma nepraj!",
        "TIP: Ak je počet hlasov rovnaký pre obe možnosti, vyhlási sa remíza hlasovania.",
        "TIP: Voľby botov sa nezobrazia, ak si zvolil ťažšiu obtiažnosť ako medium.",
        "TIP: Pri obtiažnostiach ťažších ako medium, dávaj pozor na šikovných AI protivníkov!",
        "TIP: Testovacia obtiažnosť: 3 minúty a 20 sekúnd na otázku",
        "TIP: Ľahká obtiažnosť: 1 minúta a 40 sekúnd na otázku",
        "TIP: Stredná obtiažnosť: 1 minúta na otázku",
        "TIP: Ťažká obtiažnosť: 30 sekúnd na otázku",
        "TIP: Nemožná obtiažnosť: 15 sekúnd na otázku",
        "TIP: AI úroveň na testovacej obtiažnosti: 1% (veľmi jednoduché)",
        "TIP: AI úroveň na ľahkej obtiažnosti: 30% (ľahké)",
        "TIP: AI úroveň na strednej obtiažnosti: 60% (stredné)",
        "TIP: AI úroveň na ťažkej obtiažnosti: 90% (ťažké)",
        "TIP: AI úroveň na nemožnej obtiažnosti: 99% (nemožné)",
        "TIP: Po každom hlasovaní sa zobrazia štatistiky, ktoré ti pomôžu pochopiť rozhodnutia ostatných hráčov.",
        "TIP: Zobrazenie štatistík po hlasovaní ti umožní vidieť, ako tvoje rozhodnutie ovplyvní výsledky hry.",
        "TIP: Pri tvorbe hry si môžeš zvoliť počet hráčov od 1 do 500!",
        "TIP: Pri tvorbe hry si môžeš zvoliť obtiažnosť AI!",
        "TIP: Pri tvorbe hry si môžeš zvoliť, koľko času budú mať hráči na otázku!",
        "TIP: Pri tvorbe hry si môžeš zvoliť, koľko otázok bude v hre!",
        "TIP: Pri tvorbe mien hráčov môžeš využiť náhodné generovanie mien!",
        "TIP: Ak nastavíš všetkých hráčov na AI, môžeš simulovať ich voľby v reálnom čase!"
    };

    private int currentTipIndex = 0;

    /**
     * Konštruktor triedy Tips. Zmieša poradie tipov.
     */
    public Tips() {
        Collections.shuffle(Arrays.asList(tips));
    }
    
    /**
     * Metóda getNextTip() vráti ďalší tip zo zoznamu tipov a resetuje ich poradie, ak už všetky boli použité.
     * @return Ďalší tip.
     */
    public String getNextTip() {
        String nextTip = tips[currentTipIndex];
        currentTipIndex = (currentTipIndex + 1) % tips.length;
        if (currentTipIndex == tips.length) {
            HiddenHelper.resetTipsOrder(tips);
            currentTipIndex = 0;
        }
        return nextTip;
    }


    // Vnútorná trieda alebo rozhranie

    /**
     * Vnútorná trieda pre skrytú funkcionalitu spojenú s tipmi.
     */
    private static class HiddenHelper {
        // Privátny konštruktor zabráni vytvoreniu inštancie tejto triedy
        private HiddenHelper() {}

        /**
         * Metóda na resetovanie poradia tipov.
         * @param tips Pole s tipmi.
         */
        static void resetTipsOrder(String[] tips) {
            Collections.shuffle(Arrays.asList(tips));
        }
    }
}
