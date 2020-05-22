package src.operation.imageoperation;

import src.operation.Operation;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;

public class ImageOperation implements Operation {
    protected Image conv2d(Image inputImage, double[][] mask) {
        WritableImage output = new WritableImage(inputImage.getPixelReader(), (int) inputImage.getWidth(),
                (int) inputImage.getHeight());

        int paddingNum = (mask.length - 1) / 2;
        Color[][] inputColors = imageToMatrix(inputImage, Color.WHITE, paddingNum);

        int width = (int) inputImage.getWidth();
        int height = (int) inputImage.getHeight();
        PixelWriter pw = output.getPixelWriter();

        for (int j = paddingNum; j < height + paddingNum; j++)
            for (int i = paddingNum; i < width + paddingNum; i++) {
                int r = 0, g = 0, b = 0;

                for (int m = -paddingNum; m < paddingNum + 1; m++)
                    for (int n = -paddingNum; n < paddingNum + 1; n++) {
                        r += inputColors[i + m][j + n].getRed();
                        g += inputColors[i + m][j + n].getGreen();
                        b += inputColors[i + m][j + n].getBlue();
                    }
                pw.setColor(i - paddingNum, j - paddingNum, new Color(r, g, b, 1));
            }

        return output;
    }

    private Color[][] imageToMatrix(Image inputImage, Color padding, int paddingNum) {
        int width = (int) inputImage.getWidth();
        int height = (int) inputImage.getHeight();
        PixelReader pr = inputImage.getPixelReader();

        Color[][] colorss = new Color[width + paddingNum * 2][];
        for (Color[] colors : colorss)
            colors = new Color[height + paddingNum * 2];

        for (int j = 0; j < height + paddingNum * 2; j++)
            for (int i = 0; i < width + paddingNum * 2; i++) {
                if (i < paddingNum || i >= height + paddingNum || j < paddingNum || j >= width + paddingNum)
                    colorss[i][j] = padding;
                else
                    colorss[i][j] = pr.getColor(i - paddingNum, j - paddingNum);
            }

        return colorss;
    }
}