package view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import java.util.prefs.Preferences;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

/**
 * Trieda StartMenuView predstavuje pohľad na začiatok hry.
 */
public class StartMenuView {

	private StackPane pane;
    private boolean autoBotSelectionEnabled = false; // Deklarácia na úrovni triedy
    private boolean playIntroEnabled = false; // Nová premenná pre možnosť úvodu

    //toto je generickost
    private CheckBox autoBotSelectionCheckbox; // Odkaz na checkbox
    private CheckBox playIntroCheckbox; // Nový checkbox pre možnosť úvodu


    //singleton pattern (uskladnute preferences napriec celou appkou)
    private Preferences prefs;

    /**
     * Vytvorí nový pohľad na začiatok hry.
     */
    public StartMenuView() {
        prefs = Preferences.userNodeForPackage(getClass());
        autoBotSelectionEnabled = prefs.getBoolean("autoBotSelectionEnabled", false);
        playIntroEnabled = prefs.getBoolean("playIntroEnabled", true); // Načítať stav úvodu
        createPane();
    }

    /**
     * Vytvorí kontajner pre pohľad na začiatok hry.
     */
    private void createPane() {
        pane = new StackPane();
        pane.setAlignment(Pos.CENTER); // Zarovnanie obsahu na stred StackPane
        
        //tu si tieto dve premenné vytvorím a ich hodnota sa uchová aj keď vypneme hru
        autoBotSelectionCheckbox = new CheckBox("Automaticky vybrať všetkých hráčov ako botov a vygenerovať ich mená");
        autoBotSelectionCheckbox.setSelected(autoBotSelectionEnabled); // Nastaviť počiatočný stav
        autoBotSelectionCheckbox.getStyleClass().add("auto-bot-checkbox"); // Pridať CSS triedu
        
     
        playIntroCheckbox = new CheckBox("Prehrať úvodný cinematic na začiatku hry");
        playIntroCheckbox.setSelected(playIntroEnabled); // Nastaviť počiatočný stav
        playIntroCheckbox.getStyleClass().add("auto-bot-checkbox"); // Pridať CSS triedu

        

        

        Image backgroundImage = new Image(getClass().getResource("/view/graphics/images/menu.png").toExternalForm());
        BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(1.0, 1.0, true, true, false, true)
        );

        pane.setBackground(new Background(background));

        // Vytvoriť VBox pre obsah menu
        VBox vbox = new VBox(20);
        HBox hbox = new HBox(20); // Medzera medzi tlačidlami

        vbox.setAlignment(Pos.CENTER); // Zarovnanie obsahu na stred VBox
        hbox.setAlignment(Pos.CENTER); // Zarovnanie obsahu na stred VBox


        ImageView gameLogo = new ImageView(new Image(getClass().getResource("/view/graphics/images/logo.png").toExternalForm()));
        gameLogo.setFitWidth(400);
        gameLogo.setFitHeight(190);

        Label gameDescription = new Label("Najlepšia hra na hlasovanie pre celú rodinu");
        gameDescription.getStyleClass().add("description-label");

           // Tlačidlo na začiatok hry
        Button startButton = new Button("Začať hru");
        startButton.getStyleClass().add("start-button");
        startButton.setOnAction(e -> handleStartButtonClick());
        
        // Tlačidlo na ukončenie hry
        Button quitButton = new Button("Ukončiť hru");
        quitButton.getStyleClass().add("quit-button");
        quitButton.setOnAction(e -> Platform.exit()); // Ukončí aplikáciu
        
      
     // Načítať obrázok pre tlačidlo nastavení
        ImageView settingsIcon = new ImageView(new Image(getClass().getResource("/view/graphics/images/settings.png").toExternalForm()));
        settingsIcon.setFitWidth(20); // Prispôsobte veľkosť ikony podľa potreby
        settingsIcon.setFitHeight(20);

        // Vytvoriť tlačidlo nastavení s obrázkom
        Button settingsButton = new Button();
        settingsButton.setGraphic(settingsIcon);
        settingsButton.getStyleClass().add("settings-button");
        settingsButton.setMinSize(40, 40); // Prispôsobte veľkosť tlačidla podľa ikony
        settingsButton.setMaxSize(40, 40); // Prispôsobte veľkosť tlačidla podľa ikony
        settingsButton.setOnAction(e -> openSettingsView());

        
     // Načítať obrázok pre tlačidlo kreditov
        ImageView creditsIcon = new ImageView(new Image(getClass().getResource("/view/graphics/images/credits.png").toExternalForm()));
        creditsIcon.setFitWidth(20); // Prispôsobte veľkosť ikony podľa potreby
        creditsIcon.setFitHeight(20);

        // Vytvoriť tlačidlo kreditov s obrázkom
        Button creditsButton = new Button();
        creditsButton.setGraphic(creditsIcon);
        creditsButton.getStyleClass().add("credits-button");
        creditsButton.setMinSize(40, 40); // Prispôsobte veľkosť tlačidla podľa ikony
        creditsButton.setMaxSize(40, 40); // Prispôsobte veľkosť tlačidla podľa ikony
        creditsButton.setOnAction(e -> openCreditsView());

        
     // Načítať obrázok pre tlačidlo tutoriálu
        ImageView tutorialIcon = new ImageView(new Image(getClass().getResource("/view/graphics/images/tutorial.png").toExternalForm()));
        tutorialIcon.setFitWidth(20); // Prispôsobte veľkosť ikony podľa potreby
        tutorialIcon.setFitHeight(20);

        // Vytvoriť tlačidlo tutoriálu s obrázkom
        Button tutorialButton = new Button();
        tutorialButton.setGraphic(tutorialIcon);
        tutorialButton.getStyleClass().add("tutorial-button");
        tutorialButton.setMinSize(40, 40); // Prispôsobte veľkosť tlačidla podľa ikony
        tutorialButton.setMaxSize(40, 40); // Prispôsobte veľkosť tlačidla podľa ikony
        tutorialButton.setOnAction(e -> openTutorialView());
        
        // Označenie verzie
        Label versionLabel = new Label("Verzia 1.0");
        versionLabel.getStyleClass().add("version-label");
        
        // Načítať obrázky pre označenie verzie
        ImageView logo1 = new ImageView(new Image(getClass().getResource("/view/graphics/images/logostu.png").toExternalForm()));
        ImageView logo2 = new ImageView(new Image(getClass().getResource("/view/graphics/images/logofiit.jpg").toExternalForm()));

        // Prispôsobte veľkosť obrázkov podľa potreby
        logo1.setFitWidth(65);
        logo1.setFitHeight(55);
        logo2.setFitWidth(65);
        logo2.setFitHeight(55);

        // Vytvorte VBox pre označenie verzie a zarovnajte ho vpravo dole
        VBox versionBox = new VBox(25, logo1, logo2, versionLabel);
        versionBox.setAlignment(Pos.BOTTOM_RIGHT);
        versionBox.setPadding(new Insets(0, 5, -170, 0)); // Pridať odsadenie vpravo dole

        
        
        
        
        

        
        hbox.getChildren().addAll(settingsButton, creditsButton, tutorialButton); // Pridať tlačidlá do HBox

        vbox.getChildren().addAll(gameLogo, gameDescription, startButton, quitButton, hbox);
        vbox.getChildren().addAll(versionBox); // Pridať box s označením verzie do VBox


        vbox.getStyleClass().add("menu-box"); // Použiť štýl menu-box na VBox

       vbox.getStylesheets().add(getClass().getResource("/view/stylesheet/menu.css").toExternalForm());

        pane.getChildren().addAll(vbox);
    }



    /**
     * Spracuje udalosť kliknutia na tlačidlo "Začať hru".
     */
    private void handleStartButtonClick() {
        PlayerSelectionMenuView playerSelectionMenu = new PlayerSelectionMenuView(autoBotSelectionCheckbox, playIntroCheckbox);
       // System.out.println("autoBotSelectionCheckbox value: " + autoBotSelectionCheckbox.isSelected());
       // System.out.println("and intro value: " + playIntroCheckbox.isSelected());


        pane.getScene().setRoot(playerSelectionMenu.getPane());
    }
    
    /**
     * Získa kontajner s pohľadom na začiatok hry.
     * 
     * @return Kontajner s pohľadom na začiatok hry.
     */
    // Exponovať panel pre externý prístup
    public StackPane getPane() {
        return pane;
    }

    /**
     * Získa scénu s pohľadom na začiatok hry.
     * 
     * @return Scéna s pohľadom na začiatok hry.
     */
    public Scene getScene() {
        return new Scene(pane, 400, 200);
    }
    
    /**
     * Otvorí zobrazenie nastavení.
     */
 // Definovať metódu na otvorenie zobrazenia nastavení
    private void openSettingsView() {
        // Vytvoriť obsah zobrazenia nastavení
        Label settingsLabel = new Label("Zobrazenie nastavení");
        settingsLabel.getStyleClass().add("description-label"); // Pridať ľubovoľné štýly
        
        // Načítať obrázok pre tlačidlo späť
        ImageView backButtonIcon = new ImageView(new Image(getClass().getResource("/view/graphics/images/back.png").toExternalForm()));
        backButtonIcon.setFitWidth(20); // Prispôsobte veľkosť ikony podľa potreby
        backButtonIcon.setFitHeight(20);
        
        
  
        
        // Vytvoriť tlačidlo späť s obrázkom
        Button backButton = new Button();
        backButton.setGraphic(backButtonIcon);
        backButton.getStyleClass().add("back-button"); // Použiť rovnaký štýl ako ostatné tlačidlá
        backButton.setOnAction(e -> {
            StartMenuView startMenuView = new StartMenuView(); // Znova vytvoriť zobrazenie hlavného menu
            pane.getScene().setRoot(startMenuView.getPane()); // Nastaviť hlavné menu ako koreň
        });
        
        autoBotSelectionCheckbox.setOnAction(e -> {
            autoBotSelectionEnabled = autoBotSelectionCheckbox.isSelected();
            prefs.putBoolean("autoBotSelectionEnabled", autoBotSelectionEnabled); // Aktualizovať uložený stav
        });
        
        playIntroCheckbox.setOnAction(e -> {
            playIntroEnabled = playIntroCheckbox.isSelected();
            prefs.putBoolean("playIntroEnabled", playIntroEnabled); // Uložiť stav možnosti úvodu
        });
        
        
        



        
        // Vytvoriť VBox pre obsah zobrazenia nastavení
        VBox settingsPane = new VBox(20);
        settingsPane.setAlignment(Pos.CENTER); // Zarovnanie obsahu na stred VBox
        settingsPane.getChildren().addAll(settingsLabel, autoBotSelectionCheckbox, playIntroCheckbox, backButton); // Pridať tlačidlo späť a označenie nastavení
        
        // Nahradenie existujúceho obsahu v paneli obsahom zobrazenia nastavení
        pane.getChildren().clear(); // Vyčistiť existujúci obsah
        pane.getChildren().add(settingsPane); // Pridať obsah zobrazenia nastavení
        
        settingsPane.getStylesheets().add(getClass().getResource("/view/stylesheet/menu.css").toExternalForm());
    }
    
    
    
    

    /**
     * Otvorí zobrazenie kreditov.
     */
    private void openCreditsView() {
        VBox tutorialPane = new VBox(20);
        tutorialPane.setAlignment(Pos.CENTER);

        Text introText = new Text("KREDITY\n\n"
                + "Vývojový tím:\n"
                + "- Ondrej Drengubiak\n\n"
                + "Grafika:\n"
                + "- Ondrej Drengubiak\n\n"
                + "Špeciálne poďakovanie:\n"
                + "- STU FIIT\n\n"
                + "© 2024 Ondrej Drengubiak. Všetky práva vyhradené.");
        introText.setWrappingWidth(600);
        introText.setStyle("-fx-font-size: 18px;\n" +
                "-fx-fill: white;");

        ImageView backButtonIcon = new ImageView(new Image(getClass().getResource("/view/graphics/images/back.png").toExternalForm()));
        backButtonIcon.setFitWidth(20);
        backButtonIcon.setFitHeight(20);

        ImageView retryButtonIcon = new ImageView(new Image(getClass().getResource("/view/graphics/images/restart.png").toExternalForm()));
        retryButtonIcon.setFitWidth(20);
        retryButtonIcon.setFitHeight(20);

        Button backButton = new Button();
        backButton.setGraphic(backButtonIcon);
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> {
            StartMenuView startMenuView = new StartMenuView();
            pane.getScene().setRoot(startMenuView.getPane());
        });

        Button retryButton = new Button();
        retryButton.setGraphic(retryButtonIcon);
        retryButton.getStyleClass().add("back-button");
        retryButton.setOnAction(e -> openCreditsView());

        HBox buttonPane = new HBox(20);
        buttonPane.setAlignment(Pos.TOP_RIGHT);
        buttonPane.setPadding(new Insets(0, 380, 120, 0));
        buttonPane.getChildren().addAll(retryButton, backButton);

        tutorialPane.getChildren().addAll(introText, buttonPane);

        pane.getChildren().clear();
        pane.getChildren().add(tutorialPane);

        tutorialPane.getStylesheets().add(getClass().getResource("/view/stylesheet/menu.css").toExternalForm());

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(45), introText);
        translateTransition.setFromY(pane.getHeight()); // Začať z dna
        translateTransition.setToY(-400);
        translateTransition.setInterpolator(Interpolator.LINEAR);
        translateTransition.setOnFinished(event -> {
            tutorialPane.getChildren().remove(introText);
        });

        translateTransition.play();

        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(20), introText);
        rotateTransition.setAxis(Rotate.X_AXIS);
        rotateTransition.setByAngle(-90);
        rotateTransition.setInterpolator(Interpolator.LINEAR);
        rotateTransition.play();
    }



    
    
    /**
     * Otvorí zobrazenie tutoriálu.
     */
    private void openTutorialView() {
        // Vytvoriť obsah zobrazenia tutoriálu
        VBox tutorialPane = new VBox(20);
        tutorialPane.setAlignment(Pos.CENTER); // Zarovnanie obsahu na stred VBox

        Label tutorialLabel = new Label("Tutoriál");
        tutorialLabel.getStyleClass().add("description-label"); // Pridať ľubovoľné štýly

        // Nastavenie úvodného textu
        Text introText = new Text("V tichu ranného svitanie sa ocitáte na križovatke, cesta vpred skrytá v hmle. Spomienky sa točia ako listy vo vetre, prechodné a fragmentované. Hlas, mäkký a dôrazný, vás núti ísť vpred.\n\n"
                + "\"Vitaj, cestovateľ,\" šepká, jemná melódia vo vzduchu. \"V tomto svete nekonečných možností vaše rozhodnutia tvarujú samotnú podstatu existencie. Vyberte si svoju cestu múdro, pretože na každom zákrute vás čaká osud.\"\n\n"
                + "Hlbokým dychom vkročíte vpred, neistota sa mieša s očakávaním. Pred vami je cesta, ktorú máte navigovať, každé rozhodnutie nitka zasadená do vlny vašej osudu.");
        introText.setWrappingWidth(600);
        introText.setStyle("-fx-font-size: 18px;\n" +
                "-fx-fill: white;");
        

        // Načítať obrázok pre tlačidlo späť
        ImageView backButtonIcon = new ImageView(new Image(getClass().getResource("/view/graphics/images/back.png").toExternalForm()));
        backButtonIcon.setFitWidth(20); // Prispôsobte veľkosť ikony podľa potreby
        backButtonIcon.setFitHeight(20);

        // Načítať iný obrázok pre tlačidlo opätovného pokusu
        ImageView retryButtonIcon = new ImageView(new Image(getClass().getResource("/view/graphics/images/restart.png").toExternalForm()));
        retryButtonIcon.setFitWidth(20); // Prispôsobte veľkosť ikony podľa potreby
        retryButtonIcon.setFitHeight(20);

        // Vytvoriť tlačidlo späť s obrázkom
        Button backButton = new Button();
        backButton.setGraphic(backButtonIcon);
        backButton.getStyleClass().add("back-button"); // Použiť rovnaký štýl ako iné tlačidlá
        backButton.setOnAction(e -> {
            StartMenuView startMenuView = new StartMenuView(); // Znova vytvoriť zobrazenie hlavného menu
            pane.getScene().setRoot(startMenuView.getPane()); // Nastaviť hlavné menu ako koreň
        });

        // Vytvoriť tlačidlo opätovného pokusu s iným obrázkom
        Button retryButton = new Button();
        retryButton.setGraphic(retryButtonIcon); // Nastaviť iný obrázok pre tlačidlo opätovného pokusu
        retryButton.getStyleClass().add("back-button"); // Použiť rovnaký štýl ako tlačidlo späť
        retryButton.setOnAction(e -> openTutorialView()); // Opakovať zobrazenie tutoriálu

        // Vytvoriť HBox pre tlačidlá a pridať odsadenie
        HBox buttonPane = new HBox(20);
        buttonPane.setAlignment(Pos.TOP_RIGHT); // Zarovnať tlačidlá vpravo hore
        buttonPane.setPadding(new Insets(0, 380, 120, 0)); // Pridať odsadenie vpravo hore
        buttonPane.getChildren().addAll(retryButton, backButton);

        // Pridať prvky do panela tutoriálu
        tutorialPane.getChildren().addAll(tutorialLabel, introText, buttonPane);

        // Nahradenie existujúceho obsahu v paneli obsahom zobrazenia tutoriálu
        pane.getChildren().clear(); // Vyčistiť existujúci obsah
        pane.getChildren().add(tutorialPane); // Pridať obsah zobrazenia tutoriálu

        tutorialPane.getStylesheets().add(getClass().getResource("/view/stylesheet/menu.css").toExternalForm());

        // Posunúť text tutoriálu z dna nahor a potom zmiznúť
        TranslateTransition transition = new TranslateTransition(Duration.seconds(10), introText);
        transition.setFromY(600); // Začať z dna obrazovky
        transition.setToY(-400); // Posunúť na vrch obrazovky
        transition.setOnFinished(event -> {
            // Po posune odstrániť úvodný text z panela tutoriálu
            tutorialPane.getChildren().remove(introText);
        });
        transition.play();
    }
}
