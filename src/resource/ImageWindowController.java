package src.resource;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import src.operation.imageoperation.Filter;
import javafx.event.ActionEvent;

public class ImageWindowController {

    @FXML private MenuItem openMenuItem;
    @FXML private MenuItem saveMenuItem;
    @FXML private TabPane imageTabPane;
    @FXML private Button meanBlur;
    @FXML private Button gaussianBlur;
    @FXML private Button sharpen;
    @FXML private Button relief;
    @FXML private Button unsharpMasking;
    @FXML private Label sizeLabel;
    @FXML private Label zoomLabel;
    @FXML private Label positionLabel;
    @FXML private Label colorLabel;
    @FXML private Rectangle colorBlock;
    @FXML private Label rightStatusLabel;
    @FXML private ProgressBar progressBar;

    public void addImage(File file) {
        Canvas imageView = new Canvas("file:" + file.getAbsolutePath());
        imageView.setSmooth(true);
        imageView.setPreserveRatio(true);

        imageView.setOnScroll(e -> {
            int deltaY = (int) e.getDeltaY();
            imageView.zoomFactor += deltaY * Canvas.ZOOM_RATE;
            imageView.zoomFactor = Math.max(0.01, Math.min(20.0, imageView.zoomFactor));
            imageView.setScaleX(imageView.zoomFactor);
            imageView.setScaleY(imageView.zoomFactor);
            this.zoomLabel.setText(String.valueOf((int) (imageView.zoomFactor * 100.)) + "% ");
        });

        imageView.setOnMouseMoved(e -> {
            this.positionLabel.setText("(" + (int) e.getX() + ", " + (int) e.getY() + ") ");

            Color color = imageView.getImage().getPixelReader().getColor((int) e.getX(), (int) e.getY());
            this.colorLabel.setText("(" + (int) (color.getRed() * 256) + ", " + (int) (color.getGreen() * 256) + ", "
                    + (int) (color.getBlue() * 256) + ") ");
            this.colorBlock.setFill(color);
        });

        imageView.setOnMousePressed(e -> {
            imageView.mouseX = e.getX();
            imageView.mouseY = e.getY();
        });

        imageView.setOnMouseDragged(e -> {
            imageView.setTranslateX(imageView.getTranslateX() + e.getX() - imageView.mouseX);
            imageView.setTranslateY(imageView.getTranslateY() + e.getY() - imageView.mouseY);
        });

        imageView.setOnMouseExited(e -> {
            this.positionLabel.setText("(-, -)");
            this.colorLabel.setText("(-, -, -)");
            this.colorBlock.setFill(Color.WHITE);
        });

        ScrollPane scrollPane = new ScrollPane(imageView);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        Tab tab = new Tab(file.getName(), scrollPane);

        tab.setOnSelectionChanged(e -> {
            this.sizeLabel.setText(
                    (int) (imageView.getImage().getWidth()) + " x " + (int) (imageView.getImage().getHeight()));
        });

        this.imageTabPane.getTabs().add(tab);
    }

    @FXML
    void meanBlurOnAction(ActionEvent event) {
        Tab currentTab = imageTabPane.getSelectionModel().getSelectedItem();
        ImageView currentImageView = (ImageView) ((ScrollPane) currentTab.getContent()).getContent();
        currentImageView.setImage(Filter.computeFilter(currentImageView.getImage(), Filter.MEAN_BLUR));
    }

    @FXML
    void gaussianBlurOnAction(ActionEvent event) {
        Tab currentTab = imageTabPane.getSelectionModel().getSelectedItem();
        ImageView currentImageView = (ImageView) ((ScrollPane) currentTab.getContent()).getContent();
        currentImageView.setImage(Filter.computeFilter(currentImageView.getImage(), Filter.GAUSSIAN_BLUR));
    }

    @FXML
    void reliefOnAction(ActionEvent event) {
        Tab currentTab = imageTabPane.getSelectionModel().getSelectedItem();
        ImageView currentImageView = (ImageView) ((ScrollPane) currentTab.getContent()).getContent();
        currentImageView.setImage(Filter.computeFilter(currentImageView.getImage(), Filter.RELIEF));
    }

    @FXML
    void sharpenOnAction(ActionEvent event) {
        Tab currentTab = imageTabPane.getSelectionModel().getSelectedItem();
        ImageView currentImageView = (ImageView) ((ScrollPane) currentTab.getContent()).getContent();
        currentImageView.setImage(Filter.computeFilter(currentImageView.getImage(), Filter.SHARPEN));
    }

    @FXML
    void unsharpMaskingOnAction(ActionEvent event) {
        Tab currentTab = imageTabPane.getSelectionModel().getSelectedItem();
        ImageView currentImageView = (ImageView) ((ScrollPane) currentTab.getContent()).getContent();
        currentImageView.setImage(Filter.computeFilter(currentImageView.getImage(), Filter.UNSHAPR_MASKING));
    }

    public void closeStage() {
        this.imageTabPane.getTabs().clear();
    }
}

class Canvas extends ImageView {
    static final double ZOOM_RATE = 0.01;

    public double mouseX, mouseY;
    public double zoomFactor = 1.0;

    public Canvas(String filePath) {
        super(filePath);
    }
}