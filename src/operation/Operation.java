package src.operation;

import src.controller.MultiLayerCanvas;

public interface Operation {
    void apply(MultiLayerCanvas mlc, int index);
}