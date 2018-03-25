package Test;

import static org.junit.jupiter.api.Assertions.*;
import go.Model.Game;
import go.Model.Player;
import org.junit.jupiter.api.Test;
import javafx.scene.paint.Color;

class GameTest {

    @Test
    void testTurnSwitch() {
        Game game = new Game();

        Player[] players = game.getPlayers();

        assertEquals(players[0].getColor(), Color.BLACK);
        assertEquals(players[1].getColor(), Color.WHITE);

        assertEquals(game.getCurrentPlayer(), players[0]);
        game.nextTurn();
        assertEquals(game.getCurrentPlayer(), players[1]);
        game.nextTurn();
        assertEquals(game.getCurrentPlayer(), players[0]);
        game.nextTurn();
        assertEquals(game.getCurrentPlayer(), players[1]);
    }

    @Test
    void testKO() {
        Game game = new Game();

        game.playerMove(8, 6);
        game.nextTurn();
        game.playerMove(7, 7);
        game.nextTurn();
        game.playerMove(8, 8);
        game.nextTurn();
        game.nextTurn();
        game.playerMove(7, 8);
        game.nextTurn();
        assertEquals(true, game.isValidMove(8, 7));
        game.playerMove(8, 7);
        game.nextTurn();
        assertEquals(1, game.getCurrentPlayer().getNumStonesCaptured());
        game.nextTurn();
        assertEquals(false, game.isValidMove(8, 8)); // ko rule
    }

    @Test
    void testRepeatMove() {                 // test ko rule
        Game game = new Game();

        game.playerMove(3, 3);
        game.nextTurn();
        game.playerMove(4, 2);
        game.nextTurn();
        game.playerMove(5, 3);
        game.nextTurn();

        game.nextTurn();
        game.playerMove(3, 4);
        game.nextTurn();
        game.playerMove(4, 5);
        game.nextTurn();
        game.playerMove(5, 4);
        game.nextTurn();

        game.nextTurn();
        game.playerMove(4, 4);
        game.nextTurn();

        game.nextTurn();
        assertEquals(true, game.isValidMove(4, 3)); // capture
        game.playerMove(4, 3);
        game.nextTurn();
        assertEquals(game.getCurrentPlayer().getNumStonesCaptured(), 1);

        game.nextTurn();
        assertEquals(false, game.isValidMove( 4, 4)); // ko rule
    }

}