package src.operation.imageoperation;

import javafx.scene.image.Image;

public class Filter extends ImageOperation {
    static final public double[][] SMOOTH_FILTER = { { 1. / 9, 1. / 9, 1. / 9 }, { 1. / 9, 1. / 9, 1. / 9 },
            { 1. / 9, 1. / 9, 1. / 9 } };

    static public Image computeFilter(Image inputImage, double[][] mask) {
        return conv2d(inputImage, mask);
    }
}