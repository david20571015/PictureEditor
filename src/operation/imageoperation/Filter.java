package src.operation.imageoperation;

import javafx.scene.image.Image;

public class Filter extends ImageOperation {
    // static final public double[][] MEAN_BLUR = { { 1. / 9, 1. / 9, 1. / 9 }, { 1.
    // / 9, 1. / 9, 1. / 9 },
    // { 1. / 9, 1. / 9, 1. / 9 } };

    static final public double[][] MEAN_BLUR = { { 1. / 9, 1. / 9, 1. / 9 }, { 1. / 9, 1. / 9, 1. / 9 },
            { 1. / 9, 1. / 9, 1. / 9 } };

    static final public double[][] GAUSSIAN_BLUR = { { 1. / 273, 4. / 273, 7. / 273, 4. / 273, 1. / 273 },
            { 4. / 273, 16. / 273, 26. / 273, 16. / 273, 4. / 273 },
            { 7. / 273, 26. / 273, 41. / 273, 26. / 273, 7. / 273 },
            { 4. / 273, 16. / 273, 26. / 273, 16. / 273, 4. / 273 },
            { 1. / 273, 4. / 273, 7. / 273, 4. / 273, 1. / 273 } };

    static final public double[][] SHARPEN = { { 0, -1., 0 }, { -1., 5., -1. }, { 0, -1., 0 } };

    static final public double[][] RELIEF = { { -2., -1., 0 }, { -1., 1., 1. }, { 0, 1., 2. } };

    static final public double[][] UNSHAPR_MASKING = { { -1. / 273, -4. / 273, -7. / 273, -4. / 273, -1. / 273 },
            { -4. / 273, -16. / 273, -26. / 273, -16. / 273, -4. / 273 },
            { -7. / 273, -26. / 273, 505. / 273, -26. / 273, -7. / 273 },
            { -4. / 273, -16. / 273, -26. / 273, -16. / 273, -4. / 273 },
            { -1. / 273, -4. / 273, -7. / 273, -4. / 273, -1. / 273 } };

    static public Image computeFilter(Image inputImage, double[][] mask) {
        return conv2d(inputImage, mask);
    }
}