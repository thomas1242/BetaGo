package Test;

import static org.junit.jupiter.api.Assertions.*;
import go.Model.Player;
import org.junit.jupiter.api.Test;
import javafx.scene.paint.Color;

class PlayerTest {

    @Test
    void testSuicideAndCapture_1() {
        Player player1 = new Player("P1", Color.BLACK);
        Player player2 = new Player("P2", Color.WHITE);

        assertEquals(player1.getName(), "P1");
        assertEquals(player1.getColor(), Color.BLACK);

        assertEquals(player2.getName(), "P2");
        assertEquals(player2.getColor(), Color.WHITE);

        player1.incrementStonesCaptured(10);
        player1.incrementStonesCaptured(-5);
        player2.incrementStonesCaptured(20);
        player2.incrementStonesCaptured(-5);
        assertEquals(player1.getNumStonesCaptured(), 5);
        assertEquals(player2.getNumStonesCaptured(), 15);
    }

}