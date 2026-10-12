package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.control.CheckBox; 
import model.BotPlayer;
import model.NameGenerator;
import model.Player; 
import model.QuestionLength;
import model.BotDifficulty;
import java.util.ArrayList; 
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * Trieda zodpovedná za zobrazenie menu pre výber hráčov.
 */
public class PlayerSelectionMenuView {

    private StackPane pane; // Panel, na ktorom sa zobrazuje obsah menu
    private String[] playerNames; // Pole pre uchovávanie mien hráčov
    private Label notificationLabel; // Oznamovacie štítky pre zobrazovanie chýb
    private VBox nameInputBox; // Box pre vstup mien hráčov
    private ComboBox<BotDifficulty> difficultyComboBox; // ComboBox pre výber obtiažnosti botov
    private ComboBox<QuestionLength> questionLengthComboBox; // ComboBox pre výber dĺžky otázok
    private Label selectedPlayerCountLabel; // Oznamovacie štítky pre zobrazenie počtu vybraných hráčov
    private Label selectedDifficultyLabel; // Oznamovacie štítky pre zobrazenie vybranej obtiažnosti
    private Label selectedQuestionLengthLabel; // Oznamovacie štítky pre zobrazenie vybranej dĺžky otázok
    private CheckBox autoBotSelectionCheckbox; // Odkaz na zaškrtávacie políčko pre automatický výber botov
    private CheckBox playIntroCheckbox; // Odkaz na zaškrtávacie políčko pre prehratie úvodu hry

    /**
     * Konštruktor pre inicializáciu objektu PlayerSelectionMenuView.
     * @param autoBotSelectionCheckbox Checkbox pre automatický výber botov
     * @param playIntroCheckbox Checkbox pre prehratie úvodu hry
     */
    public PlayerSelectionMenuView(CheckBox autoBotSelectionCheckbox, CheckBox playIntroCheckbox) {
        this.autoBotSelectionCheckbox = autoBotSelectionCheckbox;
        this.playIntroCheckbox = playIntroCheckbox;

        createPane(); // Vytvorenie panela pre menu
    }

    /**
     * Metóda na vytvorenie obsahu menu.
     */
    private void createPane() {
        pane = new StackPane(); // Inicializácia panela
        pane.setAlignment(Pos.CENTER); // Zarovnanie obsahu na stred

        // Nastavenie pozadia panela
        BackgroundImage backgroundImage = new BackgroundImage(
                new javafx.scene.image.Image(getClass().getResource("/view/graphics/images/menu.png").toExternalForm()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(1.0, 1.0, true, true, false, true)
        );
        pane.setBackground(new Background(backgroundImage)); // Nastavenie pozadia

        VBox vbox = new VBox(20); // Vytvorenie vertikálneho boxu pre umiestnenie prvkov
        vbox.setAlignment(Pos.CENTER); // Zarovnanie boxu na stred

        ComboBox<Integer> playersComboBox = new ComboBox<>(); // ComboBox pre výber počtu hráčov
        playersComboBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 20, 50, 100, 200, 500);
        playersComboBox.setValue(1); // Predvolená hodnota

        TextField playerCountTextField = new TextField(); // Textové pole pre vstup počtu hráčov
        playerCountTextField.setPromptText("Alebo zadajte počet hráčov klávesnicou");
        playerCountTextField.setMaxWidth(150);

        notificationLabel = new Label(); // Oznamovací štítek pre zobrazovanie chýb
        notificationLabel.getStyleClass().add("notification-label");
        notificationLabel.setVisible(false); // Najprv skrytý

        Button submitButton = new Button("Potvrdiť"); // Tlačidlo pre potvrdenie výberu
        submitButton.getStyleClass().add("start-button"); // Štýl pre tlačidlo
        submitButton.setOnAction(e -> { // Akcia po kliknutí na tlačidlo
            int selectedPlayerCount;
            if (!playerCountTextField.getText().isEmpty()) {
                try {
                    selectedPlayerCount = Integer.parseInt(playerCountTextField.getText());
                    if (selectedPlayerCount <= 0 || selectedPlayerCount > 500) {
                        throw new IllegalArgumentException("Počet hráčov musí byť medzi 1 a 500");
                    }
                    notificationLabel.setVisible(false); // Skrytie oznamovacieho štítku
                } catch (NumberFormatException ex) {
                    notificationLabel.setText("Neplatný vstup pre počet hráčov"); // Nastavenie textu chyby
                    notificationLabel.setVisible(true); // Zobrazenie oznamovacieho štítku
                    return;
                } catch (IllegalArgumentException ex) {
                    notificationLabel.setText(ex.getMessage()); // Nastavenie textu chyby
                    notificationLabel.setVisible(true); // Zobrazenie oznamovacieho štítku
                    return;
                }
            } else {
                selectedPlayerCount = playersComboBox.getValue();
            }
            handleSubmitButtonClick(selectedPlayerCount); // Volanie metódy pre potvrdenie výberu
        });

        Button backButton = new Button("Späť"); // Tlačidlo pre návrat
        backButton.getStyleClass().add("start-button"); // Štýl pre tlačidlo
        backButton.setOnAction(e -> handleBackButtonClick()); // Akcia po kliknutí na tlačidlo

        String cssFile = getClass().getResource("/view/stylesheet/menu.css").toExternalForm(); // Cesta k CSS súboru
        pane.getStylesheets().add(cssFile); // Načítanie CSS štýlu

        HBox playerCountBox = new HBox(10); // Horizontálny box pre umiestnenie prvkov
        playerCountBox.setAlignment(Pos.CENTER); // Zarovnanie na stred
        playerCountBox.getChildren().addAll(playersComboBox, playerCountTextField); // Pridanie prvkov do boxu

        difficultyComboBox = new ComboBox<>(); // ComboBox pre výber obtiažnosti
        difficultyComboBox.getItems().addAll(BotDifficulty.values());
        difficultyComboBox.setValue(BotDifficulty.MEDIUM); // Predvolená hodnota: stredná obtiažnosť

        // Nastavenie vlastného zobrazenia buniek pre ComboBox obtiažnosti
        difficultyComboBox.setCellFactory(new Callback<ListView<BotDifficulty>, ListCell<BotDifficulty>>() {
            @Override
            public ListCell<BotDifficulty> call(ListView<BotDifficulty> param) {
                return new ListCell<BotDifficulty>() {
                    @Override
                    protected void updateItem(BotDifficulty item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.toString());
                            setBackground(new Background(new BackgroundFill(item.getColor(), null, null)));
                        } else {
                            setText(null);
                            setBackground(null);
                        }
                    }
                };
            }
        });

        // Nastavenie tlačidla pre ComboBox obtiažnosti
        difficultyComboBox.setButtonCell(new ListCell<BotDifficulty>() {
            @Override
            protected void updateItem(BotDifficulty item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.toString());
                    setBackground(new Background(new BackgroundFill(item.getColor(), null, null)));
                } else {
                    setText(null);
                    setBackground(null);
                }
            }
        });

        // ComboBox pre výber dĺžky otázok
        questionLengthComboBox = new ComboBox<>();
        questionLengthComboBox.getItems().addAll(QuestionLength.values());
        questionLengthComboBox.setValue(QuestionLength.MEDIUM); // Predvolená hodnota: stredná dĺžka otázok

        // Nastavenie vlastného zobrazenia buniek pre ComboBox dĺžky otázok
        questionLengthComboBox.setCellFactory(new Callback<ListView<QuestionLength>, ListCell<QuestionLength>>() {
            @Override
            public ListCell<QuestionLength> call(ListView<QuestionLength> param) {
                return new ListCell<QuestionLength>() {
                    @Override
                    protected void updateItem(QuestionLength item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getDisplayName());
                            setBackground(new Background(new BackgroundFill(item.getColor(), null, null)));
                        } else {
                            setText(null);
                            setBackground(null);
                        }
                    }
                };
            }
        });

        // Nastavenie tlačidla pre ComboBox dĺžky otázok
        questionLengthComboBox.setButtonCell(new ListCell<QuestionLength>() {
            @Override
            protected void updateItem(QuestionLength item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getDisplayName());
                    setBackground(new Background(new BackgroundFill(item.getColor(), null, null)));
                } else {
                    setText(null);
                    setBackground(null);
                }
            }
        });

        selectedPlayerCountLabel = new Label("Vybraný počet hráčov: "); // Oznamovací štítek pre zobrazenie vybraného počtu hráčov
        selectedPlayerCountLabel.getStyleClass().add("label-style_selection");

        selectedDifficultyLabel = new Label("Vybraná obtiažnosť: "); // Oznamovací štítek pre zobrazenie vybranej obtiažnosti
        selectedDifficultyLabel.getStyleClass().add("label-style_selection");

        selectedQuestionLengthLabel = new Label("Vybraný  počet otázok: "); // Oznamovací štítek pre zobrazenie vybranej dĺžky otázok
        selectedQuestionLengthLabel.getStyleClass().add("label-style_selection");

        // Ak je zmenený výber počtu hráčov cez ComboBox
        playersComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Text text = new Text("Vybraný počet hráčov: ");
                Text valueText = new Text(newVal.toString());
                valueText.setFill(Color.RED);
                selectedPlayerCountLabel.setText("");
                selectedPlayerCountLabel.setGraphic(new HBox(text, valueText));
            }
        });

        // Ak je zmenený výber počtu hráčov cez Textové pole
        playerCountTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                Text text = new Text("Vybraný počet hráčov: ");
                Text valueText = new Text(newVal);
                valueText.setFill(Color.GREEN);
                selectedPlayerCountLabel.setText("");
                selectedPlayerCountLabel.setGraphic(new HBox(text, valueText));
            }
        });

        // Ak je zmenený výber obtiažnosti
        difficultyComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Text text = new Text("Vybraná obtiažnosť: ");
                Text valueText = new Text(newVal.toString());
                valueText.setFill(Color.GREEN);
                selectedDifficultyLabel.setText("");
                selectedDifficultyLabel.setGraphic(new HBox(text, valueText));
            }
        });

        // Ak je zmenený výber dĺžky otázok
        questionLengthComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Text text = new Text("Vybraný počet otázok: ");
                Text valueText = new Text(newVal.toString());
                valueText.setFill(Color.GREEN);
                selectedQuestionLengthLabel.setText("");
                selectedQuestionLengthLabel.setGraphic(new HBox(text, valueText));
            }
        });

        // Pridanie prvkov do VBoxu
        vbox.getChildren().addAll(selectedPlayerCountLabel, playerCountBox, notificationLabel,
                selectedDifficultyLabel, difficultyComboBox, selectedQuestionLengthLabel,
                questionLengthComboBox, submitButton, backButton);

        pane.getChildren().add(vbox); // Pridanie VBoxu na panel
    }
    
    


    /**
     * Metóda pre spracovanie udalosti po kliknutí na tlačidlo "Potvrdiť".
     * @param selectedPlayerCount Vybraný počet hráčov
     */
    
    private void handleSubmitButtonClick(int numPlayers) {
        nameInputBox = new VBox(10); // Vertikálny box pre vstup mien hráčov
        nameInputBox.setAlignment(Pos.CENTER); // Zarovnanie na stred
        this.playerNames = new String[numPlayers]; // Inicializácia poľa pre mien hráčov
        List<CheckBox> botCheckboxes = new ArrayList<>(); // Zoznam pre uchovávanie zaškrtávacích políčok pre bota

     // Vytvorenie vstupných políčok pre hráčov
        for (int i = 0; i < numPlayers; i++) {
            TextField textField = new TextField();
            textField.getStyleClass().add("player-name-field");
            textField.setMaxWidth(300);
            textField.setPadding(new Insets(5)); // Add padding to text field

            // Zaškrtávacie políčko pre určenie bota
            CheckBox botCheckBox = new CheckBox("Bot");
            botCheckBox.getStyleClass().add("bot-checkbox");
            botCheckBox.setSelected(false); // Predvolená hodnota: nie je to bot
            botCheckBox.setPadding(new Insets(5)); // Add padding to checkbox

            botCheckboxes.add(botCheckBox); // Pridanie políčka do zoznamu
            
            Label label = new Label("Meno hráča " + (i + 1) + ":");
            label.getStyleClass().add("player-name-label");
            label.setPadding(new Insets(5)); // Add padding to label
            
            HBox hbox = new HBox(textField, botCheckBox);
            hbox.setAlignment(Pos.CENTER);
            hbox.setSpacing(5);
            
            VBox vbox = new VBox(label, hbox); // Using VBox and HBox
            vbox.setAlignment(Pos.CENTER); // Center align VBox contents
            vbox.setSpacing(5); // Optional: Adjust spacing between components
            vbox.setPadding(new Insets(5)); // Add padding to VBox
            
            nameInputBox.getChildren().add(vbox); // Pridanie VBoxu do VBoxu
        }


        // Tlačidlo pre zaskrtnutie všetkých políčok pre bota
        Button checkAllBotsButton = new Button("Všetci Boti");
        checkAllBotsButton.getStyleClass().add("start-button");
        checkAllBotsButton.setOnAction(e -> {
            for (CheckBox checkBox : botCheckboxes) {
                checkBox.setSelected(!checkBox.isSelected());
            }
        });

        // Tlačidlo pre generovanie náhodných mien
        Button randomNamesButton = new Button("Náhodné Mená");
        randomNamesButton.getStyleClass().add("start-button");
        randomNamesButton.setOnAction(e -> fillRandomNames());

        // ScrollPane pre zobrazenie vstupných políčok
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(nameInputBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setMaxHeight(400);
        scrollPane.setMaxWidth(600);
        scrollPane.setStyle("-fx-background-color: rgba(20, 80, 20, 0.5); -fx-background-radius: 4px; -fx-border-radius: 3px; -fx-background: none; -fx-background-insets: 0; -fx-padding: 10px; -fx-margin: 20px");

        HBox buttonBox = new HBox(10); // Horizontálny box pre tlačidlá
        buttonBox.setAlignment(Pos.CENTER); // Zarovnanie na stred

        // Tlačidlo pre potvrdenie výberu
        Button confirmButton = new Button("Potvrdiť");
        confirmButton.getStyleClass().add("start-button");
        confirmButton.setOnAction(e -> handleConfirmButtonClick(nameInputBox.getChildren(), numPlayers));

        // Tlačidlo pre návrat
        Button backButton = new Button("Späť");
        backButton.getStyleClass().add("start-button");
        backButton.setOnAction(e -> handleBackButtonClick());

        buttonBox.getChildren().addAll(checkAllBotsButton, randomNamesButton, backButton, confirmButton); // Pridanie tlačidiel do boxu

        VBox containerBox = new VBox(10);
        containerBox.getChildren().addAll(scrollPane, buttonBox, notificationLabel);
        containerBox.setAlignment(Pos.CENTER);

        // Automatické zaskrtnutie všetkých políčok pre bota
        if (autoBotSelectionCheckbox.isSelected()) {
            for (CheckBox checkBox : botCheckboxes) {
                checkBox.setSelected(true);
            }
            // Generovanie náhodných mien
            fillRandomNames();
        }

        // Odstránenie existujúceho obsahu a pridanie nového obsahu na panel
        pane.getChildren().clear();
        pane.getChildren().add(containerBox);
    }

    /**
     * Metóda pre spracovanie udalosti po kliknutí na tlačidlo "Potvrdiť".
     * @param List children
     * @param numPlayers pocet hracov aky sme zvolili
     */
    private void handleConfirmButtonClick(List<Node> children, int numPlayers) {
        List<Integer> emptyPlayerNumbers = new ArrayList<>(); // Zoznam pre uchovávanie čísel hráčov s prázdny mi

        // Kontrola vstupných políčok
        for (int i = 0; i < numPlayers; i++) {
            VBox playerBox = (VBox) children.get(i); // Get the VBox for each player
            HBox hbox = (HBox) playerBox.getChildren().get(1); // Get the HBox containing the text field and checkbox

            TextField textField = (TextField) hbox.getChildren().get(0); // Get the text field
            String playerName = textField.getText().trim();

            CheckBox botCheckBox = (CheckBox) hbox.getChildren().get(1); // Get the checkbox
            boolean isBot = botCheckBox.isSelected();

            if (playerName.isEmpty()) {
                emptyPlayerNumbers.add(i + 1);
            } else {
                if (isBot) {
                    playerName = "Bot " + playerName;
                }
                Player player = new BotPlayer(playerName, isBot);
                playerNames[i] = playerName;
            }
        }

    


        // Ak sú prázdne políčka
        if (!emptyPlayerNumbers.isEmpty()) {
            StringBuilder message = new StringBuilder("Prosím, vyplňte mená pre hráčov: ");
            for (int i = 0; i < emptyPlayerNumbers.size(); i++) {
                message.append(emptyPlayerNumbers.get(i));
                if (i < emptyPlayerNumbers.size() - 1) {
                    message.append(", ");
                }
            }
            notificationLabel.setText(message.toString());
            notificationLabel.setVisible(true);
        } else {
            // Pokračovanie na ďalší krok
            GameView gameView = new GameView(numPlayers, playerNames, difficultyComboBox.getValue(), questionLengthComboBox.getValue(), playIntroCheckbox);
            pane.getScene().setRoot(gameView.getPane());
        }
    }

    /**
     * Metóda pre generovanie náhodných mien hráčov.
     */
    private void fillRandomNames() {
        NameGenerator.generateRandomNames(nameInputBox);
    }

    /**
     * Metóda pre spracovanie udalosti po kliknutí na tlačidlo "Späť".
     * Vracia používateľa späť na úvodné menu.
     */
    private void handleBackButtonClick() {
        StartMenuView startMenu = new StartMenuView();
        pane.getScene().setRoot(startMenu.getPane());
    }

    /**
     * Metóda pre získanie scény pre zobrazenie menu pre výber hráčov.
     * 
     * @return Scéna obsahujúca panel pre menu pre výber hráčov
     */
    public Scene getScene() {
        return new Scene(pane, 400, 200);
    }

    /**
     * Metóda pre získanie panela menu pre výber hráčov.
     * 
     * @return Panel menu pre výber hráčov
     */
    public StackPane getPane() {
        return pane;
    }
}
