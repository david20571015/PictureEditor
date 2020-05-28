package src.operation.imageoperation;

import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class Pen extends Circle {
    public Pen() {
    }

    public void setPosition(double xPos, double yPos) {
        super.setCenterX(xPos);
        super.setCenterY(yPos);
        // System.out.println("pen pos: (" + (int) xPos + ", " + (int) yPos + ")");
    }
}