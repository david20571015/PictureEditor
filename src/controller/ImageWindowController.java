package src.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import src.operation.Filter;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;

public class ImageWindowController {
    @FXML
    private MenuItem openMenuItem;
    @FXML
    private MenuItem saveMenuItem;
    @FXML
    private MenuItem undoMenuItem;
    @FXML
    private MenuItem redoMenuItem;
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
    private Button negative;
    @FXML
    private Button grayScale;
    @FXML
    private Label sizeLabel;
    @FXML
    private Label zoomLabel;
    @FXML
    private ColorPicker penColorPicker;
    @FXML
    private Slider penSizeSlider;
    @FXML
    private TextField penSizeTextField;
    @FXML
    private Slider saturationSlider;
    @FXML
    private Slider brightnessSlider;
    @FXML
    private Slider hueSlider;
    @FXML
    private Slider contrastSlider;
    @FXML
    private GridPane layersGridPane;
    @FXML
    private Button addNewLayerButtom;
    @FXML
    private Label positionLabel;
    @FXML
    private Label colorRGBLabel;
    @FXML
    private Label colorHSBLabel;
    @FXML
    private Rectangle colorBlock;
    @FXML
    private Label rightStatusLabel;
    @FXML
    private ProgressBar progressBar;

    @FXML
    void initialize() {
        penSizeTextField.textProperty().bindBidirectional(penSizeSlider.valueProperty(), new StringConverter<Number>() {
            @Override
            public String toString(Number t) {
                return String.format("%.0f", t);
            }

            @Override
            public Number fromString(String s) {
                return Integer.valueOf(s);
            }
        });
    }

    @FXML
    void saveMenuItemOnAction(ActionEvent event) {
        Tab currentTab = imageTabPane.getSelectionModel().getSelectedItem();
        Image currentImage = getCurreMultiLayerCanvas().snapshot(null, null);
        File currentFile = new File(currentTab.getText());
        System.out.println("ImageWindowController.saveMenuItemOnAction()");
        System.out.println(currentImage.getHeight());
        currentFile = new File(currentFile.getName().split("\\.")[0]);
        BufferedImage bImage = SwingFXUtils.fromFXImage(currentImage, null);
        BufferedImage bbImage = new BufferedImage((int) currentImage.getWidth(), (int) currentImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bbImage.createGraphics();
        g.drawImage(bImage, 0, 0, null);
        g.dispose();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image File as");
        fileChooser.setInitialDirectory(new File("C:/"));
        fileChooser.setInitialFileName(currentFile.toString());

        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("jpg", "*.jpg"),
                new FileChooser.ExtensionFilter("png", "*.png"), new FileChooser.ExtensionFilter("bmp", "*.bmp"));

        File savePath = fileChooser.showSaveDialog(null);
        String fileExtension = fileChooser.selectedExtensionFilterProperty().get().getDescription();
        System.out.println(savePath + " " + fileExtension);
        if (savePath != null) {
            try {
                ImageIO.write(bbImage, fileExtension, savePath);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private MultiLayerCanvas getCurreMultiLayerCanvas() {
        Tab currentTab = imageTabPane.getSelectionModel().getSelectedItem();
        MultiLayerCanvas currentMLC = (MultiLayerCanvas) ((ScrollPane) currentTab.getContent()).getContent();
        return currentMLC;
    }

    public void addImage(File file) {
        Image baseImage = null;
        try {
            baseImage = new Image(file.toURI().toURL().toString());
        } catch (Exception e) {
            System.out.println(e);
        }
        MultiLayerCanvas multiLayerCanvas = new MultiLayerCanvas(baseImage);

        ColumnConstraints column = new ColumnConstraints();
        layersGridPane.getColumnConstraints().add(column);

        Label label = new Label("base");
        label.setPadding(new Insets(5, 5, 5, 5));
        layersGridPane.add(label, 0, 1, 1, 1);
        layersGridPane.add(new ImageView(), 1, 1, 1, 1);

        CheckBox cb = new CheckBox();
        cb.setDisable(true);
        layersGridPane.add(cb, 2, 1, 1, 1);

        // imageView.setSmooth(true);
        // imageView.setPreserveRatio(true);

        // imageView.getPen().fillProperty().bind(penColorPicker.valueProperty());
        // imageView.getPen().radiusProperty().bind(penSizeSlider.valueProperty());
        // imageView.getPen().setVisible(true);

        // ColorAdjust colorAdjust = new ColorAdjust();
        // colorAdjust.saturationProperty().bind(saturationSlider.valueProperty());
        // colorAdjust.brightnessProperty().bind(brightnessSlider.valueProperty());
        // colorAdjust.hueProperty().bind(hueSlider.valueProperty());
        // colorAdjust.contrastProperty().bind(contrastSlider.valueProperty());

        // saturationSlider.valueProperty().addListener((v, ov, nv) ->
        // imageView.setEffect(colorAdjust));
        // brightnessSlider.valueProperty().addListener((v, ov, nv) ->
        // imageView.setEffect(colorAdjust));
        // hueSlider.valueProperty().addListener((v, ov, nv) ->
        // imageView.setEffect(colorAdjust));
        // contrastSlider.valueProperty().addListener((v, ov, nv) ->
        // imageView.setEffect(colorAdjust));

        multiLayerCanvas.setOnScroll(e -> {
            double deltaY = e.getDeltaY();
            deltaY = (deltaY > 0 ? 0.1 : -0.1);
            if (multiLayerCanvas.getScaleX() > 0.05 && multiLayerCanvas.getScaleY() > 0.05) {
                multiLayerCanvas.setScaleX(multiLayerCanvas.getScaleX() + deltaY);
                multiLayerCanvas.setScaleY(multiLayerCanvas.getScaleY() + deltaY);
            }
            zoomLabel.setText(String.valueOf((int) (multiLayerCanvas.getScaleX() * 100)) + "% ");
        });

        multiLayerCanvas.setOnMouseEntered(e -> {
            multiLayerCanvas.getScene().setCursor(Cursor.CROSSHAIR);
        });

        multiLayerCanvas.setOnMouseMoved(e -> {
            int xPos = (int) (e.getX() * multiLayerCanvas.getScaleX());
            int yPos = (int) (e.getY() * multiLayerCanvas.getScaleY());

            positionLabel.setText("(" + xPos + ", " + yPos + ")");

            Color color = multiLayerCanvas.snapshot(null, null).getPixelReader().getColor(xPos, yPos);
            colorRGBLabel.setText("(" + (int) (color.getRed() * 256) + ", " + (int) (color.getGreen() * 256) + ", "
                    + (int) (color.getBlue() * 256) + ") ");
            colorHSBLabel.setText(
                    "(" + String.format("%.3f", color.getHue()) + ", " + String.format("%.3f", color.getSaturation())
                            + ", " + String.format("%.3f", color.getBrightness()) + ") ");
            colorBlock.setFill(color);

            // imageView.getPen().setPosition(e.getX(), e.getY());
        });

        multiLayerCanvas.setOnMousePressed(e -> {
            // imageView.mouseX = e.getX();
            // imageView.mouseY = e.getY();
            // imageView.penX = (int) e.getX();
            // imageView.penY = (int) e.getY();
            // });

            // imageView.setOnMouseDragged(e -> {
            // if (e.getButton().equals(MouseButton.PRIMARY)) {
            // imageView.getScene().setCursor(Cursor.DISAPPEAR);
            // Circle penCircle = new Circle();

            // penCircle.radiusProperty().bind(penSizeSlider.valueProperty());
            // penCircle.fillProperty().bind(penColorPicker.valueProperty());
            // penCircle.setStroke(Color.BLACK);
            // penCircle.setCenterX(e.getX());
            // penCircle.setCenterY(e.getY());

            // imageView.paint((int) e.getX(), (int) e.getY(), (Color)
            // imageView.getPen().getFill());
            // }
            // if (e.getButton().equals(MouseButton.SECONDARY)) {
            // imageView.getScene().setCursor(Cursor.MOVE);
            // imageView.setTranslateX(imageView.getTranslateX() + e.getX() -
            // imageView.mouseX);
            // imageView.setTranslateY(imageView.getTranslateY() + e.getY() -
            // imageView.mouseY);
            // }
        });

        multiLayerCanvas.setOnMouseExited(e -> {
            multiLayerCanvas.getScene().setCursor(Cursor.DEFAULT);
            this.positionLabel.setText("(-, -)");
            this.colorRGBLabel.setText("(-, -, -)");
            this.colorHSBLabel.setText("(-, -, -)");
            this.colorBlock.setFill(Color.WHITE);
        });

        ScrollPane scrollPane = new ScrollPane(multiLayerCanvas);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        Tab tab = new Tab(file.getName(), scrollPane);

        tab.setOnSelectionChanged(e -> {
            this.sizeLabel.setText((int) (multiLayerCanvas.getWidth()) + " x " + (int) (multiLayerCanvas.getHeight()));
            multiLayerCanvas.updateLayersDetail(layersGridPane);
            // saturationSlider.valueProperty().unbind();
            // brightnessSlider.valueProperty().unbind();
            // hueSlider.valueProperty().unbind();
            // contrastSlider.valueProperty().unbind();

            // imageView.setEffect(new ColorAdjust(0, 0, 0, 0));
            // saturationSlider.setValue(((ColorAdjust)
            // imageView.getEffect()).getSaturation());
            // brightnessSlider.setValue(((ColorAdjust)
            // imageView.getEffect()).getBrightness());
            // hueSlider.setValue(((ColorAdjust) imageView.getEffect()).getHue());
            // contrastSlider.setValue(((ColorAdjust) imageView.getEffect()).getContrast());
        });

        this.imageTabPane.getTabs().add(tab);
    }

    @FXML
    void filterButtonOnAction(ActionEvent event) {
        MultiLayerCanvas mlc = getCurreMultiLayerCanvas();
        Filter filter = null;

        if (event.getSource() == meanBlur) {
            filter = new Filter(new String("MeanBlur"), Filter.MEAN_BLUR, true);
        } else if (event.getSource() == gaussianBlur) {
            filter = new Filter(new String("GaussianBlur"), Filter.GAUSSIAN_BLUR, true);
        } else if (event.getSource() == sharpen) {
            filter = new Filter(new String("Sharpen"), Filter.SHARPEN, true);
        } else if (event.getSource() == relief) {
            filter = new Filter(new String("Relief"), Filter.RELIEF, true);
        } else if (event.getSource() == unsharpMasking) {
            filter = new Filter(new String("UnsharpMasking"), Filter.UNSHAPR_MASKING, true);
        } else if (event.getSource() == negative) {
            filter = new Filter(new String("Negative"), Filter.NULL_FILTER, false);
        } else if (event.getSource() == grayScale) {
            filter = new Filter(new String("GrayScale"), Filter.NULL_FILTER, false);
        }

        filter.apply(mlc);
        mlc.updateLayersDetail(layersGridPane);
    }

    @FXML
    void layersGridPaneOnMouseClicked(MouseEvent event) {
        Node clickedNode = event.getPickResult().getIntersectedNode();
        if (clickedNode instanceof ImageView) {
            int index = GridPane.getRowIndex(clickedNode) - 1;
            getCurreMultiLayerCanvas().setCurrrntLayer(index);
            getCurreMultiLayerCanvas().updateLayersDetail(layersGridPane);
        }
    }

    @FXML
    void addNewLayerButtomOnAction(ActionEvent event) {
        MultiLayerCanvas currentCanvas = getCurreMultiLayerCanvas();
        currentCanvas.addLayer();

        int newRowIndex = layersGridPane.getRowCount();
        ColumnConstraints column = new ColumnConstraints();
        layersGridPane.getColumnConstraints().add(column);

        Label label = new Label(String.valueOf(newRowIndex - 1));
        label.setPadding(new Insets(5, 5, 5, 5));
        layersGridPane.add(label, 0, newRowIndex, 1, 1);
        layersGridPane.add(new ImageView(), 1, newRowIndex, 1, 1);

        CheckBox cb = new CheckBox();
        cb.setOnAction(e -> currentCanvas.setLayerVisible(GridPane.getRowIndex(cb), cb.isSelected()));
        layersGridPane.add(cb, 2, newRowIndex, 1, 1);

        Button deletebuButton = new Button("delete");
        layersGridPane.add(deletebuButton, 3, newRowIndex, 1, 1);
        deletebuButton.setOnAction(e -> {
            Integer rowIndex = GridPane.getRowIndex(deletebuButton);
            rowIndex = (rowIndex == null ? 0 : rowIndex);

            currentCanvas.deleteLayer(rowIndex);

            Set<Node> deleteNodes = new HashSet<>();
            for (Node node : layersGridPane.getChildren()) {
                Integer row = GridPane.getRowIndex(node);
                row = (row == null ? 0 : row);
                if (row > rowIndex) {
                    if (GridPane.getColumnIndex(node) == 0 || GridPane.getColumnIndex(node) == null) {
                        ((Label) node).setText(String.valueOf(row - 2));
                    }
                    GridPane.setRowIndex(node, row - 1);
                } else if (row == rowIndex) {
                    deleteNodes.add(node);
                }
            }
            layersGridPane.getChildren().removeAll(deleteNodes);

            currentCanvas.updateLayersDetail(layersGridPane);
        });

        currentCanvas.updateLayersDetail(layersGridPane);
    }

    @FXML
    void UndoMenuItemOnAction(ActionEvent event) {
        MultiLayerCanvas currMLC = getCurreMultiLayerCanvas();
        currMLC.undo();
        currMLC.updateLayersDetail(layersGridPane);
    }

    @FXML
    void RedoMenuItemOnAction(ActionEvent event) {
        // Tab currentTab = imageTabPane.getSelectionModel().getSelectedItem();
        // Canvas currentCanvas = (Canvas) ((ScrollPane)
        // currentTab.getContent()).getContent();

        // if (currentCanvas.operationIter < currentCanvas.operations.size() - 1)
        // currentCanvas.setImage(currentCanvas.operations.get(++currentCanvas.operationIter));

        // System.out.printf("redo, Operation.size() = %d, iter = %d\n",
        // currentCanvas.operations.size(),
        // currentCanvas.operationIter);
    }

    public void closeStage() {
        this.imageTabPane.getTabs().clear();

        Set<Node> deleteNodes = new HashSet<>();
        for (Node node : layersGridPane.getChildren()) {
            Integer row = GridPane.getRowIndex(node);
            row = (row == null ? 0 : row);
            if (row > 0) {
                deleteNodes.add(node);
            }
        }
        layersGridPane.getChildren().removeAll(deleteNodes);
    }
}