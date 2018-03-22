package sample.Model;
import javafx.scene.paint.Color;

public class Player {

    private Color color;
    private String name;
    private int enemyStonesCaptured;
    private boolean isUsingAI;
    private int colorEnclosedRegionPoints;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public void enableAI() {
        isUsingAI = true;
    }

    public void disableAI() {
        isUsingAI = false;
    }

    public boolean isUsingAI() {
        return isUsingAI;
    }

    public Color getColor() {
        return color;
    }

    public void incrementStonesCaptured(int n) {
        enemyStonesCaptured += n;
    }

    public int getNumStonesCaptured() {
        return enemyStonesCaptured;
    }

    public void resetScore() {
        enemyStonesCaptured = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCERPoints(int score){
        colorEnclosedRegionPoints = score;
    }
    public int getScore(){
        return enemyStonesCaptured + colorEnclosedRegionPoints;
    }

}