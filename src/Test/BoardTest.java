package Test;

import static org.junit.jupiter.api.Assertions.*;
import sample.Model.Stone;
import org.junit.jupiter.api.Test;
import sample.Model.Board;
import javafx.scene.paint.Color;
import java.util.HashSet;

class BoardTest {

    @Test
    void testStonePlacement() {
        int size = 9;
        Board board = new Board(size);

        board.placeStoneOnBoard(1, 0, Color.BLACK);
        board.placeStoneOnBoard(0, 1, Color.BLACK);
        board.placeStoneOnBoard(2, 0, Color.WHITE);
        board.placeStoneOnBoard(1, 1, Color.WHITE);
        board.placeStoneOnBoard(0, 2, Color.WHITE);

        assertEquals(1, Stone.getNumLiberties(board.getPoints()[1][0].getStone(), new HashSet<>()));
        assertEquals(1, Stone.getNumLiberties(board.getPoints()[0][1].getStone(), new HashSet<>()));
        assertEquals(2, Stone.getNumLiberties(board.getPoints()[2][0].getStone(), new HashSet<>()));
        assertEquals(2, Stone.getNumLiberties(board.getPoints()[1][1].getStone(), new HashSet<>()));
        assertEquals(2, Stone.getNumLiberties(board.getPoints()[0][2].getStone(), new HashSet<>()));
    }

    @Test
    void testStonePlacementWithSuicide() {
        int size = 9;
        Board board = new Board(size);

        board.placeStoneOnBoard(1, 1, Color.BLACK);
        assertEquals(4, Stone.getNumLiberties(board.getPoints()[1][1].getStone(), new HashSet<>()));
        board.placeStoneOnBoard(1, 0, Color.WHITE);
        assertEquals(3, Stone.getNumLiberties(board.getPoints()[1][1].getStone(), new HashSet<>()));
        board.removeStoneFromBoard(1, 0);
        assertEquals(4, Stone.getNumLiberties(board.getPoints()[1][1].getStone(), new HashSet<>()));
        board.placeStoneOnBoard(1, 0, Color.WHITE);
        assertEquals(3, Stone.getNumLiberties(board.getPoints()[1][1].getStone(), new HashSet<>()));
        board.removeStoneFromBoard(1, 0);
        assertEquals(4, Stone.getNumLiberties(board.getPoints()[1][1].getStone(), new HashSet<>()));

        board.placeStoneOnBoard(0, 0, Color.BLACK);
        board.placeStoneOnBoard(1, 0, Color.BLACK);
        board.placeStoneOnBoard(0, 1, Color.BLACK);
        board.placeStoneOnBoard(2, 0, Color.WHITE);
        board.placeStoneOnBoard(1, 1, Color.WHITE);
        board.placeStoneOnBoard(0, 2, Color.WHITE);

        assertEquals(board.captureStones(Color.WHITE), 3);  // check that white player captured 3 black stones
        assertEquals(board.getPoints()[0][0].getStone(), null);         // check that stone was removed from board
        assertEquals(board.getPoints()[1][0].getStone(), null);         // check that stone was removed from board
        assertEquals(board.getPoints()[0][1].getStone(), null);         // check that stone was removed from board
        assertEquals(4, Stone.getNumLiberties(board.getPoints()[1][1].getStone(), new HashSet<>()));

        board.placeStoneOnBoard(0, 1, Color.BLACK);
        board.placeStoneOnBoard(1, 0, Color.BLACK);

        assertEquals(2, Stone.getNumLiberties(board.getPoints()[1][1].getStone(), new HashSet<>()));
        assertEquals(false, board.isValidMove(0, 0, Color.BLACK));  // test suicide move
    }


    @Test
    void testSuicideAndCapture_1() {
        int size = 9;
        Board board = new Board(size);

        board.placeStoneOnBoard(1, 0, Color.BLACK);
        board.placeStoneOnBoard(0, 0, Color.WHITE);
        board.placeStoneOnBoard(0, 1, Color.BLACK);

        assertEquals(board.captureStones(Color.BLACK), 1);  // check that black player captured 1 white stone
        assertEquals(board.getPoints()[0][0].getStone(), null);         // check that stone was removed from board

        assertEquals(board.isValidMove(0, 0, Color.WHITE), false);   // test if suicide move in corner valid
        assertEquals(board.isValidMove(0, 0, Color.BLACK), true);
    }

    @Test
    void testSuicideAndCapture_2() {
        int size = 9;
        Board board = new Board(size);

        board.placeStoneOnBoard(0, 0, Color.BLACK);
        board.placeStoneOnBoard(1, 0, Color.BLACK);
        board.placeStoneOnBoard(0, 1, Color.BLACK);
        board.placeStoneOnBoard(2, 0, Color.WHITE);
        board.placeStoneOnBoard(1, 1, Color.WHITE);
        board.placeStoneOnBoard(0, 2, Color.WHITE);

        assertEquals(board.captureStones(Color.WHITE), 3);  // check that white player captured 3 black stones
        assertEquals(board.getPoints()[0][0].getStone(), null);         // check that stone was removed from board
        assertEquals(board.getPoints()[1][0].getStone(), null);         // check that stone was removed from board
        assertEquals(board.getPoints()[0][1].getStone(), null);         // check that stone was removed from board
    }

    @Test
    void testSuicideAndCapture_3() {
        int size = 9;
        Board board = new Board(size);

        board.placeStoneOnBoard(0, 0, Color.BLACK);
        board.placeStoneOnBoard(1, 0, Color.BLACK);
        board.placeStoneOnBoard(0, 1, Color.BLACK);
        board.placeStoneOnBoard(2, 0, Color.WHITE);
        board.placeStoneOnBoard(1, 1, Color.WHITE);
        board.placeStoneOnBoard(0, 2, Color.WHITE);

        assertEquals(board.captureStones(Color.WHITE), 3);  // check that white player captured 3 black stones
        assertEquals(board.getPoints()[0][0].getStone(), null);         // check that stone was removed from board
        assertEquals(board.getPoints()[1][0].getStone(), null);         // check that stone was removed from board
        assertEquals(board.getPoints()[0][1].getStone(), null);         // check that stone was removed from board

        board.placeStoneOnBoard(0, 1, Color.BLACK);
        board.placeStoneOnBoard(1, 0, Color.BLACK);
        assertEquals(false, board.isValidMove(0, 0, Color.BLACK));  // test suicide move

        board.placeStoneOnBoard(3, 4, Color.BLACK);
        board.placeStoneOnBoard(5, 4, Color.BLACK);
        board.placeStoneOnBoard(4, 3, Color.BLACK);
        board.placeStoneOnBoard(4, 5, Color.BLACK);
        board.placeStoneOnBoard(4, 2, Color.WHITE);
        board.placeStoneOnBoard(5, 5, Color.WHITE);
        board.placeStoneOnBoard(3, 3, Color.WHITE);
        board.placeStoneOnBoard(3, 5, Color.WHITE);
        board.placeStoneOnBoard(5, 3, Color.WHITE);
        board.placeStoneOnBoard(2, 4, Color.WHITE);
        board.placeStoneOnBoard(6, 4, Color.WHITE);
        board.placeStoneOnBoard(4, 7, Color.BLACK);
        board.placeStoneOnBoard(4, 6, Color.WHITE);

        assertEquals(board.isValidMove(4, 4, Color.BLACK), false);   // test if suicide move in center valid
        board.placeStoneOnBoard(4, 4, Color.WHITE);
        assertEquals(board.captureStones(Color.WHITE), 4);  // check that white player captured 4 black stones

        assertEquals(board.getPoints()[3][4].getStone(), null);
        assertEquals(board.getPoints()[5][4].getStone(), null);
        assertEquals(board.getPoints()[4][3].getStone(), null);
        assertEquals(board.getPoints()[4][5].getStone(), null);
    }

    @Test
    void testValidLocation() {
        int size = 9;
        Board board = new Board(size);

        boolean isValid = board.isValidMove(0, 0, Color.WHITE);
        isValid &= board.isValidMove(0, size - 1, Color.WHITE);
        isValid &= board.isValidMove(size - 1, 0, Color.WHITE);
        isValid &= board.isValidMove(size - 1, size - 1, Color.WHITE);
        isValid &= !board.isValidMove(-1, 0, Color.WHITE);
        isValid &= !board.isValidMove(0, -1, Color.WHITE);
        isValid &= board.isValidMove(0, 0, Color.BLACK);
        isValid &= board.isValidMove(0, size - 1, Color.BLACK);
        isValid &= board.isValidMove(size - 1, 0, Color.BLACK);
        isValid &= board.isValidMove(size - 1, size - 1, Color.BLACK);
        isValid &= !board.isValidMove(-1, 0, Color.BLACK);
        isValid &= !board.isValidMove(0, -1, Color.BLACK);
        assertEquals( isValid, true );

        assertEquals(board.getPoints()[0][0].getStone(), null);             // check to make sure board not modified
        assertEquals(board.getPoints()[size - 1][0].getStone(), null);
        assertEquals(board.getPoints()[0][size - 1].getStone(), null);
        assertEquals(board.getPoints()[size - 1][size - 1].getStone(), null);
    }

}