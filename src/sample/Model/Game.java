package sample.Model;
import javafx.scene.paint.Color;
import sample.Model.Utility.Pair;

public class Game {
    private Player[] players;
    private Board board;
    private int turn;

    private MoveData prevMove;          // ko rule
    private boolean lastTurnPassed;

    public Game() {
        players = new Player[]{
                                 new Player("Player 1", Color.BLACK),
                                 new Player("Player 2", Color.WHITE)
                              };
        setBoardSize(9);
    }

    public void setBoardSize(int size) {
        board = new Board(size);
    }

    public boolean isValidMove(int row, int col) {
        Player currentPlayer = getCurrentPlayer();

        return board.isValidMove(row, col, currentPlayer.getColor()) && !isRepeatBoardPosition(row, col, currentPlayer.getColor());
    }

    private boolean isRepeatBoardPosition(int row, int col, Color color) {
        boolean isRepeatPosition = false;

        board.placeStoneOnBoard(row, col, color);                        // make move
        if (board.countCapturedStones(color) == 1) {
            Pair<Integer, Integer> capturedStone = board.captureSingleStone(color);
            MoveData currMove = new MoveData(capturedStone, 1);
            if (prevMove != null && currMove.equals(prevMove))           // check for ko rule
                isRepeatPosition = true;
        }

        board.removeStoneFromBoard(row, col);                            // undo move
        return isRepeatPosition;
    }

    public void playerMove(int row, int col) {  // precondition: move must be valid
        if (!isValidMove(row, col))
            throw new RuntimeException("Invalid move!");

        Player currentPlayer = getCurrentPlayer();

        board.placeStoneOnBoard(row, col, currentPlayer.getColor());                // make move

        int numStonesCaptured = board.captureStones(currentPlayer.getColor());      // capture enemy stones
        currentPlayer.incrementStonesCaptured(numStonesCaptured);                   // increment score by # stones captured

        prevMove = new MoveData(row, col, numStonesCaptured);
        lastTurnPassed = false;
    }

    public void passTurn() {
        if(lastTurnPassed)
            System.exit(0);      // game over
        nextTurn();
        lastTurnPassed = true;
    }

    public void nextTurn() {
        turn = ++turn % 2;
    }

    @Override
    public String toString() {
        return board.toString() + "SCORES P1: " + players[0].numStonesCaptured() + " P2: " + players[1].numStonesCaptured() + '\n';
    }

    public Player getCurrentPlayer() {
        return players[turn];
    }

    public Player[] getPlayers() {
        return players;
    }

    public Board getBoard() {
        return board;
    }

    public int getBoardSize() {
        return board.size();
    }

    public void restartGame() {
        board.clearBoard();
        for (Player p : players)
            p.resetScore();
        turn = 0;
        lastTurnPassed = false;
        prevMove = null;
    }

}

class MoveData {
    private Pair<Integer, Integer> position;
    private int stonesCaptured;

    MoveData(int x, int y, int stonesCaptured) {
        position = new Pair<>(x, y);
        this.stonesCaptured = stonesCaptured;
    }

    MoveData(Pair<Integer, Integer> pair, int stonesCaptured) {
        this.position = pair;
        this.stonesCaptured = stonesCaptured;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MoveData && (o == this || ((MoveData) o).stonesCaptured == this.stonesCaptured && ((MoveData) o).position.equals(this.position));
    }
}