package src.operation;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import src.controller.MultiLayerCanvas;
import src.controller.MultiLayerCanvas.SingleLayerCanvas;

public class Filter implements Operation {
	String filterType = null;
	double[][] kernel = { { 1 } };
	double normalizeFactor = 1;

	public Filter(String filterType, double[][] kernel, boolean needNormailze) {
		this.filterType = filterType;
		this.kernel = kernel;

		if (needNormailze)
			kernel = normalize(kernel);
	}

	private double[][] normalize(double[][] kernel) {
		double sum = 0;
		for (double[] elements : kernel)
			for (double element : elements)
				sum += element;

		for (double[] elements : kernel)
			for (double element : elements)
				element /= sum;
		return kernel;
	}

	@Override
	public void apply(MultiLayerCanvas mlc) {
		SingleLayerCanvas layer = mlc.getCurrentLayer();
		layer.addStep();

		SnapshotParameters sp = new SnapshotParameters();
		sp.setFill(Color.TRANSPARENT);
		Image snap = layer.snapshot(sp, null);
		Image image = null;

		if (filterType.equals(new String("Negative"))) {
			image = toNegative(snap);
		} else if (filterType.equals(new String("GrayScale"))) {
			image = toGrayScale(snap);
		} else {
			image = conv2d(snap, kernel);
		}
		layer.getGraphicsContext2D().drawImage(image, 0, 0);
	}

	static final public double[][] NULL_FILTER = { { 1 } };

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
				if (pr.getColor(i, j).getOpacity() > 0) {
					double r = 1 - pr.getColor(i, j).getRed();
					double g = 1 - pr.getColor(i, j).getGreen();
					double b = 1 - pr.getColor(i, j).getBlue();

					r = Math.max(0, Math.min(r, 1.));
					g = Math.max(0, Math.min(g, 1.));
					b = Math.max(0, Math.min(b, 1.));

					pw.setColor(i, j, new Color(r, g, b, 1.0));
				} else {
					pw.setColor(i, j, Color.TRANSPARENT);
				}
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
				if (pr.getColor(i, j).getOpacity() > 0) {
					double gray = pr.getColor(i, j).getRed() + pr.getColor(i, j).getGreen()
							+ pr.getColor(i, j).getBlue();
					gray /= 3.;
					gray = Math.max(0, Math.min(gray, 1.));
					pw.setColor(i, j, new Color(gray, gray, gray, 1.0));
				} else {
					pw.setColor(i, j, Color.TRANSPARENT);
				}
			}
		return outputImage;
	}

	static private Image conv2d(Image inputImage, double[][] mask) {
		WritableImage outputImage = new WritableImage(inputImage.getPixelReader(), (int) inputImage.getWidth(),
				(int) inputImage.getHeight());

		int paddingSize = (mask.length - 1) / 2;

		int width = (int) inputImage.getWidth();
		int height = (int) inputImage.getHeight();
		PixelReader pr = inputImage.getPixelReader();
		PixelWriter pw = outputImage.getPixelWriter();

		for (int j = paddingSize; j < height - paddingSize; j++)
			for (int i = paddingSize; i < width - paddingSize; i++) {
				if (pr.getColor(i, j).getOpacity() > 0) {
					double r = 0, g = 0, b = 0;

					for (int m = -paddingSize; m < paddingSize + 1; m++)
						for (int n = -paddingSize; n < paddingSize + 1; n++) {
							r += pr.getColor(i + m, j + n).getRed() * mask[paddingSize + m][paddingSize + n];
							g += pr.getColor(i + m, j + n).getGreen() * mask[paddingSize + m][paddingSize + n];
							b += pr.getColor(i + m, j + n).getBlue() * mask[paddingSize + m][paddingSize + n];
						}

					r = Math.max(0, Math.min(r, 1.));
					g = Math.max(0, Math.min(g, 1.));
					b = Math.max(0, Math.min(b, 1.));

					pw.setColor(i, j , new Color(r, g, b, 1.0));
				} else {
					pw.setColor(i, j, Color.TRANSPARENT);
				}
			}
		return outputImage;
	}
}