package src.operation.imageoperation;

import src.operation.Operation;

import javafx.scene.image.Image;

public interface ImageOperation extends Operation {
    Image execute(Image img);
}