package sample.Model;

import sample.Model.Utility.Pair;
import java.util.List;
import java.util.Random;

public class AI {

    public static Pair<Integer, Integer> chooseRandomMove(Game game) {
        List<Pair<Integer, Integer>> validMoves = game.validMoves();

        if(validMoves.size() == 0)
            return null;
        else
            return validMoves.get( (new Random()).nextInt(validMoves.size()) ) ;
    }

    public static void makeMove(Game game) {
        Pair<Integer, Integer> randMove = chooseRandomMove(game);

        if(randMove == null)
            game.passTurn();
        else
            game.playerMove( randMove.getKey(), randMove.getValue() );
    }

}
