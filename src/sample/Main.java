package sample;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.Stage;
import sample.Model.*;
import sample.Model.Utility.*;

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

        gameView = new GameView();
        gameView.getStylesheets().add("sample/stylesheet.css");
        gameView.setPrefSize(WIDTH, HEIGHT);
        gameView.displayHomeScreen();

        primaryStage.setTitle("BetaGo");
        primaryStage.setScene( new Scene(gameView, WIDTH / 0.80, HEIGHT) );
        primaryStage.show();
    }

    class GameView extends StackPane {

        private GamePlayScreen gamePlayScreen;
        private HomeScreen homeScreen;

        GameView() {
            gamePlayScreen = new GamePlayScreen();
            homeScreen = new HomeScreen();
        }

        public void displayHomeScreen() {
            displayScreen(homeScreen);
        }

        public void displayGamePlayScreen() {
            displayScreen(gamePlayScreen);
        }

        public void updateGamePlayScreen() {
            gamePlayScreen.update();
        }

        public void displayScreen(Node screen) {
            this.getChildren().removeAll(this.getChildren());
            this.getChildren().add(screen);
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
            boardView.drawBoardState();
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
                    drawBoardState();                                           // draw board on top of previously drawn valid move
                                                                                // draw valid move on top of board
                    if(game.getCurrentPlayer().getColor() == Color.WHITE)
                        drawCircle(row, col, new Color(1, 1, 1, 0.5));
                    else
                        drawCircle(row, col, new Color(0, 0, 0, 0.5));
                }
            });

            setOnMouseExited(event -> drawBoardState());

            drawBackground();
        }

        private void attemptToPlaceStone(int row, int col) {
            if(game.isValidMove(row, col)) {
                game.playerMove(row, col);
                game.nextTurn();

                gameView.updateGamePlayScreen();
            }
        }

        private void drawBoardState() {
            drawBackground();

            Stone[][] stones = game.getBoard().getBoard();
            for (int i = 0; i < stones.length; i++)
                for (int j = 0; j < stones.length; j++)
                    if(stones[i][j] != null)
                        drawCircle(i, j, stones[i][j].getColor());
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

            getChildren().addAll(label, passTurnBtn, exitBtn, newGameBtn, homeScreenBtn);
        }

        private void updateLabel() {
            label.setText(game.getCurrentPlayer().getName() + "'s turn\nP1: " + game.getPlayers()[0].numStonesCaptured() + "\nP2: " + game.getPlayers()[1].numStonesCaptured());
         }
    }

    class HomeScreen extends VBox {

        private Label label;
        private Button newGameBtn;
        private Button exitBtn;
        private VBox newGameOptions;

        HumanVsHumanScreen humanVsHumanScreen;
        HumanVsComputerScreen humanVsComputerScreen;

        HomeScreen() {
            getStyleClass().add("homeScreen");

            label = new Label("BetaGo");
            label.getStyleClass().add("homeScreenLabel");

            newGameBtn = new Button("New Game");
            newGameBtn.setOnAction(e -> gameView.displayGamePlayScreen());

            exitBtn = new Button("Quit");
            exitBtn.setOnAction(e -> System.exit(0));

            humanVsHumanScreen = new HumanVsHumanScreen();
            humanVsComputerScreen = new HumanVsComputerScreen();

            Button vsHumanBtn = new Button("Human vs Human");
            vsHumanBtn.setOnAction(e -> gameView.displayScreen(humanVsHumanScreen));
            vsHumanBtn.setMinWidth(WIDTH * .5);

            Button vsComputerBtn = new Button("Human vs Computer");
            vsComputerBtn.setOnAction(e -> gameView.displayScreen(humanVsComputerScreen));
            vsComputerBtn.setMinWidth(WIDTH * .5);

            vsHumanBtn.setStyle("-fx-border-color: black;");            // TODO: use .css file
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
            playBtn.setOnAction(e -> gameView.displayGamePlayScreen());

            Button backBtn = new Button("Back");
            backBtn.setOnAction(e -> gameView.displayHomeScreen());

            playBtn.setMinWidth(WIDTH * .5);
            backBtn.setMinWidth(WIDTH * .5);

            getChildren().addAll(backBtn, playBtn);
        }
    }

    class BoardSizeButtons extends HBox {
        BoardSizeButtons() {
            getStyleClass().add("playBackButtons");

            int[] boardSizes = new int[]{9, 13, 19};
            for (Integer i : boardSizes) {
                Button button = new Button(String.valueOf(i));

                final int boardSize = i;
                button.setOnAction((ActionEvent e) -> {
                    game.setBoardSize(boardSize);
                    gameView.updateGamePlayScreen();
                });

                button.getStyleClass().add("boardSizeButtons");
                button.setPrefWidth(WIDTH / 5);
                button.setMinWidth(WIDTH / 5);

                getChildren().add(button);
            }
        }
    }

    class HumanVsHumanScreen extends VBox {
        HumanVsHumanScreen() {
            getStyleClass().add("humanVsHumanScreen");

            PlayAndBackButtons playAndBackBtns = new PlayAndBackButtons();
            BoardSizeButtons boardSizeBtns = new BoardSizeButtons();

            GridPane nameSelectGrid = new GridPane();
            nameSelectGrid.add( new Label("Players"), 0, 0, 2, 1);

            int imageWidth = WIDTH / 5;
            ImageView whiteImageView = new ImageView( new Image(Main.class.getResourceAsStream("../images/white.png"), imageWidth, imageWidth, true, true) );
            ImageView blackImageView = new ImageView( new Image(Main.class.getResourceAsStream("../images/black.png"), imageWidth, imageWidth, true, true) );

            TextField playerOneName = new TextField("Player 1");
            TextField playerTwoName = new TextField("Player 2");

            nameSelectGrid.add(blackImageView, 0, 1);
            nameSelectGrid.add(playerOneName,  1, 1);
            nameSelectGrid.add(whiteImageView, 0, 2);
            nameSelectGrid.add(playerTwoName,  1, 2);

            nameSelectGrid.setPrefWidth(WIDTH / 5);
            nameSelectGrid.setMinWidth(WIDTH  / 5);
            nameSelectGrid.setStyle("-fx-alignment: center;");

            Label boardSizeLabel = new Label("Board size");
            getChildren().addAll(boardSizeLabel, boardSizeBtns, nameSelectGrid, playAndBackBtns);
        }
    }

    class HumanVsComputerScreen extends VBox {
        HumanVsComputerScreen() {
            getStyleClass().add("humanVsHumanScreen");

            PlayAndBackButtons playAndBackBtns = new PlayAndBackButtons();
            BoardSizeButtons boardSizeBtns = new BoardSizeButtons();

            Button easyBtn = new Button("Easy");
            easyBtn.getStyleClass().add("boardSizeButtons");
            easyBtn.setMinWidth(WIDTH / 5);

            Button hardBtn = new Button("Hard");
            hardBtn.getStyleClass().add("boardSizeButtons");
            hardBtn.setMinWidth(WIDTH / 5);
            
            HBox difficultyBtns = new HBox();
            difficultyBtns.setStyle("-fx-alignment: center;");
            difficultyBtns.setSpacing(20.0);
            difficultyBtns.getChildren().addAll(easyBtn, hardBtn);
            
            Label difficultyLabel = new Label("Difficulty");
            Label boardSizeLabel = new Label("Board size");
            getChildren().addAll(boardSizeLabel, boardSizeBtns, difficultyLabel, difficultyBtns, playAndBackBtns);
        }
    }

}