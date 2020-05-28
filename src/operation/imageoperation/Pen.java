package src.operation.imageoperation;

import javafx.scene.shape.Circle;

public class Pen extends Circle {
    public Pen() {
    }

    public void setPosition(double xPos, double yPos) {
        super.setCenterX(xPos);
        super.setCenterY(yPos);
    }
}