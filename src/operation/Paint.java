package src.operation;

import javafx.scene.shape.Circle;

public class Paint extends Circle {
    public Paint() {
    }

    public void setPosition(double xPos, double yPos) {
        super.setCenterX(xPos);
        super.setCenterY(yPos);
    }
}