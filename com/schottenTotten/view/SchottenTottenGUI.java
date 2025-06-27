package com.schottenTotten.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import com.schottenTotten.model.*;
import com.schottenTotten.controller.variantes.*;
import com.schottenTotten.ai.*;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.application.Platform;
import java.util.Set;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
public class SchottenTottenGUI extends Application {
    private IJeu jeu;
    private Stage primaryStage;
    private VBox mainLayout;
    private HBox bornesArea;
    private HBox playerHand;
    private Label statusLabel;
    private boolean isGameStarted = false;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showWelcomeScreen();
    }

    private void showWelcomeScreen() {
        VBox welcomeLayout = new VBox(20);
        welcomeLayout.setAlignment(Pos.CENTER);
        welcomeLayout.setPadding(new Insets(50));
        welcomeLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #2c5530, #1a331d);");

        // Titre du jeu
        Label titleLabel = new Label("SCHOTTEN-TOTTEN");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titleLabel.setStyle("-fx-text-fill: #FFD700; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");

        // Sous-titre
        Label subtitleLabel = new Label("Un jeu de stratégie pour 2 joueurs");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        subtitleLabel.setStyle("-fx-text-fill: white;");

        // Conteneur pour les options de jeu
        VBox gameOptionsBox = new VBox(15);
        gameOptionsBox.setAlignment(Pos.CENTER);
        gameOptionsBox.setPadding(new Insets(20));
        gameOptionsBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 10;");
        gameOptionsBox.setMaxWidth(400);

        // Choix de la variante
        Label varianteLabel = new Label("Choisissez votre variante :");
        varianteLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        ComboBox<String> varianteChoice = new ComboBox<>();
        varianteChoice.getItems().addAll("Normal", "Tactique", "Expert");
        varianteChoice.setValue("Normal");
        varianteChoice.setMaxWidth(Double.MAX_VALUE);
        varianteChoice.setStyle("-fx-font-size: 14px;");

        // Mode de jeu
        Label modeLabel = new Label("Mode de jeu :");
        modeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        ComboBox<String> gameModeChoice = new ComboBox<>();
        gameModeChoice.getItems().addAll("Joueur vs Joueur", "Joueur vs IA");
        gameModeChoice.setValue("Joueur vs Joueur");
        gameModeChoice.setMaxWidth(Double.MAX_VALUE);
        gameModeChoice.setStyle("-fx-font-size: 14px;");

        // Niveau de l'IA
        Label niveauLabel = new Label("Niveau de l'IA :");
        niveauLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        ComboBox<String> niveauChoice = new ComboBox<>();
        niveauChoice.getItems().addAll("Facile", "Moyen", "Difficile");
        niveauChoice.setValue("Moyen");
        niveauChoice.setMaxWidth(Double.MAX_VALUE);
        niveauChoice.setStyle("-fx-font-size: 14px;");
        
        // Visibilité du niveau IA
        niveauLabel.visibleProperty().bind(gameModeChoice.valueProperty().isEqualTo("Joueur vs IA"));
        niveauChoice.visibleProperty().bind(gameModeChoice.valueProperty().isEqualTo("Joueur vs IA"));

        // Champs pour les noms des joueurs
        TextField player1Field = new TextField();
        player1Field.setPromptText("Nom du Joueur 1");
        player1Field.setMaxWidth(Double.MAX_VALUE);
        player1Field.setStyle("-fx-font-size: 14px;");

        TextField player2Field = new TextField();
        player2Field.setPromptText("Nom du Joueur 2");
        player2Field.setMaxWidth(Double.MAX_VALUE);
        player2Field.setStyle("-fx-font-size: 14px;");
        player2Field.visibleProperty().bind(gameModeChoice.valueProperty().isEqualTo("Joueur vs Joueur"));

        // Bouton de démarrage
        Button startButton = new Button("COMMENCER LA PARTIE");
        startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                           "-fx-font-size: 18px; -fx-font-weight: bold; " +
                           "-fx-background-radius: 5; -fx-cursor: hand;");
        startButton.setMaxWidth(Double.MAX_VALUE);
        
        startButton.setOnMouseEntered(e -> startButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; " +
                                                               "-fx-font-size: 18px; -fx-font-weight: bold; " +
                                                               "-fx-background-radius: 5; -fx-cursor: hand;"));
        startButton.setOnMouseExited(e -> startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                                                              "-fx-font-size: 18px; -fx-font-weight: bold; " +
                                                              "-fx-background-radius: 5; -fx-cursor: hand;"));

        startButton.setOnAction(e -> {
            if (validateInputs(player1Field, player2Field, gameModeChoice)) {
                VarianteJeu variante = VarianteJeu.valueOf(varianteChoice.getValue().toUpperCase());
                String player2Name = gameModeChoice.getValue().equals("Joueur vs IA") ? "IA" : player2Field.getText().trim();
                NiveauIA niveau = null;
                if (gameModeChoice.getValue().equals("Joueur vs IA")) {
                    niveau = NiveauIA.valueOf(niveauChoice.getValue().toUpperCase());
                }
                setupGame(variante, player1Field.getText().trim(), player2Name, niveau);
                showGameScreen();
            }
        });

        // Organisation des éléments
        gameOptionsBox.getChildren().addAll(
            varianteLabel, varianteChoice,
            modeLabel, gameModeChoice,
            niveauLabel, niveauChoice,
            new Separator(),
            player1Field,
            player2Field,
            new Separator(),
            startButton
        );

        welcomeLayout.getChildren().addAll(titleLabel, subtitleLabel, gameOptionsBox);

        Scene welcomeScene = new Scene(welcomeLayout, 800, 600);
        primaryStage.setTitle("Schotten-Totten");
        primaryStage.setScene(welcomeScene);
        primaryStage.show();
    }

    private boolean validateInputs(TextField player1Field, TextField player2Field, ComboBox<String> gameModeChoice) {
        String player1Name = player1Field.getText().trim();
        String player2Name = player2Field.getText().trim();

        if (player1Name.isEmpty()) {
            showError("Erreur", "Le nom du Joueur 1 est requis!");
            return false;
        }

        if (gameModeChoice.getValue().equals("Joueur vs Joueur") && player2Name.isEmpty()) {
            showError("Erreur", "Le nom du Joueur 2 est requis pour le mode Joueur vs Joueur!");
            return false;
        }

        return true;
    }

    private void setupGame(VarianteJeu variante, String joueur1, String joueur2, NiveauIA niveau) {
        this.jeu = JeuFactory.creerJeu(variante, joueur1, joueur2, niveau);
    }

    private void showGameScreen() {
        mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #2c5530;");

        HBox topBar = createTopBar();
        
        bornesArea = new HBox(10);
        bornesArea.setAlignment(Pos.CENTER);
        setupBornes();

        playerHand = new HBox(10);
        playerHand.setAlignment(Pos.CENTER);
        updatePlayerHand();

        ScrollPane bornesScroll = new ScrollPane(bornesArea);
        bornesScroll.setStyle("-fx-background: #2c5530; -fx-border-color: #2c5530;");
        bornesScroll.setFitToWidth(true);

        ScrollPane handScroll = new ScrollPane(playerHand);
        handScroll.setStyle("-fx-background: #2c5530; -fx-border-color: #2c5530;");
        handScroll.setFitToWidth(true);

        mainLayout.getChildren().addAll(topBar, bornesScroll, handScroll);

        Scene gameScene = new Scene(mainLayout, 1200, 800);
        primaryStage.setScene(gameScene);
        isGameStarted = true;
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: rgba(0,0,0,0.3); -fx-background-radius: 5;");

        statusLabel = new Label("Tour de " + jeu.getJoueurCourant().getNom());
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        Button surrenderButton = new Button("Abandonner");
        surrenderButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
        surrenderButton.setOnAction(e -> handleSurrender());

        Button rulesButton = new Button("Règles");
        rulesButton.setStyle("-fx-background-color: #4444ff; -fx-text-fill: white;");
        rulesButton.setOnAction(e -> showRules());

        topBar.getChildren().addAll(statusLabel, rulesButton, surrenderButton);
        return topBar;
    }

    private void setupBornes() {
        bornesArea.getChildren().clear();
        for (Borne borne : jeu.getBornes()) {
            VBox borneBox = createBorneBox(borne);
            bornesArea.getChildren().add(borneBox);
        }
    }

    private VBox createBorneBox(Borne borne) {
        VBox borneBox = new VBox(5);
        borneBox.setAlignment(Pos.CENTER);
        borneBox.setPadding(new Insets(10));
        borneBox.setMinWidth(120);
        
        // Changer la couleur de fond si la borne a un propriétaire
        String backgroundColor = borne.getProprietaire() != null ? 
            (borne.getProprietaire() == jeu.getJoueur1() ? "rgba(0,255,0,0.2)" : "rgba(255,0,0,0.2)") :
            "rgba(255,255,255,0.1)";
        
        borneBox.setStyle("-fx-background-color: " + backgroundColor + "; " +
                        "-fx-border-color: #1a331d; -fx-border-width: 2; " +
                        "-fx-border-radius: 5; -fx-background-radius: 5;");

        VBox player2Cards = new VBox(2);
        player2Cards.setAlignment(Pos.CENTER);
        for (Carte carte : borne.getCartesJoueur2()) {
            player2Cards.getChildren().add(createCardView(carte, false));
        }

        Label borneLabel = new Label("Borne " + borne.getNumero());
        borneLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        
        if (borne.getProprietaire() != null) {
            borneLabel.setText(borneLabel.getText() + "\nGagnée par " + borne.getProprietaire().getNom());
            borneLabel.setStyle(borneLabel.getStyle() + "; -fx-font-weight: bold;");
            
            // Ajouter une animation de victoire
            FadeTransition ft = new FadeTransition(Duration.millis(1000), borneBox);
            ft.setFromValue(0.5);
            ft.setToValue(1.0);
            ft.setCycleCount(2);
            ft.setAutoReverse(true);
            ft.play();
        }

        VBox player1Cards = new VBox(2);
        player1Cards.setAlignment(Pos.CENTER);
        for (Carte carte : borne.getCartesJoueur1()) {
            player1Cards.getChildren().add(createCardView(carte, true));
        }

        borneBox.getChildren().addAll(player2Cards, borneLabel, player1Cards);
        return borneBox;
    }

    private void checkBornesGagnees() {
        for (Borne borne : jeu.getBornes()) {
            if (borne.getProprietaire() != null) {
                if (borne.getCartesJoueur1().size() == 3 && 
                    borne.getCartesJoueur2().size() == 3 && 
                    !borneDejaAnnoncee(borne)) {
                    
                    Platform.runLater(() -> {
                        showBorneGagneeDialog(borne);
                        marquerBorneCommeAnnoncee(borne);
                    });
                }
            }
        }
    }


    private void updateGameState() {
        setupBornes();
        updatePlayerHand();
        statusLabel.setText("Tour de " + jeu.getJoueurCourant().getNom());

        // Vérifier les bornes gagnées
        checkBornesGagnees();

        if (jeu.estPartieTerminee()) {
            showGameOverDialog(null);
            return;
        }

        // Si c'est le tour de l'IA, utiliser un Thread séparé
        if (!jeu.getJoueurCourant().estHumain()) {
            Thread iaThread = new Thread(() -> {
                try {
                    Thread.sleep(500); // Délai réduit à 500ms
                    Platform.runLater(() -> {
                        jeu.jouerTourIA();
                        updateGameState();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            iaThread.start();
        }
    }


    // Ajouter ces nouvelles méthodes
    private Set<Integer> bornesAnnonceesSet = new HashSet<>();

    private boolean borneDejaAnnoncee(Borne borne) {
        return bornesAnnonceesSet.contains(borne.getNumero());
    }

    private void marquerBorneCommeAnnoncee(Borne borne) {
        bornesAnnonceesSet.add(borne.getNumero());
    }

    private void showBorneGagneeDialog(Borne borne) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Borne gagnée !");
        alert.setHeaderText(null);
        alert.setContentText("La borne " + borne.getNumero() + " a été gagnée par " + 
                            borne.getProprietaire().getNom() + " !");
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2c5530;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: white;");
        dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #1a331d;");
        
        alert.showAndWait();
    }

    private StackPane createCardView(Carte carte, boolean isPlayer1) {
        StackPane cardView = new StackPane();
        cardView.setPrefSize(100, 140);
        cardView.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 0);");

        VBox cardContent = new VBox(5);
        cardContent.setAlignment(Pos.CENTER);

        Label numberLabel = new Label(String.valueOf(carte.getValeur()));
        numberLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: " + getColorForCard(carte.getCouleur()) + ";");

        Region colorBox = new Region();
        colorBox.setPrefSize(60, 60);
        colorBox.setStyle("-fx-background-color: " + getColorForCard(carte.getCouleur()) + ";" +
                         "-fx-opacity: 0.2; -fx-background-radius: 5;");

        cardContent.getChildren().addAll(numberLabel, colorBox);
        cardView.getChildren().add(cardContent);

        return cardView;
    }

    private void updatePlayerHand() {
        playerHand.getChildren().clear();
        for (Carte carte : jeu.getJoueurCourant().getMain()) {
            StackPane cardView = createCardView(carte, true);
            cardView.setOnMouseClicked(e -> handleCardClick(carte));
            cardView.setOnMouseEntered(e -> 
                cardView.setStyle(cardView.getStyle() + "-fx-scale-x: 1.1; -fx-scale-y: 1.1;"));
            cardView.setOnMouseExited(e -> 
                cardView.setStyle(cardView.getStyle() + "-fx-scale-x: 1; -fx-scale-y: 1;"));
            playerHand.getChildren().add(cardView);
        }
    }

    private void handleCardClick(Carte carte) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Jouer une carte");
        dialog.setHeaderText("Sur quelle borne voulez-vous jouer cette carte ?");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2c5530;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: white;");
        dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #1a331d;");
        dialogPane.lookup(".header-panel .label").setStyle("-fx-text-fill: white;");

        ComboBox<Integer> borneChoice = new ComboBox<>();
        List<Borne> bornesList = jeu.getBornes();
        for (int i = 1; i <= 9; i++) {
            Borne borne = bornesList.get(i-1);
            if (!borne.estComplete() && borne.getProprietaire() == null) {
                borneChoice.getItems().add(i);
            }
        }

        if (borneChoice.getItems().isEmpty()) {
            showError("Aucune borne disponible", "Toutes les bornes sont complètes ou revendiquées.");
            return;
        }

        borneChoice.setValue(borneChoice.getItems().get(0));
        borneChoice.setStyle("-fx-font-size: 14px;");

        ButtonType playButtonType = new ButtonType("Jouer", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(playButtonType, cancelButtonType);

        Button playButton = (Button) dialog.getDialogPane().lookupButton(playButtonType);
        playButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        VBox cardInfo = new VBox(10);
        cardInfo.setAlignment(Pos.CENTER);
        Label cardLabel = new Label(String.format("Carte : %d %s", carte.getValeur(), carte.getCouleur()));
        cardLabel.setStyle("-fx-text-fill: white;");
        cardInfo.getChildren().addAll(cardLabel, borneChoice);

        dialogPane.setContent(cardInfo);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == playButtonType) {
                return borneChoice.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(borneNum -> {
            int cardIndex = jeu.getJoueurCourant().getMain().indexOf(carte);
            if (jeu.jouerCarte(cardIndex, borneNum)) {
                updateGameState();
            } else {
                showError("Coup invalide", "Impossible de jouer cette carte sur cette borne.");
            }
        });
    }

    
    private void handleSurrender() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Abandonner la partie");
        alert.setHeaderText("Êtes-vous sûr de vouloir abandonner ?");
        alert.setContentText("Cette action mettra fin à la partie.");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2c5530;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: white;");
        dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #1a331d;");
        dialogPane.lookup(".header-panel .label").setStyle("-fx-text-fill: white;");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showGameOverDialog(jeu.getJoueurCourant() == jeu.getJoueur1() ? jeu.getJoueur2() : jeu.getJoueur1());
            }
        });
    }

    private void showGameOverDialog(Joueur gagnant) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fin de la partie");
        alert.setHeaderText("La partie est terminée!");
        
        if (gagnant == null) {
            gagnant = jeu.determinerGagnant();
        }
        
        if (gagnant != null) {
            alert.setContentText("Le gagnant est : " + gagnant.getNom());
        } else {
            alert.setContentText("Match nul!");
        }

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2c5530;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: white;");
        dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #1a331d;");
        dialogPane.lookup(".header-panel .label").setStyle("-fx-text-fill: white;");

        ButtonType newGameButton = new ButtonType("Nouvelle Partie");
        ButtonType quitButton = new ButtonType("Quitter", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(newGameButton, quitButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == newGameButton) {
                showWelcomeScreen();
            } else {
                primaryStage.close();
            }
        });
    }

    private void showRules() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Règles du jeu");
        alert.setHeaderText("Comment jouer à Schotten-Totten");
        
        String rules = "Schotten-Totten est un jeu de cartes stratégique pour 2 joueurs.\n\n" +
                      "Règles de base :\n" +
                      "1. Chaque joueur pose des cartes devant les bornes\n" +
                      "2. Pour revendiquer une borne, il faut avoir la meilleure combinaison\n" +
                      "3. Les combinaisons (de la plus forte à la plus faible) :\n" +
                      "   - Suite colorée\n" +
                      "   - Brelan\n" +
                      "   - Suite\n" +
                      "   - Couleur\n" +
                      "   - Somme des valeurs\n\n" +
                      "Pour gagner :\n" +
                      "- Revendiquez 5 bornes\n" +
                      "- Ou la majorité des bornes une fois toutes revendiquées";

        alert.setContentText(rules);
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2c5530;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #1a331d;");
        dialogPane.lookup(".header-panel .label").setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #2c5530;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: white;");
        dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #1a331d;");

        alert.showAndWait();
    }

    private String getColorForCard(String couleur) {
        switch (couleur) {
            case "Rouge": return "#ff0000";
            case "Bleu": return "#0000ff";
            case "Vert": return "#00ff00";
            case "Orange": return "#ffa500";
            case "Violet": return "#800080";
            case "Jaune": return "#ffff00";
            default: return "#000000";
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}