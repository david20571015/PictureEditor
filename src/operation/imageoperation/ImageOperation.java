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
                int r = 0, g = 0, b = 0;
                // System.out.println(inputColors[i][j].getRed() + " " +
                // inputColors[i][j].getGreen() + " "
                // + inputColors[i][j].getBlue());

                for (int m = -paddingSize; m < paddingSize + 1; m++)
                    for (int n = -paddingSize; n < paddingSize + 1; n++) {
                        r += inputColors[i + m][j + n].getRed() * 256 * mask[paddingSize + m][paddingSize + n];
                        g += inputColors[i + m][j + n].getGreen() * 256 * mask[paddingSize + m][paddingSize + n];
                        b += inputColors[i + m][j + n].getBlue() * 256 * mask[paddingSize + m][paddingSize + n];
                    }
                // System.out.println((i - paddingSize) + " " + (j - paddingSize));
                pw.setColor(i - paddingSize, j - paddingSize, Color.rgb(r, g, b));
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
                else {
                    // System.out.println((i - paddingSize) + " " + (j - paddingSize));
                    colorss[i][j] = pr.getColor(i - paddingSize, j - paddingSize);
                }
            }
        System.out.println("complete imageToMatrix");
        return colorss;
    }
}