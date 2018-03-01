package sample.Model;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import javafx.scene.paint.Color;

public class Stone {

    private Color color;
    private List<Stone> adjacentStones;
    private int maxLiberties;

    Stone(Color color, int maxLiberties) {
        this.color = color;
        this.maxLiberties = maxLiberties;
        this.adjacentStones = new LinkedList<>();
    }

    public List<Stone> getAdjacentStones() {
        return adjacentStones;
    }

    public Color getColor() {
        return color;
    }

    public static int getNumLiberties(Stone s, Set<Stone> visited) { // pass visited set down recursive calls
        if(s == null || visited.contains(s))                         // to make sure we visit each node only once
            return 0;                                                // and don't get stuck in a cycle

        visited.add(s);
        int numLiberties = (s.maxLiberties - s.getAdjacentStones().size());

        for(Stone adjacentStone : s.getAdjacentStones())
            if(adjacentStone.getColor() == s.getColor())
                numLiberties += getNumLiberties(adjacentStone, visited);

        return numLiberties;
    }

}