package go.Model;

import javafx.scene.paint.Color;

import java.util.*;

public class GameNode {
    private GameNode parent;
    private BoardState state;
    private List<GameNode> children;
    private int row;
    private int col;

    public GameNode(Board board, Color player){
        this.state = new BoardState(board, player);
        children = new ArrayList<>();
    }
    public GameNode(GameNode node){
        this.children = new ArrayList<>();
        this.state = new BoardState(node.getBoardState());
        if(node.getParent() != null){
            this.parent = node.getParent();
        }
        List<GameNode> children = node.getChildren();
        for (GameNode child : children){
            this.children.add(new GameNode(child));
        }
    }
    public GameNode (BoardState boardState){
        this.state = boardState;
        children = new ArrayList<>();
    }
    public BoardState getBoardState() {
        return state;
    }
    public List<GameNode> getChildren(){
        return children;
    }
    public GameNode getParent() {
        return parent;
    }
    public void setParent(GameNode parent) {
        this.parent = parent;
    }
    public GameNode randomChild(){
        Random randomNumGen = new Random();
        int index = randomNumGen.nextInt(children.size());
        return children.get(index);
    }
    public void setPosition(int row, int col){
        this.row = row;
        this.col = col;
    }
    public int getRow(){
      return row;
    }
    public int getCol(){
        return col;
    }
    public GameNode getChildWithMostWins(){
        GameNode tempChild = null;
        for(GameNode child : children){
            if(tempChild == null)
                tempChild = child;
            if(tempChild.getBoardState().getNumOfWins() <= child.getBoardState().getNumOfWins())
                tempChild = child;
        }
        //System.out.println("Child with most wins: "+tempChild+": "+ tempChild.getBoardState().getNumOfWins() + "/" +  + tempChild.getBoardState().getNumOfPlayouts());
        return tempChild;
    }
    public void printChildStats(){
        int counter = 0;
        for(GameNode child : children) {
            counter++;
            System.out.println("child " + child.getRow()+", "+ child.getCol()+ " " + child.getBoardState().getNumOfWins() + "/" +  + child.getBoardState().getNumOfPlayouts());
           /* for(GameNode grandchild: child.getChildren()){
                System.out.println("grand child " + grandchild.getRow()+", "+ grandchild.getCol()+ " " + grandchild.getBoardState().getNumOfWins() + "/" +  + grandchild.getBoardState().getNumOfPlayouts());
            }*/
        }
        System.out.println("number of possible states = "+counter);
    }
}
