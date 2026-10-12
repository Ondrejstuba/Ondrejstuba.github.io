package model;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Trieda NameGenerator poskytuje funkcionalitu na generovanie náhodných mien.
 */
public class NameGenerator {

    // Pole prefixov, sufixov a bežných mien na generovanie náhodných mien
    private static final String[] PREFIXES = {"Ava", "Bro", "Eli", "Eva", "Ivy", "Jay", "Kai", "Leo", "Lia", "Mia", "Neo", "Noa", "Oli", "Ora", "Zoe"};
    private static final String[] SUFFIXES = {"den", "lin", "lyn", "lynn", "son", "ton", "ley", "leigh", "elle", "ella", "ie", "lee", "ey", "ay", "en", "yn", "an", "an", "yn", "yn"};
    private static final String[] COMMON_NAMES = {
    	    // Male names
    	    "Peter", "John", "Michael", "William", "James", "Joseph", "David", "Daniel", "Matthew", "Christopher",
    	    "Andrew", "Anthony", "Robert", "Thomas", "Steven", "Richard", "Brian", "Kevin", "Edward", "Jason",
    	    "Mark", "Paul", "Kenneth", "George", "Joshua", "Jeremy", "Timothy", "Jonathan", "Larry",
    	    "Justin", "Ryan", "Scott", "Brandon", "Benjamin", "Gregory", "Samuel", "Charles", "Jeffrey", "Nicholas",
    	    "Dennis", "Jerry", "Bryan", "Terry", "Alexander", "Carl", "Keith", "Randy", "Roger", "Aaron",
    	    "Howard", "Frank", "Harry", "Alan", "Philip", "Vincent", "Eric", "Johnny", "Kyle", "Louis",
    	    
    	    // Female names
    	    "Mary", "Jennifer", "Linda", "Patricia", "Susan", "Karen", "Jessica", "Nancy", "Sarah", "Betty",
    	    "Lisa", "Margaret", "Sandra", "Ashley", "Kimberly", "Emily", "Donna", "Michelle", "Dorothy", "Carol",
    	    "Amanda", "Melissa", "Deborah", "Stephanie", "Rebecca", "Sharon", "Laura", "Cynthia", "Kathleen", "Amy",
    	    "Shirley", "Angela", "Helen", "Carolyn", "Frances", "Joyce", "Diane", "Virginia", "Christine", "Janet",
    	    "Martha", "Debra", "Amanda", "Tiffany", "Pamela", "Nicole", "Catherine", "Judith", "Jean", "Tammy"
    	};


    /**
     * Generuje náhodné mená pre textové polia vo VBoxe.
     *
     * @param nameInputBox VBox obsahujúci textové polia, do ktorých sa vygenerujú náhodné mená.
     */
    public static void generateRandomNames(VBox nameInputBox) {
        Random random = new Random();
        Set<String> generatedNames = new HashSet<>();
        for (Node node : nameInputBox.getChildren()) {
            if (node instanceof VBox) {
                VBox vbox = (VBox) node;
                for (Node innerNode : vbox.getChildren()) {
                    if (innerNode instanceof HBox) {
                        HBox hbox = (HBox) innerNode;
                        TextField textField = (TextField) hbox.getChildren().get(0);
                        String randomName = generateRandomName(random, generatedNames);
                        textField.setText(randomName);
                    }
                }
            }
        }
    }

    /**
     * Generuje náhodné meno pomocou poskytnutého generátora náhodných čísel a sady už vygenerovaných mien.
     *
     * @param random         Generátor náhodných čísel.
     * @param generatedNames Sada už vygenerovaných mien.
     * @return Náhodne vygenerované meno.
     */
    public static String generateRandomName(Random random, Set<String> generatedNames) {
        String name;
        do {
            name = generateName(random);
        } while (name.length() > 10 || generatedNames.contains(name));
        generatedNames.add(name);
        return name;
    }

    // Pomocná metóda pre generovanie mena
    private static String generateName(Random random) {
        if (random.nextBoolean()) {
            // Generuje moderné znejúce meno
            StringBuilder nameBuilder = new StringBuilder();
            int nameLength = 2 + random.nextInt(5); // Náhodná dĺžka medzi 2 a 6 slabikami

            // Začína prefixom
            nameBuilder.append(PREFIXES[random.nextInt(PREFIXES.length)]);

            // Pridáva stredné časti (slabiky)
            for (int i = 0; i < nameLength; i++) {
                // Náhodne vyberá štruktúru slabiky (CVC alebo VC)
                String syllable = (random.nextBoolean()) ? generateCVC(random) : generateVC(random);
                nameBuilder.append(syllable);
            }

            // Pridáva sufix
            nameBuilder.append(SUFFIXES[random.nextInt(SUFFIXES.length)]);

            return capitalizeFirstLetter(nameBuilder.toString());
        } else {
            // Generuje bežné meno
            return COMMON_NAMES[random.nextInt(COMMON_NAMES.length)];
        }
    }

    // Pomocná metóda pre generovanie slabiky CVC
    private static String generateCVC(Random random) {
        StringBuilder syllable = new StringBuilder();
        syllable.append(randomConsonant(random));
        syllable.append(randomVowel(random));
        syllable.append(randomConsonant(random));
        return syllable.toString();
    }

    // Pomocná metóda pre generovanie slabiky VC
    private static String generateVC(Random random) {
        StringBuilder syllable = new StringBuilder();
        syllable.append(randomVowel(random));
        syllable.append(randomConsonant(random));
        return syllable.toString();
    }

    // Pomocná metóda pre náhodný výber spoluhlásky
    private static String randomConsonant(Random random) {
        return randomElement(random, "bcdfghjklmnpqrstvwxyz");
    }

    // Pomocná metóda pre náhodný výber samohlásky
    private static String randomVowel(Random random) {
        return randomElement(random, "aeiou");
    }

    // Pomocná metóda pre náhodný výber prvku zo zadaného reťazca
    private static String randomElement(Random random, String str) {
        return String.valueOf(str.charAt(random.nextInt(str.length())));
    }

    // Pomocná metóda na veľké začiatočné písmeno
    private static String capitalizeFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
