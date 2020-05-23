package src.operation.imageoperation;

import src.operation.Operation;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImageOperation implements Operation {
    static protected Image conv2d(Image inputImage, double[][] mask) {
        WritableImage output = new WritableImage(inputImage.getPixelReader(), (int) inputImage.getWidth(),
                (int) inputImage.getHeight());

        int paddingSize = (mask.length - 1) / 2;
        Color[][] inputColors = imageToMatrix(inputImage, Color.WHITE, paddingSize);

        int width = (int) inputImage.getWidth();
        int height = (int) inputImage.getHeight();
        PixelWriter pw = output.getPixelWriter();

        for (int j = paddingSize; j < height + paddingSize; j++)
            for (int i = paddingSize; i < width + paddingSize; i++) {
                double r = 0, g = 0, b = 0;

                for (int m = -paddingSize; m < paddingSize + 1; m++)
                    for (int n = -paddingSize; n < paddingSize + 1; n++) {
                        r += inputColors[i + m][j + n].getRed() * mask[paddingSize + m][paddingSize + n];
                        g += inputColors[i + m][j + n].getGreen() * mask[paddingSize + m][paddingSize + n];
                        b += inputColors[i + m][j + n].getBlue() * mask[paddingSize + m][paddingSize + n];
                    }

                r = Math.max(0, Math.min(r, 1.));
                g = Math.max(0, Math.min(g, 1.));
                b = Math.max(0, Math.min(b, 1.));

                pw.setColor(i - paddingSize, j - paddingSize, new Color(r, g, b, 1.0));
            }

        return output;
    }

    static private Color[][] imageToMatrix(Image inputImage, Color padding, int paddingSize) {
        int width = (int) inputImage.getWidth();
        int height = (int) inputImage.getHeight();
        PixelReader pr = inputImage.getPixelReader();

        Color[][] colorss = new Color[width + paddingSize * 2][];
        for (int i = 0; i < colorss.length; i++)
            colorss[i] = new Color[height + paddingSize * 2];

        for (int j = 0; j < height + paddingSize * 2; j++)
            for (int i = 0; i < width + paddingSize * 2; i++) {
                if (i < paddingSize || i >= width + paddingSize || j < paddingSize || j >= height + paddingSize)
                    colorss[i][j] = padding;
                else
                    colorss[i][j] = pr.getColor(i - paddingSize, j - paddingSize);
            }
        return colorss;
    }
}