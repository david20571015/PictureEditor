package src.operation.imageoperation;

import javafx.scene.image.Image;

public class Filter extends ImageOperation {
    static final double[][] SMOOTH_FILTER = { { 1. / 9, 1. / 9, 1. / 9 }, { 1. / 9, 1. / 9, 1. / 9 },
            { 1. / 9, 1. / 9, 1. / 9 } };

    public Image computeFilter(Image inputImage, double[][] mask) {
        return conv2d(inputImage, mask);
    }
}