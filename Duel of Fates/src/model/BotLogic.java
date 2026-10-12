package model;

import java.io.Serializable;
import java.util.Random;

/**
 * Trieda BotLogic implementuje logiku robota v hre.
 */
public class BotLogic implements Serializable {
    private BotDifficulty botDifficulty;

    /**
     * Konštruktor pre inicializáciu úrovne obtiažnosti robota.
     * @param botDifficulty Úroveň obtiažnosti robota.
     */
    public BotLogic(BotDifficulty botDifficulty) {
        this.botDifficulty = botDifficulty;
    }

    /**
     * Metóda makeChoice() generuje odpoveď robota na základe aktuálnych možností.
     * @param currentOptions Aktuálne možnosti, z ktorých si robot vyberá.
     * @return Vybraná odpoveď robota.
     */
    public Choice makeChoice(Choice[] currentOptions) {
        Random random = new Random();
        int randomIndex = random.nextInt(currentOptions.length);
        double randomValue = random.nextDouble();
        double correctProbability = botDifficulty.getCorrectProbability();

        if (randomValue < correctProbability) {
            // Vyberie správnu odpoveď
            for (Choice option : currentOptions) {
                if (option.isCorrect()) {
                    return option;
                }
            }
        } else {
            // Vyberie nesprávnu odpoveď
            for (Choice option : currentOptions) {
                if (!option.isCorrect()) {
                    return option;
                }
            }
        }

        // Záložné riešenie: Vyberie náhodnú možnosť
        return currentOptions[randomIndex];
    }
}
