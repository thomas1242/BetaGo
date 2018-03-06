package sample;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Model.*;
import sample.Model.Utility.Pair;

public class Main extends Application {

    private static int HEIGHT = (int) (java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 1.20);
    private static int WIDTH = HEIGHT;

    private Game game;
    private GameView gameView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        game = new Game();

        gameView = new GameView(primaryStage);
        gameView.getStylesheets().add("sample/stylesheet.css");
        gameView.setPrefSize(WIDTH, HEIGHT);
        gameView.displayHomeScreen();

        primaryStage.setTitle("BetaGo");
        primaryStage.setScene( new Scene(gameView, WIDTH / 0.80, HEIGHT) );
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    class GameView extends StackPane {

        private HomeScreen homeScreen;
        private GamePlayScreen gamePlayScreen;
        private GameOverPopUp gameOverPopUp;

        GameView(Stage primaryStage) {
            homeScreen = new HomeScreen();
            gamePlayScreen = new GamePlayScreen();
            gameOverPopUp = new GameOverPopUp(primaryStage);
        }

        public void displayHomeScreen() {
            displayScreen(homeScreen);
        }

        public void displayGamePlayScreen() {
            updateGamePlayScreen();
            displayScreen(gamePlayScreen);
        }

        public void updateGamePlayScreen() {
            gamePlayScreen.update();
            if(game.isGameOver())
                gameView.displayGameOverPopup();
        }

        public void displayScreen(Node screen) {
            gameOverPopUp.hide();
            this.getChildren().removeAll(this.getChildren());
            this.getChildren().add(screen);
        }

        public void displayGameOverPopup() {
            gameOverPopUp.display();
        }
    }

    class GamePlayScreen extends GridPane {

        private BoardView boardView;
        private SidePanel sidePanel;

        GamePlayScreen() {
            boardView = new BoardView(WIDTH, HEIGHT);
            sidePanel = new SidePanel();

            ColumnConstraints col1 = new ColumnConstraints();
            col1.setPercentWidth(80);
            ColumnConstraints col2 = new ColumnConstraints();
            col2.setPercentWidth(20);
            getColumnConstraints().addAll(col1, col2);

            add(boardView, 0, 0);
            add(sidePanel, 1, 0);
        }

        public void update() {
            boardView.update();
            sidePanel.updateLabel();
        }

    }

    class BoardView extends Canvas {
        
        private GraphicsContext gc;

        BoardView(int width, int height) {
            super(width, height);
            getStyleClass().add("boardView");
            gc = getGraphicsContext2D();

            setOnMouseClicked(event -> {
                Pair<Integer, Integer> position = pixelToBoardCoordinate(event.getX(), event.getY());
                int row = position.getValue(), col = position.getKey();

                attemptToPlaceStone(row, col);
            });

            setOnMouseMoved(event -> {
                Pair<Integer, Integer> position = pixelToBoardCoordinate(event.getX(), event.getY());
                int row = position.getValue(), col = position.getKey();

                if(game.isValidMove(row, col)) {
                    update();                                           // draw board on top of previously drawn valid move
                                                                                // draw valid move on top of board
                    if(game.getCurrentPlayer().getColor() == Color.WHITE)
                        drawCircle(row, col, new Color(1, 1, 1, 0.5));
                    else
                        drawCircle(row, col, new Color(0, 0, 0, 0.5));
                }

            });

            setOnMouseExited(event -> update());

            drawBackground();
        }

        private void attemptToPlaceStone(int row, int col) {
            if(game.isValidMove(row, col))
                game.playerMove(row, col);

            if(game.isGameOver())
                gameView.displayGameOverPopup();
            else
                gameView.updateGamePlayScreen();
        }

        private void update() {
            drawBackground();

            Point[][] points = game.getBoard().getPoints();

            for (int i = 0; i < points.length; i++)
                for (int j = 0; j < points.length; j++)
                    if(points[i][j].getStone() != null)
                        drawCircle(i, j, points[i][j].getStone().getColor());
        }

        private void drawCircle(double row, double col, Paint p) {
            int xOffset = (int)getWidth()  / game.getBoardSize();
            int yOffset = (int)getHeight() / game.getBoardSize();

            gc.setFill(p);
            gc.fillOval(col * xOffset, row * yOffset, xOffset, yOffset);
        }

        private Pair<Integer, Integer> pixelToBoardCoordinate(double x , double y) {
            x = (x / (getWidth()  / game.getBoardSize()));
            y = (y / (getHeight() / game.getBoardSize()));
            return new Pair<>((int)x, (int)y);
        }

        private void drawGridLines(int size) {
            int xOffset = (int)(getWidth()  / 1.0 / size);
            int yOffset = (int)(getHeight() / 1.0 / size);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1.5);

            for (int i = 0; i < size; i++) {
                gc.strokeLine(i * xOffset + xOffset / 2,  yOffset / 2, i * xOffset + xOffset / 2, game.getBoardSize() * yOffset - yOffset / 2 - 1);
                gc.strokeLine(xOffset / 2, i * yOffset + yOffset / 2, game.getBoardSize() * xOffset - xOffset / 2 - 1, i * yOffset + yOffset / 2);
            }
        }

        final Image woodImg = new Image(Main.class.getResourceAsStream("../images/wood1.jpg"));
        private void drawBackground() {
            gc.drawImage(woodImg, 0, 0, WIDTH, HEIGHT);
            drawGridLines(game.getBoardSize());
        }
    }

    class SidePanel extends VBox {
        private Label label;
        int imageWidth = (int)(WIDTH * 0.20);

        private ImageView whiteImageView = new ImageView( new Image(Main.class.getResourceAsStream("../images/white.png"), imageWidth, imageWidth, true, true) );
        private ImageView blackImageView = new ImageView( new Image(Main.class.getResourceAsStream("../images/black.png"), imageWidth, imageWidth, true, true) );
        private ImageView currPlayerImage = new ImageView(blackImageView.getImage());

        SidePanel() {
            getStyleClass().add("sidePanel");

            Button homeScreenBtn = new Button("Home Screen");
            homeScreenBtn.setOnAction(e -> gameView.displayHomeScreen());

            Button exitBtn = new Button("Quit");
            exitBtn.setOnAction(e -> System.exit(0));

            Button passTurnBtn = new Button("Pass turn");
            passTurnBtn.setOnAction(e -> {
                game.passTurn();
                gameView.updateGamePlayScreen();
            });

            Button newGameBtn = new Button("Restart");
            newGameBtn.setOnAction(e -> {
                game.restartGame();
                gameView.updateGamePlayScreen();
            });

            label = new Label("");
            label.getStyleClass().add("sidePanelLabel");
            label.setPrefWidth(WIDTH * 0.20);
            updateLabel();

            for(Button button : new Button[]{passTurnBtn, newGameBtn, homeScreenBtn, exitBtn}) {
                button.setMinWidth(WIDTH * 0.20);
                button.setMinHeight(HEIGHT * 0.06);
                button.getStyleClass().add("sidePanelButton");
            }

            getChildren().addAll(label, currPlayerImage, passTurnBtn, newGameBtn, homeScreenBtn, exitBtn);
        }

        private void updateLabel() {
            label.setText(game.getCurrentPlayer().getName() + "'s turn\n" +
                    game.getPlayers()[0].getName() + ": " + game.getPlayers()[0].getNumStonesCaptured() + "\n" + game.getPlayers()[1].getName() + ": " + game.getPlayers()[1].getNumStonesCaptured());

            if (game.getCurrentPlayer().getColor() == Color.BLACK)
                currPlayerImage.setImage(blackImageView.getImage());
            else
                currPlayerImage.setImage(whiteImageView.getImage());
        }
    }

    class HomeScreen extends VBox {

        private Label label;
        private Button newGameBtn;
        private Button exitBtn;
        private VBox newGameOptions;

        private HumanVsHumanScreen humanVsHumanScreen;
        private HumanVsComputerScreen humanVsComputerScreen;

        HomeScreen() {
            getStyleClass().add("homeScreen");

            label = new Label("BetaGo");
            label.getStyleClass().add("homeScreenLabel");

            newGameBtn = new Button("New Game");
            newGameBtn.setOnAction(e -> {
                gameView.displayGamePlayScreen();
            });

            exitBtn = new Button("Quit");
            exitBtn.setOnAction(e -> System.exit(0));

            humanVsHumanScreen = new HumanVsHumanScreen();
            humanVsComputerScreen = new HumanVsComputerScreen();

            Button vsHumanBtn = new Button("Human vs Human");
            vsHumanBtn.setOnAction(e -> {
                game = new Game();
                gameView.displayScreen(humanVsHumanScreen);
            });
            vsHumanBtn.setMinWidth(WIDTH * .5);

            Button vsComputerBtn = new Button("Human vs Computer");
            vsComputerBtn.setOnAction(e -> {
                game = new Game();
                game.getPlayers()[1].enableAI();
                gameView.displayScreen(humanVsComputerScreen);
            });
            vsComputerBtn.setMinWidth(WIDTH * .5);

            vsHumanBtn.setStyle("-fx-border-color: black;");
            vsComputerBtn.setStyle("-fx-border-color: transparent black black black;");

            for(Button button : new Button[]{newGameBtn, exitBtn}) {
                button.getStyleClass().add("homeScreenButton");
                button.setMinWidth(WIDTH * .5);
            }

            newGameOptions = new VBox();
            newGameOptions.setMaxWidth(WIDTH * .5);
            newGameOptions.getStyleClass().add("newGameOptions");
            newGameOptions.getChildren().addAll(vsHumanBtn, vsComputerBtn);

            newGameBtn.setOnMouseEntered(e -> {
                this.getChildren().removeAll(getChildren());
                this.getChildren().add(label);
                this.getChildren().add(newGameOptions);
                this.getChildren().add(exitBtn);
            });

            newGameOptions.setOnMouseExited(e -> {
                this.getChildren().removeAll(getChildren());
                this.getChildren().add(label);
                this.getChildren().add(newGameBtn);
                this.getChildren().add(exitBtn);
            });

            getChildren().addAll(label, newGameBtn, exitBtn);
        }
    }

    class PlayAndBackButtons extends HBox {
        PlayAndBackButtons() {
            getStyleClass().add("playBackButtons");

            Button playBtn = new Button("Play");
            playBtn.setOnAction(e -> {
                game.restartGame();
                gameView.displayGamePlayScreen();
            });

            Button backBtn = new Button("Back");
            backBtn.setOnAction(e -> gameView.displayHomeScreen());

            playBtn.setStyle("-fx-border-color: black;");
            backBtn.setStyle("-fx-border-color: black;");

            playBtn.setMinWidth(WIDTH * .5);
            backBtn.setMinWidth(WIDTH * .5);

            getChildren().addAll(backBtn, playBtn);
        }
    }

    class BoardSizeButtons extends HBox {
        BoardSizeButtons() {
            getStyleClass().add("playBackButtons");

            final int[] boardSizes = new int[]{9, 13, 19};
            Button[] buttons = new Button[3];

            for (int i = 0; i < buttons.length; i++) {
                buttons[i] = new Button(String.valueOf(boardSizes[i]));
                buttons[i].getStyleClass().add("boardSizeButtons");
                buttons[i].setPrefWidth(WIDTH / 5);
                buttons[i].setMinWidth(WIDTH / 5);

                int finalI = i;
                buttons[i].setOnAction((ActionEvent e) -> {
                    for(Button btn : buttons) btn.setStyle("-fx-base: #666666;");
                    buttons[finalI].setStyle("-fx-base: #00802b;");

                    game.setBoardSize(boardSizes[finalI]);
                    gameView.updateGamePlayScreen();
                });

                getChildren().add(buttons[i]);
            }

            buttons[0].setStyle("-fx-base: #00802b;");
        }
    }

    class HumanVsHumanScreen extends VBox {
        HumanVsHumanScreen() {
            getStyleClass().add("humanVsHumanScreen");

            PlayAndBackButtons playAndBackBtns = new PlayAndBackButtons();
            BoardSizeButtons boardSizeBtns = new BoardSizeButtons();

            TextField playerOneName = new TextField("Player 1");
            TextField playerTwoName = new TextField("Player 2");

            playerOneName.setOnKeyReleased(event -> game.setPlayerOneName(playerOneName.getText()));
            playerTwoName.setOnKeyReleased(event -> game.setPlayerTwoName(playerTwoName.getText()));

            GridPane nameSelectGrid = new GridPane();
            nameSelectGrid.add( new Label("Players"), 0, 0, 2, 1);

            int imageWidth = WIDTH / 5;
            ImageView whiteImageView = new ImageView( new Image(Main.class.getResourceAsStream("../images/white.png"), imageWidth, imageWidth, true, true) );
            ImageView blackImageView = new ImageView( new Image(Main.class.getResourceAsStream("../images/black.png"), imageWidth, imageWidth, true, true) );

            nameSelectGrid.add(blackImageView, 0, 1);
            nameSelectGrid.add(playerOneName,  1, 1);
            nameSelectGrid.add(whiteImageView, 0, 2);
            nameSelectGrid.add(playerTwoName,  1, 2);

            nameSelectGrid.setPrefWidth(WIDTH / 5);
            nameSelectGrid.setMinWidth(WIDTH  / 5);
            nameSelectGrid.setHgap(20);
            nameSelectGrid.setStyle("-fx-alignment: center;" );

            Label boardSizeLabel = new Label("Board size");
            getChildren().addAll(boardSizeLabel, boardSizeBtns, nameSelectGrid, playAndBackBtns);
        }
    }

    class HumanVsComputerScreen extends VBox {
        HumanVsComputerScreen() {
            getStyleClass().add("humanVsHumanScreen");

            PlayAndBackButtons playAndBackBtns = new PlayAndBackButtons();
            BoardSizeButtons boardSizeBtns = new BoardSizeButtons();

            Button hardBtn = new Button("Hard");
            Button easyBtn = new Button("Easy");

            easyBtn.getStyleClass().add("boardSizeButtons");
            easyBtn.setMinWidth(WIDTH / 5);
            easyBtn.setOnAction(event -> {
                hardBtn.setStyle("fx-base: #666666;");
                easyBtn.setStyle("-fx-base: #00802b;");
            });

            hardBtn.getStyleClass().add("boardSizeButtons");
            hardBtn.setMinWidth(WIDTH / 5);
            hardBtn.setOnAction(event -> {
                easyBtn.setStyle("fx-base: #666666;");
                hardBtn.setStyle("-fx-base: #00802b;");
            });
            easyBtn.setStyle("-fx-base: #00802b;");
            
            HBox difficultyBtns = new HBox();
            difficultyBtns.setStyle("-fx-alignment: center;");
            difficultyBtns.setSpacing(20.0);
            difficultyBtns.getChildren().addAll(easyBtn, hardBtn);

            int imageWidth = WIDTH / 4;
            ImageView whiteImageView = new ImageView( new Image(Main.class.getResourceAsStream("../images/white.png"), imageWidth, imageWidth, true, true) );
            ImageView blackImageView = new ImageView( new Image(Main.class.getResourceAsStream("../images/black.png"), imageWidth, imageWidth, true, true) );
            whiteImageView.setOpacity(0.5);

            whiteImageView.setOnMouseClicked(event -> {
                game.getPlayers()[0].enableAI();
                game.getPlayers()[1].disableAI();

                whiteImageView.setOpacity(1.0);
                blackImageView.setOpacity(0.5);
            });
            blackImageView.setOnMouseClicked(event -> {
                game.getPlayers()[0].disableAI();
                game.getPlayers()[1].enableAI();

                whiteImageView.setOpacity(0.5);
                blackImageView.setOpacity(1.0);
            });

            HBox stoneBtns = new HBox();
            stoneBtns.setStyle("-fx-alignment: center;");
            stoneBtns.setSpacing(20.0);
            stoneBtns.getChildren().addAll(blackImageView, whiteImageView);
            
            Label difficultyLabel = new Label("Difficulty");
            Label boardSizeLabel = new Label("Board size");
            getChildren().addAll(boardSizeLabel, boardSizeBtns, difficultyLabel, difficultyBtns, stoneBtns, playAndBackBtns);
        }
    }

    class GameOverPopUp extends Stage {

        private Stage dialog;
        private Label scoreLabel;

        GameOverPopUp(Stage primaryStage) {
            dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage);

            Label gameOverLabel = new Label("GAME OVER");
            gameOverLabel.setStyle("-fx-font-size: 44pt; -fx-border-width: 3; -fx-border-color: transparent transparent black transparent;");

            scoreLabel = new Label("");
            scoreLabel.setStyle("-fx-font-size: 30pt;");

            Button homeScreenBtn = new Button("Home Screen");
            homeScreenBtn.setOnAction(e -> {
                this.hide();
                gameView.displayHomeScreen();
            });

            Button playAgainBtn = new Button("Play again");
            playAgainBtn.setOnAction(e -> {
                this.hide();
                game.restartGame();
            });

            Button quitBtn = new Button("Quit");
            quitBtn.setOnAction(e -> System.exit(0) );

            quitBtn.setStyle("-fx-border-color: black; -fx-font-size: 18pt;");
            homeScreenBtn.setStyle("-fx-border-color: black; -fx-font-size: 18pt;");
            playAgainBtn.setStyle("-fx-border-color: black; -fx-font-size: 18pt;");

            quitBtn.setMinWidth(WIDTH * (1.2 * 3/4) * .20);
            playAgainBtn.setMinWidth(WIDTH * (1.2 * 3/4) * .20);
            homeScreenBtn.setMinWidth(WIDTH * (1.2 * 3/4) * .20);

            HBox hBox = new HBox(20);
            hBox.getChildren().addAll(playAgainBtn, homeScreenBtn, quitBtn);
            hBox.setStyle("-fx-alignment: center;");

            VBox dialogVbox = new VBox(30);
            dialogVbox.getStyleClass().add("endGamePopup");
            dialogVbox.getChildren().addAll(gameOverLabel, scoreLabel, hBox);

            Scene dialogScene = new Scene(dialogVbox, WIDTH * (1.2 * 3/4), HEIGHT* (1.0 * 3/4));
            dialogScene.getStylesheets().add("sample/stylesheet.css");

            dialog.setOnCloseRequest(event -> System.exit(0));
            dialog.setOpacity(.70);
            dialog.setScene(dialogScene);
            dialog.setResizable(false);
        }

        public void display() {
            String winner = game.getPlayers()[0].getNumStonesCaptured() > game.getPlayers()[1].getNumStonesCaptured()
                            ? game.getPlayers()[0].getName() : game.getPlayers()[1].getName();

            scoreLabel.setText(winner + " wins!\n" +
                               game.getPlayers()[0].getName() + ": " + game.getPlayers()[0].getNumStonesCaptured() + "\n" +
                               game.getPlayers()[1].getName() + ": " + game.getPlayers()[1].getNumStonesCaptured());
            dialog.show();
        }

        public void hide() {
            dialog.hide();
        }
    }

}