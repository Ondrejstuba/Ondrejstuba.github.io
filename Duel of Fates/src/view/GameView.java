package view;

import model.BotDifficulty;
import model.BotLogic;
import model.Choice;
import model.GameHelper;
import model.GameSerializer;
import model.GameViewInterface;
import model.PickItUpAction;
import model.QuestionLength;
import model.VoteCounter;
import model.LeaveItAction;
import model.Options;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Trieda zobrazenia pre rozhranie hry.
 */
public class GameView implements Serializable, GameViewInterface {
    private transient StackPane pane;
    private int currentPromptNumber = 1; 
    private final int numPlayers;
    private transient Text lastNotificationText; 
    private Timeline timeline; 
    private BotLogic botLogic;
    private VoteCounter voteCounter;
    private GameHelper gameHelper;
    private BackgroundTask backgroundTask;

    private final Map<String, Integer> votes = new HashMap<>();
    private Map<String, List<String>> optionVotesByPlayer = new HashMap<>();
    private Choice[] currentOptions; // Current set of options
    private int currentPlayerIndex = 0;
    private final String[] playerNames;
    private Set<String> excludedPlayers = new HashSet<>();
    private Map<String, Boolean> specialCardUsage = new HashMap<>();
    private Queue<Text> notificationQueue = new LinkedList<>();
    int questioncount = 0;
    
    private final Set<String> votedPlayers = new HashSet<>();
    
    private transient ScrollPane notificationScrollPane;
    private transient VBox notificationBox;
    private final BotDifficulty botDifficulty;
    private final QuestionLength questionLength; 
    
    private CheckBox playIntroCheckbox; 



    /**
     * Konštruktor pre triedu GameView.
     *
     * @param numPlayers       Počet hráčov v hre
     * @param playerNames      Pole mien hráčov
     * @param botDifficulty    Obtiažnosť botov a aj cas na otazku
     * @param questionLength   Dĺžka hry v počte otázok
     * @param playIntroCheckbox CheckBox pre prehrávanie úvodu
     */
    public GameView(int numPlayers, String[] playerNames, BotDifficulty botDifficulty, QuestionLength questionLength, CheckBox playIntroCheckbox) {
        

        this.numPlayers = numPlayers;
        this.playerNames = playerNames;
        this.botDifficulty = botDifficulty;
        this.questionLength = questionLength; 
        this.playIntroCheckbox = playIntroCheckbox;
        voteCounter = new VoteCounter(votes);
        this.botLogic = new BotLogic(botDifficulty);
        backgroundTask = new BackgroundTask();
        


        
        for (String playerName : playerNames) {
            specialCardUsage.put(playerName, false);
        }
        
        
        notificationBox = new VBox(10); // 10 je medzera medzi nimi tie notif.
        notificationScrollPane = new ScrollPane(notificationBox);
        notificationScrollPane.setPrefViewportHeight(150); // to ej to miesto kde budu
        
        
        
        
        currentOptions = Options.getRandomOptions();
        
     
        
        createPane();
        
        gameHelper = new GameHelper(pane);
        
        pane.getStylesheets().add(getClass().getResource("/view/stylesheet/menu.css").toExternalForm());
        
    }

    /**
     * Vytvorenie panelu.
     */
    public void createPane() {
        pane = new StackPane();
        pane.setAlignment(Pos.CENTER);
        
        Text introText = new Text("V tichu úsvitu sa nachádzate na rozcestí, cesta vpred je zahalená do hmly. Spomienky sa víria ako lístie vo vetre, úkazy a fragmenty. Hlas, jemný a predsa dôrazný, vás tlačí dopredu.\n\n"
                + "\"Vitaj, cestovateľ,\" šepká, jemná melódia vo vzduchu. \"V tomto raji nekonečných možností vaše rozhodnutia tvarujú samotný plášť existencie. Vyberte si svoju cestu múdro, lebo na každom rohu vás čaká osud.\"\n\n"
                + "S hlbokým nádychom kročíte vpred, neistota miešaná s očakávaním. Cesta vpred je vaša, každé rozhodnutie vlákno, ktoré je zasadené do tkaniny vášho osudu.");
        introText.setWrappingWidth(600);

        introText.getStyleClass().add("intro-text");
        
        Text promptNumberText = new Text("OTÁZKA " + currentPromptNumber);
        promptNumberText.getStyleClass().add("prompt-number-text");

        Text playerText = new Text("Hráč " + playerNames[currentPlayerIndex]);
        playerText.getStyleClass().add("player-text");

        Text questionText = new Text(currentOptions[0].getPromptText());
        questionText.getStyleClass().add("question-text");
        
        BackgroundImage backgroundImage = new BackgroundImage(
                new javafx.scene.image.Image(getClass().getResource("/view/graphics/images/intro.jpg").toExternalForm()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(1.0, 1.0, true, true, false, true)
        );
        pane.setBackground(new Background(backgroundImage));    
        
        pane.getChildren().add(backgroundTask.getLoadingPane());
        
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);

     

        //tu sa robi task...a to je ten loading effect
        backgroundTask.setOnTaskCompleted(() -> {
            // odstranime ho a dame ine uz
            pane.getChildren().remove(backgroundTask.getLoadingPane());
            
            
            
            if (playIntroCheckbox.isSelected()) {
                
            	TranslateTransition transition = new TranslateTransition(Duration.seconds(3), introText);
                transition.setFromY(600);
                transition.setToY(-400);
                
                contentBox.getChildren().addAll(introText);
                
                transition.setOnFinished(event -> {
                    contentBox.getChildren().remove(introText);
                    contentBox.getChildren().addAll(promptNumberText, playerText, questionText);
                    
                    if (playerNames[currentPlayerIndex].startsWith("Bot")) {
                        handleBotChoice(playerNames[currentPlayerIndex]);
                    } else {
                        // Display options for manual choice
                        for (Choice option : currentOptions) {
                            addOptionButton(contentBox, option);
                        }
                        addSpecialCardButton(contentBox);
                        startTimer(contentBox);

                    }

                    
                });
                transition.play();
            } else {
            	
            	TranslateTransition transition = new TranslateTransition(Duration.seconds(3), introText);
                transition.setFromY(600);
                transition.setToY(-400);
                
                contentBox.getChildren().addAll(introText);
                
                transition.setOnFinished(event -> {
                    contentBox.getChildren().remove(introText);
                    contentBox.getChildren().addAll(promptNumberText, playerText, questionText);
                    
                    //ak je bot tak nech robi sam                    
                    if (playerNames[currentPlayerIndex].startsWith("Bot")) {
                        handleBotChoice(playerNames[currentPlayerIndex]);
                    } else {
                    	//ak nie bot tak manualna volba
                        for (Choice option : currentOptions) {
                            addOptionButton(contentBox, option);
                        }
                        addSpecialCardButton(contentBox);
                        startTimer(contentBox);

                    }

                    
                });
                transition.playFrom(Duration.seconds(3)); // skipne na koniec
            	
            	
            }
            

            pane.getChildren().add(contentBox);        });
        

        
    }
    
    /**
     * Metóda na získanie správnej možnosti.
     * ked chceme mudreho bota napriklad
     *
     * @return Správna možnosť
     */
    public Choice getCorrectOption() {
        for (Choice option : currentOptions) {
            if (option.isCorrect()) {
                return option;
            }
        }
        return null; 
    }
    
    /**
     * Metóda na získanie nesprávnej možnosti.
     * napr ked chceme hlupeho bota
     * 
     * @return Nesprávna možnosť
     */    public Choice getFalseOption() {
        for (Choice option : currentOptions) {
            if (!option.isCorrect()) { // vlastne opak len correct
                return option;
            }
        }
        return null; // Return null if no correct option found (should not happen in a valid scenario)
    }
    
    
    //toto bude timer pri kazdej otazke pre kazdeho hraca
     /**
      * Metóda na spustenie časovača pre každého hráča pri každej otázke.
      *
      * @param contentBox Panel obsahujúci herný obsah
      */
  public void startTimer(VBox contentBox) {
        final int[] timerDuration = { botDifficulty.getTimerDuration() }; //ulozime to do array

        // toto je ten circle v ktorom bude nas timer
        StackPane timerPane = new StackPane();
        Circle circle = new Circle();
        Text timerText = new Text(String.valueOf(timerDuration[0]));

        circle.setRadius(19); // toto je velkost radius
        circle.setFill(Color.BLACK); // farba pozadia
        circle.setStroke(Color.WHITE); // farba hrany
        circle.setStrokeWidth(2); // sirka hrany
        timerPane.getChildren().addAll(circle, timerText);
        timerText.getStyleClass().add("timer-text");


        // najprv nech je ten scale 0
        timerPane.setScaleX(0);
        timerPane.setScaleY(0);

        // pridame tam ten timerpane aby ho bolo vidno
        contentBox.getChildren().addAll(timerPane);

       
        VBox.setMargin(timerPane, new Insets(50, 0, 0, 0));

        // zastavi stary timeline ak tam je...pri. otazke nie je ale potom uz ano
        if (timeline != null) {
            timeline.stop();
        }

        //vytvori timeline pre ten nas countdown otazky
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timerDuration[0]--; // Decrement countdown duration
            timerText.setText(String.valueOf(timerDuration[0]));

            // animacia countdown
            timerText.setFill(Color.rgb(255, (int) (255 * ((double) timerDuration[0] / botDifficulty.getTimerDuration())), 0)); // Change color gradually

            // stupa velkost stale
            double scale = 1 + (double) timerDuration[0] / (botDifficulty.getTimerDuration() / 2);
            // nemoze byt neg.
            scale = Math.max(scale, 1);
            timerPane.setScaleX(scale);
            timerPane.setScaleY(scale);

            if (timerDuration[0] <= 10) {
                // nech blika
                timerPane.setOpacity(timerDuration[0] % 2 == 0 ? 1 : 0.7);

                // alarmuje nas ze pozor uz malo casu...tak blika cerveno a cierno
                Color circleColor = timerDuration[0] % 2 == 0 ? Color.RED : Color.BLACK;
                circle.setFill(circleColor);
            }

            if (timerDuration[0] == 0) {
                handleVote(getFalseOption());
                moveToNextPlayer(); // ked bot vyberie ideme dalej
            }
        }));

        timeline.setCycleCount(timerDuration[0]); // to je ze kolko iteracii...no ale ja chcem 
        timeline.play(); // zacni
    }

  /**
   * Spracovanie výberu bota.
   *
   * @param botName Meno bota
   */
    public void handleBotChoice(String botName) {
        Choice botChoice = botLogic.makeChoice(currentOptions);

        
        votes.put(botChoice.getDescription(), votes.getOrDefault(botChoice.getDescription(), 0) + 1);
        
        // Update the list of players who voted for this option
        List<String> voters = optionVotesByPlayer.getOrDefault(botChoice.getDescription(), new ArrayList<>());
        voters.add(playerNames[currentPlayerIndex]);
        optionVotesByPlayer.put(botChoice.getDescription(), voters);

        votedPlayers.add(playerNames[currentPlayerIndex]);
        
        // Check if the choice made is false, if so, add the player to the excluded set
        if (!botChoice.isCorrect()) {
            excludedPlayers.add(playerNames[currentPlayerIndex]);
        }
        
        moveToNextPlayer(); // Move to the next player after the bot has chosen
        
        // Only show notification if difficulty is not hard or impossible
        if (botDifficulty != BotDifficulty.HARD && botDifficulty != BotDifficulty.IMPOSSIBLE) {
            // Create notification text for bot choice including bot's name
            Text notificationText = new Text(botName + " vykonal voľbu: " + botChoice.getDescription());
            notificationText.getStyleClass().add("notification-text");
            
            // Add notification text to the queue
            notificationQueue.add(notificationText);
            
            // Limit the number of notifications to MAX_NOTIFICATIONS
            if (notificationQueue.size() > 5) {
                Text removedNotification = notificationQueue.poll();
                VBox contentBox = (VBox) pane.getChildren().get(0);
                contentBox.getChildren().remove(removedNotification);
            }
            
            // Add the new notification text to the content box
            VBox contentBox = (VBox) pane.getChildren().get(0);
            contentBox.getChildren().add(notificationText);
            
            // Position the new notification text below the previous one
            if (lastNotificationText != null) {
                VBox.setMargin(notificationText, new Insets(10, 0, 0, 0)); // Add some margin between notifications
            }
            lastNotificationText = notificationText; // Update the last notification text
        }
    }
    
    /**
     * Pridáva tlačidlo pre možnosť do obsahového boxu pre zadanú voľbu.
     *
     * @param contentBox VBox, do ktorého sa má tlačidlo pridať.
     * @param choice Voľba reprezentujúca možnosť.
     */
    public void addOptionButton(VBox contentBox, Choice choice) {
        Button optionButton = new Button(choice.getDescription());
        optionButton.setOnAction(event -> handleVote(choice));
        optionButton.getStyleClass().add("option-button");
        contentBox.getChildren().add(optionButton);
    }

   
    /**
     * Pridáva tlačidlo pre špeciálnu kartu do obsahového boxu, ak hráč ešte nevyužil svoju špeciálnu kartu.
     *
     * @param contentBox VBox, do ktorého sa má tlačidlo pridať.
     */
    public  void addSpecialCardButton(VBox contentBox) {
        // Check if the player has already used their special card
        if (!specialCardUsage.containsKey(playerNames[currentPlayerIndex]) || !specialCardUsage.get(playerNames[currentPlayerIndex])) {
        	
        	// Create special card button
            Button specialCardButton = new Button("Použiť mojho žolíka");
            specialCardButton.getStyleClass().add("special-card-button");

            // Define event handler for special card button
            specialCardButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    handleSpecialCardButtonClick(playerNames[currentPlayerIndex]);
                }
            });

            
            

            contentBox.getChildren().add(specialCardButton);
        }
    }

       
    /**
     * Spracováva hlasovanie pre zadanú voľbu.
     *
     * @param choice Voľba reprezentujúca možnosť, za ktorú hlasuje hráč.
     */
    public void handleVote(Choice choice) {
        if (!votedPlayers.contains(playerNames[currentPlayerIndex])) {
        	
        	     	
            votes.put(choice.getDescription(), votes.getOrDefault(choice.getDescription(), 0) + 1);
            
            // Update the list of players who voted for this option
            List<String> voters = optionVotesByPlayer.getOrDefault(choice.getDescription(), new ArrayList<>());
            voters.add(playerNames[currentPlayerIndex]);
            optionVotesByPlayer.put(choice.getDescription(), voters);

            votedPlayers.add(playerNames[currentPlayerIndex]);
            
            // Check if the choice made is false, if so, add the player to the excluded set
            if (!choice.isCorrect()) {
                excludedPlayers.add(playerNames[currentPlayerIndex]);
            }
            
            
         // RTTI example
            if (choice.getAction() instanceof PickItUpAction) {
                System.out.println("Choice is a PickItUpAction");
            } else if (choice.getAction() instanceof LeaveItAction) {
                System.out.println("Choice is a LeaveItAction");
            } else {
                System.out.println("Choice is of unknown type");
            }

            if (playerNames[currentPlayerIndex].startsWith("Bot")) {
                handleBotChoice(playerNames[currentPlayerIndex]); 
            }
            
            moveToNextPlayer(); 
    }
    }

    /**
     * Presúva hru na ďalší ťah hráča.
     * iterator pattern
     */
    public void moveToNextPlayer() {
        currentPlayerIndex++;
        
        // Check if all players are excluded
        boolean allPlayersExcluded = true;
        for (String playerName : playerNames) {
            if (!excludedPlayers.contains(playerName)) {
                allPlayersExcluded = false;
                break;
            }
        }
        
        if (currentPlayerIndex < numPlayers) {
        	
        	// Skip the first player if they are excluded
            if (currentPlayerIndex == 0 && excludedPlayers.contains(playerNames[currentPlayerIndex])) {
                moveToNextPlayer();
                return;
            }
        	
        	//skip players that are excluded
            if (excludedPlayers.contains(playerNames[currentPlayerIndex])) {
                moveToNextPlayer();
                return;
            }

            VBox contentBox = (VBox) pane.getChildren().get(0);
            contentBox.getChildren().clear();
            

            // Add progress bar
            ProgressBar progressBar = new ProgressBar();
            progressBar.setPrefWidth(400); // Adjust width as needed
            progressBar.setProgress((double) currentPlayerIndex / numPlayers); // Use remainingPlayers here
            contentBox.getChildren().add(progressBar);
            
         // Set the prompt text based on the current set of options
            Text promptNumberText = new Text("OTÁZKA " + currentPromptNumber);
            promptNumberText.getStyleClass().add("prompt-number-text");

            Text playerText = new Text("Hráč " + playerNames[currentPlayerIndex]);
            playerText.getStyleClass().add("player-text");

            Text questionText = new Text(currentOptions[0].getPromptText());
            questionText.getStyleClass().add("question-text");

            contentBox.getChildren().addAll(promptNumberText, playerText, questionText);
            
            



            // toto je lambda napriklad
            contentBox.getChildren().removeIf(node -> node instanceof Button || (node instanceof Text && !node.equals(promptNumberText) && !node.equals(playerText) && !node.equals(questionText)));

            // Clear votedPlayers set for the next player
            votedPlayers.clear();

            if (playerNames[currentPlayerIndex].startsWith("Bot")) {
                handleBotChoice(playerNames[currentPlayerIndex]); // Automatically handle bot choice
            } else {
                // Add option buttons for manual choice
                for (Choice option : currentOptions) {
                    addOptionButton(contentBox, option);
                                 }
                addSpecialCardButton(contentBox);
                
                startTimer(contentBox);

            }

        } else {
        	
        	if (timeline != null) {
                timeline.stop();
            }
        	
        	VBox contentBox = (VBox) pane.getChildren().get(0);
            contentBox.getStyleClass().add("contentboxinsummary");
        	contentBox.getChildren().removeIf(node -> node instanceof Button || node instanceof Text);
        	contentBox.getChildren().clear();

        	// Text for result notification
        	Text resultTextONE = new Text("HLASOVANIE SA SKONČILO.");
        	 resultTextONE.getStyleClass().add("result-text");
             
        	// Determine winning option
        	String winningOption = determineWinningOption();
        	
        	// Print the amount of players left
        	Text playersLeftText = new Text("Zostávajúci počet hráčov: " + (numPlayers - excludedPlayers.size()));
        	playersLeftText.getStyleClass().add("result-text");

        	// Text displaying the winning option
        	Text resultText = new Text("Najviac hlasov dostala možnosť: " + winningOption);
        	resultText.getStyleClass().add("result-text");
            
            contentBox.getChildren().addAll(resultTextONE, playersLeftText, resultText);
            


        	// Display the true option
        	for (Choice option : currentOptions) {
        	    if (option.isCorrect()) {
        	    	
        	    	
        	    	
        	        Text trueOptionText = new Text("Správna možnosť bola: " + option.getDescription());
        	        trueOptionText.getStyleClass().add("true-option-text");
        	        contentBox.getChildren().add(trueOptionText);
        	        break; // No need to continue once the true option is found
        	    }
        	}

        	for (Choice option : currentOptions) {
        	    Text optionStatistics = new Text(option.getDescription() + ": " + votes.getOrDefault(option.getDescription(), 0));
        	    optionStatistics.getStyleClass().add(option.isCorrect() ? "option-statistics-correct" : "option-statistics-incorrect");
        	    contentBox.getChildren().add(optionStatistics);

        	    List<String> votersList = optionVotesByPlayer.getOrDefault(option.getDescription(), new ArrayList<>());
        	    if (!votersList.isEmpty()) { 
        	    	
        	    	
        	    	
        	    	
        	    	ImageView rolloverButtonIcon = new ImageView(new Image(getClass().getResource("/view/graphics/images/down.png").toExternalForm()));
        	    	rolloverButtonIcon.setFitWidth(20); 
        	    	rolloverButtonIcon.setFitHeight(20);

        	    	// button aby sme sa posunuli dole a ukaze tie mena potom
        	    	Button rolloverButton = new Button();
        	    	rolloverButton.setGraphic(rolloverButtonIcon);
        	    	rolloverButton.getStyleClass().add("downup-button"); // rovnaky style 
        	    	
        	        

        	    	// vytvorime obsah
        	    	GridPane optionContent = new GridPane(); // tu to uchovame
        	    	optionContent.setHgap(10); // medzery
        	    	optionContent.setVgap(5); // medzery

        	    	int namesPerRow = 6;
        	    	int row = 0;
        	    	int col = 0;

        	    	for (int i = 0; i < votersList.size(); i++) {
        	    	    Text voterText = new Text(votersList.get(i));
        	    	    voterText.getStyleClass().add("voter-text");

        	    	    optionContent.add(voterText, col, row); // pridame akt meno

        	    	    // presunieme sa na dalsi riadok ked treba
        	    	    if ((i + 1) % namesPerRow == 0) {
        	    	        row++;
        	    	        col = 0;
        	    	    } else {
        	    	        col++;
        	    	    }
        	    	}



        	        ScrollPane scrollPane = new ScrollPane(optionContent); // dame to do scroll pane
        	        scrollPane.getStyleClass().add("scroll-pane-of-voters");
        	        scrollPane.setFitToWidth(true); // horizontalne resiznutie
        	        scrollPane.setPrefViewportHeight(100);
        	        scrollPane.setFitToHeight(true);
        	        scrollPane.setMaxHeight(400); //ake velke to bude
        	        scrollPane.setMaxWidth(600); // toto tiez
        	        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); //aby nebolo napravo to vidiet tu evc
        	        scrollPane.setVisible(false); //najprv nevidime
        	        
        	        
        	        //toto je akcia
        	        rolloverButton.setOnAction(event -> {
        	            if (!contentBox.getChildren().contains(scrollPane)) {
        	                contentBox.getChildren().add(contentBox.getChildren().indexOf(rolloverButton), scrollPane);
        	                rolloverButton.setGraphic(new ImageView(new Image(getClass().getResource("/view/graphics/images/up.png").toExternalForm())));
        	                scrollPane.setVisible(true);
        	            } else {
        	                contentBox.getChildren().remove(scrollPane);
        	                rolloverButton.setGraphic(new ImageView(new Image(getClass().getResource("/view/graphics/images/down.png").toExternalForm())));
        	            }
        	        });


        	        contentBox.getChildren().add(rolloverButton);
        	    }
        	}





        

        	// Create buttons for options
        	Button tryAgainButton = createStyledButton("Try Again");
        	tryAgainButton.getStyleClass().add("button-styled");
        	tryAgainButton.setOnAction(e -> handleTryAgainButtonClick()); 


        	//next button na moznost dalej
        	Button nextButton = createStyledButton("Next");
        	nextButton.getStyleClass().add("button-styled");
        	nextButton.setOnAction(e -> handleNext());
        	
        	if (allPlayersExcluded) {
        	    nextButton.setText("End Game");
        	}
        	        	
            if (questioncount + 1 >= questionLength.getLength()) {
            	nextButton.setText("Win Game");
            }

            //tu tie buttons vsetky pekne dame
        	HBox buttonContainer = new HBox(10); // medzera medzi nimi
        	buttonContainer.setAlignment(Pos.CENTER);
        	buttonContainer.getChildren().addAll(nextButton, tryAgainButton);

        	contentBox.getChildren().add(buttonContainer);
        }
    }
    
    /**
     * Spracováva prechod na ďalšiu sadu otázok v hre.
     */
    public void handleNext() {
    	

    	//ale neprejdeme a dame endgame ak uz nie su hraci...aspon jeden musi byt aby sa hralo dalej
    	boolean allPlayersExcluded = true;
        for (String playerName : playerNames) {
            if (!excludedPlayers.contains(playerName)) {
                allPlayersExcluded = false;
                break;
            }
        }
    	
    	if (allPlayersExcluded) {
            endGame();
            return;
        }
    	
        votes.clear();
        currentPlayerIndex = 0;
        votedPlayers.clear();
        currentPromptNumber++; 


        VBox contentBox = (VBox) pane.getChildren().get(0);
        contentBox.getChildren().clear();
        
        
        
        
        GameView game = new GameView(numPlayers, playerNames, botDifficulty, questionLength, playIntroCheckbox);
        GameSerializer packageView = new GameSerializer();
        packageView.saveGame("saved_snapshot.ser", game, votes, optionVotesByPlayer,
                votedPlayers, excludedPlayers, currentPromptNumber,
                currentPlayerIndex, questioncount, currentPromptNumber);
        

     
        
        
        currentOptions = Options.getRandomOptions();

        
        questioncount++;
        
        if (questioncount >= questionLength.getLength()) {
        	winGame();
            return;
        }
        
        
        // Skip the first player if they are excluded
        if (currentPlayerIndex == 0 && excludedPlayers.contains(playerNames[currentPlayerIndex])) {
            moveToNextPlayer();
            return;
        }
        
        
        
        Text promptNumberText = new Text("QUESTION " + currentPromptNumber);
        promptNumberText.getStyleClass().add("prompt-number-text");

        Text playerText = new Text("Player " + playerNames[currentPlayerIndex]);
        playerText.getStyleClass().add("player-text");

        Text questionText = new Text(currentOptions[0].getPromptText());
        questionText.getStyleClass().add("question-text");
     
        contentBox.getChildren().addAll(promptNumberText, playerText, questionText);
        
        
        
        
        if (playerNames[currentPlayerIndex].startsWith("Bot")) {
            handleBotChoice(playerNames[currentPlayerIndex]); // Automatically handle bot choice
        } else {
            // Add option buttons for manual choice
            for (Choice option : currentOptions) {
                addOptionButton(contentBox, option);

            }
            addSpecialCardButton(contentBox);
            startTimer(contentBox);

        }



    }
    
    /**
     * Ukazuje koniec hry a zobrazuje zobrazenie konca hry.
     */   
    public void endGame() {
        // Create an instance of EndGameView
        EndGameView endGameView = new EndGameView(pane.getWidth(), pane.getHeight());
        StackPane endGamePane = endGameView.getPane();

        // Replace the current content with the end game view
        pane.getChildren().clear();
        pane.getChildren().add(endGamePane);
    }
    
    /**
     * ked jeden alebo viac hracov vyhra hru a zobrazuje zobrazenie výhry.
     */
    public void winGame() {
        // Determine the number of players left in the game
        int playersLeft = numPlayers - excludedPlayers.size();

        // Get the names of the winning players
        List<String> winnersList = new ArrayList<>();
        for (String playerName : playerNames) {
            if (!excludedPlayers.contains(playerName)) {
                winnersList.add(playerName);
            }
        }

        // Create an instance of WinGameView with the list of winners
        WinGameView winGameView = new WinGameView(pane.getWidth(), pane.getHeight(), playersLeft, winnersList);
        StackPane winGamePane = winGameView.getPane();

        // Replace the current content with the win game view
        pane.getChildren().clear();
        pane.getChildren().add(winGamePane);
    }
    
    /**
     * Vytvára štýlované tlačidlo s určitým textom.
     *
     * @param text Text, ktorý sa má zobraziť na tlačidle.
     * @return Štýlované tlačidlo.
     */
    public Button createStyledButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("styled-button");

        // Add hover effect
        button.setOnMouseEntered(e -> {
            button.getStyleClass().add("hovered");
            button.setScaleX(1.1);
            button.setScaleY(1.1);
        });

        button.setOnMouseExited(e -> {
            button.getStyleClass().remove("hovered");
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });

        return button;
    }

    /**
     * Spracováva udalosť kliknutia na tlačidlo "Skúsiť znova".
     */
    public void handleTryAgainButtonClick() {
        StartMenuView startMenu = new StartMenuView();
        pane.getScene().setRoot(startMenu.getPane());
    }

    //toto vzdy rozhodne ktora moznost vyhrala...zavola tu triedu
    /**
     * Určuje výhernú možnosť na základe hlasov.
     *
     * @return Popis výhernej možnosti.
     */
    public String determineWinningOption() {
        return voteCounter.determineWinningOption();
    }
    
    //toto je na to aby sa vykonalo pouzitie esa
    /**
     * Používa špeciálnu kartu pre zadaného hráča.
     *
     * @param playerName Meno hráča, ktorý používa špeciálnu kartu.
     */
    public void useSpecialCard(String playerName) {
        gameHelper.useSpecialCard(playerName, specialCardUsage, getCorrectOption());
    }

    /**
     * Získava panel hry.
     *
     * @return StackPane obsahujúci panel hry.
     */
    public StackPane getPane() {
        return pane;
    }

    /**
     * Získava scénu hry.
     *
     * @return Scéna reprezentujúca panel hry.
     */
    public Scene getScene() {
        return new Scene(pane, 800, 600); // Adjust size as needed
    }
}