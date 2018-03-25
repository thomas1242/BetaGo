package go.Model;

import java.util.List;
import java.util.LinkedList;

public class Point {

    private List<Point> adjacentPoints;
    private Stone stone;

    Point() {
        this.adjacentPoints = new LinkedList<>();
        this.stone = null;
    }

    public List<Point> getAdjacentPoints() {
        return adjacentPoints;
    }

    public Stone getStone() {
        return stone;
    }

    public void setStone(Stone stone) {
        this.stone = stone;
    }

}
