package go.Model;

import go.Model.Utility.Pair;
import javafx.scene.paint.Color;
import java.util.List;
import java.util.Random;

public class BoardState {

    private Color player;
    private Board board;
    private int numOfWins;
    private int numOfPlayouts;

    public BoardState(){

        board = new Board(9);
    }
    public BoardState(Board board, Color player){
        this.board = new Board(board);
        this.player = player;
    }
    public BoardState(BoardState boardState){
        this.player = boardState.player;
        this.board = new Board(boardState.board);
        this.numOfWins = boardState.numOfWins;
        this.numOfPlayouts = boardState.numOfPlayouts;
    }
    public Board getBoard() {
        return board;
    }
    public int getNumOfPlayouts() {
        return numOfPlayouts;
    }
    public int getNumOfWins() {
        return numOfWins;
    }
    public void incrementNumOfWins() {
        this.numOfWins++;
    }
    public Color getNextPlayer() {
       return (this.player == Color.BLACK)? Color.WHITE : Color.BLACK;
    }
    public Color getPlayer(){
        return player;
    }
    public void setPlayer(Color player){
        this.player = player;
    }
    public void incrementNumOfPlayouts(){
        this.numOfPlayouts++;
    }
    public void playRandomMove(){
        List<Pair<Integer, Integer>> validMoves = board.validMoves(player);
        Pair<Integer, Integer> randMove;
        if(validMoves.size() != 0){
            randMove = validMoves.get((new Random()).nextInt(validMoves.size()));
           // System.out.println("randMove: "+randMove.getKey() +", "+ randMove.getValue());
            board.placeStoneOnBoard(randMove.getKey(), randMove.getValue(),player);
        }
    }
}
