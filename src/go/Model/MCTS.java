package go.Model;

import go.Model.Utility.Pair;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MCTS {

    private long moveTime;
    private Color currentPlayer;
    private Color nextPlayer;
    private GameNode root;
    public MCTS(long moveTime){
        this.moveTime = moveTime;
    }
    public Board getNextBestMove(Board board, Color player){
        //WARNING: Timing isn't accurate: We should use a timing thread for accurate timing
        long begin = System.currentTimeMillis(); // start timer
        long finish = 1000*moveTime;            // finish time

       if(root != null){
           for(GameNode child : root.getChildren()){
               if(board.findEquivalent(child.getBoardState().getBoard())){
                    root = child; // use existing game tree
                    root.setParent(null);
                   break;
               }
           }
       }else {
           root  = new GameNode(board, player);
           currentPlayer =  player; //store enemy player for easy reference
           nextPlayer = root.getBoardState().getNextPlayer();
           root.getBoardState().setPlayer(nextPlayer);
       }

        int count = 0;//counter for tracking num of simulations
        while( System.currentTimeMillis()-begin <= finish){ // while elapsed time in this method hasn't exceeded moveTime
            count++;
            GameNode node = select(root);
            if(node.getBoardState().getBoard().validMoves(player).size()>0){
                expand(node);
            }
            GameNode exploreNode = node;
            if(node.getChildren().size() >0){
                exploreNode = node.randomChild();
            }
            Color outcome = simulate(exploreNode);
            backPropagate(exploreNode,outcome);
        }
        //System.out.println("simulations: "+count);
        //root.printChildStats();
        root = root.getChildWithMostWins();
        root.setParent(null);
        return root.getBoardState().getBoard();
    }
    private GameNode select(GameNode node){
        while (node.getChildren().size() != 0){
            node = UCT.getUCT(node);
        }
        return node;
    }
    private void expand(GameNode node){
        Color enemy = node.getBoardState().getNextPlayer();
        List<Pair<Integer, Integer>> legalMoves = node.getBoardState().getBoard().validMoves(enemy);
        for (Pair<Integer,Integer> position : legalMoves){
            BoardState newBoardState = new BoardState(node.getBoardState().getBoard(), enemy);
            newBoardState.getBoard().placeStoneOnBoard(position.getKey(), position.getValue(),enemy);
            newBoardState.getBoard().captureStones(enemy);
            GameNode child = new GameNode(newBoardState);
            child.setParent(node);
            child.setPosition(position.getKey(),position.getValue());
            node.getChildren().add(child);
        }
    }
    private Color simulate(GameNode node){
        GameNode newNode = new GameNode(node); // create new game tree with explore node as the root
        BoardState newBoardState = newNode.getBoardState();
        String outcome = newBoardState.getBoard().outcome(newBoardState.getPlayer());
        while(outcome.equals("ONGOING") ){
            newBoardState.setPlayer(newBoardState.getNextPlayer());
            newBoardState.playRandomMove();
            newBoardState.getBoard().captureStones(newBoardState.getPlayer());
            outcome = newBoardState.getBoard().outcome(newBoardState.getPlayer());
        }
        if(outcome.equals("BLACK"))
            return Color.BLACK;
        else
            return Color.WHITE;
    }
    private void backPropagate(GameNode node, Color winner){
        while (node != null){
            node.getBoardState().incrementNumOfPlayouts();
            if(node.getBoardState().getPlayer() == winner && winner == currentPlayer){
                node.getBoardState().incrementNumOfWins();
            }
            node = node.getParent();
        }
    }
}
class UCT{
    private static double uct(int totalPlayouts, int numOfWins, int numOfPlayouts){
        if(numOfPlayouts ==0){
            return Integer.MAX_VALUE;
        }
        return (numOfWins/(double)numOfPlayouts) + 1.41 * Math.sqrt(Math.log(totalPlayouts/(double)numOfPlayouts));
    }
    public static GameNode getUCT(GameNode node){
        int totalPlayouts = node.getBoardState().getNumOfPlayouts();
        return Collections.max(node.getChildren(), Comparator.comparing(child->uct(totalPlayouts,child.getBoardState().getNumOfWins(),child.getBoardState().getNumOfPlayouts())));
    }
}