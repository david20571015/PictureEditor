package src.operation;

import javafx.scene.paint.Color;
import src.controller.MultiLayerCanvas;
import src.controller.MultiLayerCanvas.SingleLayerCanvas;

public class Pen implements Operation {
    Color penColor;
    double penSize;

    public Pen(Color penColor, double penSize) {
        this.penColor = penColor;
        this.penSize = penSize;
    }

    @Override
    public void apply(MultiLayerCanvas mlc) {
        SingleLayerCanvas layer = mlc.getCurrentLayer();
        layer.addStep();
    }
}