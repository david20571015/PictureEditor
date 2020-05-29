package src.resource;

import src.operation.imageoperation.Filter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;

public class ImageWindowController {

    @FXML
    private MenuItem openMenuItem;
    @FXML
    private MenuItem saveMenuItem;
    @FXML
    private TabPane imageTabPane;
    @FXML
    private Button meanBlur;
    @FXML
    private Button gaussianBlur;
    @FXML
    private Button sharpen;
    @FXML
    private Button relief;
    @FXML
    private Button unsharpMasking;
    @FXML
    private Label sizeLabel;
    @FXML
    private Label zoomLabel;
    @FXML
    private Label positionLabel;
    @FXML
    private Label colorLabel;
    @FXML
    private Rectangle colorBlock;
    @FXML
    private Label rightStatusLabel;
    @FXML
    private ProgressBar progressBar;

    @FXML
    void saveMenuItemOnAction(ActionEvent event) {
        Tab currentTab = imageTabPane.getSelectionModel().getSelectedItem();
        Image currentImage = ((ImageView) ((ScrollPane) currentTab.getContent()).getContent()).getImage();
        File currentFile = new File(currentTab.getText());
        currentFile = new File(currentFile.getName().split("\\.")[0]);
        BufferedImage bImage = SwingFXUtils.fromFXImage(currentImage, null);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image File as");
        fileChooser.setInitialDirectory(new File("C:/"));
        fileChooser.setInitialFileName(currentFile.toString());

        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("jpg", "*.jpg"),
                new FileChooser.ExtensionFilter("png", "*.png"), new FileChooser.ExtensionFilter("bmp", "*.bmp"));

        File savePath = fileChooser.showSaveDialog(null);

        String fileExtension = fileChooser.selectedExtensionFilterProperty().get().getDescription();

        if (savePath != null) {
            try {
                ImageIO.write(bImage, fileExtension, savePath);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

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
    void filterButtonOnAction(ActionEvent event) {
        Tab currentTab = imageTabPane.getSelectionModel().getSelectedItem();
        ImageView currentImageView = (ImageView) ((ScrollPane) currentTab.getContent()).getContent();

        double[][] filter = { { 1 } };

        if (event.getSource() == meanBlur)
            filter = Filter.MEAN_BLUR;
        else if (event.getSource() == gaussianBlur)
            filter = Filter.GAUSSIAN_BLUR;
        else if (event.getSource() == sharpen)
            filter = Filter.SHARPEN;
        else if (event.getSource() == relief)
            filter = Filter.RELIEF;
        else if (event.getSource() == unsharpMasking)
            filter = Filter.UNSHAPR_MASKING;

        currentImageView.setImage(Filter.computeFilter(currentImageView.getImage(), filter));
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