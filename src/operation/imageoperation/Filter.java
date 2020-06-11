package src.operation.imageoperation;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Filter extends ImageOperation {
	static final public double[][] MEAN_BLUR = { { 1. / 9, 1. / 9, 1. / 9 }, { 1. / 9, 1. / 9, 1. / 9 },
			{ 1. / 9, 1. / 9, 1. / 9 } };

	static final public double[][] GAUSSIAN_BLUR = { { 1. / 273, 4. / 273, 7. / 273, 4. / 273, 1. / 273 },
			{ 4. / 273, 16. / 273, 26. / 273, 16. / 273, 4. / 273 },
			{ 7. / 273, 26. / 273, 41. / 273, 26. / 273, 7. / 273 },
			{ 4. / 273, 16. / 273, 26. / 273, 16. / 273, 4. / 273 },
			{ 1. / 273, 4. / 273, 7. / 273, 4. / 273, 1. / 273 } };

	static final public double[][] SHARPEN = { { 0, -1., 0 }, { -1., 5., -1. }, { 0, -1., 0 } };

	static final public double[][] RELIEF = { { 2., 1., 0 }, { 1., 1., -1. }, { 0, -1., -2. } };

	static final public double[][] UNSHAPR_MASKING = { { -1. / 273, -4. / 273, -7. / 273, -4. / 273, -1. / 273 },
			{ -4. / 273, -16. / 273, -26. / 273, -16. / 273, -4. / 273 },
			{ -7. / 273, -26. / 273, 505. / 273, -26. / 273, -7. / 273 },
			{ -4. / 273, -16. / 273, -26. / 273, -16. / 273, -4. / 273 },
			{ -1. / 273, -4. / 273, -7. / 273, -4. / 273, -1. / 273 } };

	static public Image toNegative(Image inputImage) {
		WritableImage outputImage = new WritableImage(inputImage.getPixelReader(), (int) inputImage.getWidth(),
				(int) inputImage.getHeight());

		int width = (int) inputImage.getWidth();
		int height = (int) inputImage.getHeight();

		PixelReader pr = inputImage.getPixelReader();
		PixelWriter pw = outputImage.getPixelWriter();

		for (int j = 0; j < height; j++)
			for (int i = 0; i < width; i++) {
				double r = 1 - pr.getColor(i, j).getRed();
				double g = 1 - pr.getColor(i, j).getGreen();
				double b = 1 - pr.getColor(i, j).getBlue();

				r = Math.max(0, Math.min(r, 1.));
				g = Math.max(0, Math.min(g, 1.));
				b = Math.max(0, Math.min(b, 1.));

				pw.setColor(i, j, new Color(r, g, b, 1.0));
			}

		return outputImage;
	}

	static public Image toGrayScale(Image inputImage) {
		WritableImage outputImage = new WritableImage(inputImage.getPixelReader(), (int) inputImage.getWidth(),
				(int) inputImage.getHeight());

		int width = (int) inputImage.getWidth();
		int height = (int) inputImage.getHeight();

		PixelReader pr = inputImage.getPixelReader();
		PixelWriter pw = outputImage.getPixelWriter();

		for (int j = 0; j < height; j++)
			for (int i = 0; i < width; i++) {
				double gray = pr.getColor(i, j).getRed() + pr.getColor(i, j).getGreen() + pr.getColor(i, j).getBlue();
				gray /= 3.;
				gray = Math.max(0, Math.min(gray, 1.));
				pw.setColor(i, j, new Color(gray, gray, gray, 1.0));
			}

		return outputImage;
	}

	static public Image computeFilter(Image inputImage, double[][] mask) {
		return conv2d(inputImage, mask);
	}
}