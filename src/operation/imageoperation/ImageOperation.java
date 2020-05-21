package src.operation.imageoperation;

import src.operation.Operation;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public abstract class ImageOperation implements Operation {
    public abstract WritableImage execute(Image img);

    private WritableImage conv2d(Image inputImage, double[][] mask) {
        WritableImage input = new WritableImage(inputImage.getPixelReader(), (int) inputImage.getWidth(),
                (int) inputImage.getHeight());

    }

    private Color[][] imageToMatrix(Image inputImage, Color padding) {

    }
}