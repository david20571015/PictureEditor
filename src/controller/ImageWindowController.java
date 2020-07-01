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
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import src.operation.Filter;
import src.operation.Pen;
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
    private CheckBox paintingCheckBox;
    @FXML
    private Slider penSizeSlider;
    @FXML
    private TextField penSizeTextField;
    @FXML
    private ToggleGroup shapeToggleGroup;
    @FXML
    private RadioButton lineRadioButton;
    @FXML
    private RadioButton rectangleRadioButton;
    @FXML
    private RadioButton ellipseRadioButton;
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
    void initialize() {
        lineRadioButton.setUserData(new Line());
        rectangleRadioButton.setUserData(new Rectangle());
        ellipseRadioButton.setUserData(new Ellipse());

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
            multiLayerCanvas.getScene().setCursor(Cursor.CROSSHAIR);
            int xPos = (int) (e.getX() * multiLayerCanvas.getScaleX());
            int yPos = (int) (e.getY() * multiLayerCanvas.getScaleY());

            positionLabel.setText("(" + xPos + ", " + yPos + ")");

            try {
                Color color = multiLayerCanvas.snapshot(null, null).getPixelReader().getColor(xPos, yPos);
                colorRGBLabel.setText("(" + (int) (color.getRed() * 256) + ", " + (int) (color.getGreen() * 256) + ", "
                        + (int) (color.getBlue() * 256) + ") ");
                colorHSBLabel.setText("(" + String.format("%.3f", color.getHue()) + ", "
                        + String.format("%.3f", color.getSaturation()) + ", "
                        + String.format("%.3f", color.getBrightness()) + ") ");
                colorBlock.setFill(color);
            } catch (IndexOutOfBoundsException ex) {
            }
        });

        multiLayerCanvas.setOnMousePressed(e -> {
            multiLayerCanvas.setMouseClickedPos(e.getX(), e.getY());

            if (e.getButton().equals(MouseButton.PRIMARY)) {
                multiLayerCanvas.pen = new Pen(penColorPicker.getValue(), penSizeSlider.getValue());
                multiLayerCanvas.getCurrentLayer().getGraphicsContext2D().setLineWidth(penSizeSlider.getValue());
                multiLayerCanvas.getCurrentLayer().getGraphicsContext2D().setStroke(penColorPicker.getValue());
                multiLayerCanvas.setMouseLastPos(e.getX(), e.getY());
                multiLayerCanvas.pen.apply(multiLayerCanvas);

                if (!paintingCheckBox.isSelected()) {
                    Shape shape = (Shape) shapeToggleGroup.getSelectedToggle().getUserData();
                    shape.setStroke(penColorPicker.getValue());
                    shape.setStrokeWidth(penSizeSlider.getValue());
                    shape.setFill(Color.TRANSPARENT);
                    multiLayerCanvas.shape = shape;
                    multiLayerCanvas.getChildren().add(new Pane(multiLayerCanvas.shape));
                    if (shape instanceof Line) {
                        ((Line) multiLayerCanvas.shape).startXProperty().set(e.getX());
                        ((Line) multiLayerCanvas.shape).startYProperty().set(e.getY());
                        ((Line) multiLayerCanvas.shape).endXProperty().set(e.getX());
                        ((Line) multiLayerCanvas.shape).endYProperty().set(e.getY());
                    } else if (shape instanceof Rectangle) {
                        ((Rectangle) multiLayerCanvas.shape).xProperty().set(e.getX());
                        ((Rectangle) multiLayerCanvas.shape).yProperty().set(e.getY());
                        ((Rectangle) multiLayerCanvas.shape).widthProperty().set(0);
                        ((Rectangle) multiLayerCanvas.shape).heightProperty().set(0);
                    } else if (shape instanceof Ellipse) {
                        ((Ellipse) multiLayerCanvas.shape).centerXProperty().set(e.getX());
                        ((Ellipse) multiLayerCanvas.shape).centerYProperty().set(e.getY());
                        ((Ellipse) multiLayerCanvas.shape).radiusXProperty().set(0);
                        ((Ellipse) multiLayerCanvas.shape).radiusYProperty().set(0);
                    }
                }
            }

        });

        multiLayerCanvas.setOnMouseDragged(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                if (paintingCheckBox.isSelected()) {
                    GraphicsContext gc = multiLayerCanvas.getCurrentLayer().getGraphicsContext2D();
                    gc.strokeLine(multiLayerCanvas.getMouseLastX(), multiLayerCanvas.getMouseLastY(), e.getX(),
                            e.getY());
                    multiLayerCanvas.setMouseLastPos(e.getX(), e.getY());
                } else {
                    if (multiLayerCanvas.shape instanceof Line) {
                        ((Line) multiLayerCanvas.shape).endXProperty().set(e.getX());
                        ((Line) multiLayerCanvas.shape).endYProperty().set(e.getY());
                    } else if (multiLayerCanvas.shape instanceof Rectangle) {
                        double x = multiLayerCanvas.getMouseClickedX();
                        double y = multiLayerCanvas.getMouseClickedY();
                        double w = e.getX() - x;
                        double h = e.getY() - y;
                        if (w < 0) {
                            x += w;
                            w *= -1;
                        }
                        if (h < 0) {
                            y += h;
                            h *= -1;
                        }
                        ((Rectangle) multiLayerCanvas.shape).xProperty().set(x);
                        ((Rectangle) multiLayerCanvas.shape).yProperty().set(y);
                        ((Rectangle) multiLayerCanvas.shape).widthProperty().set(w);
                        ((Rectangle) multiLayerCanvas.shape).heightProperty().set(h);
                    } else if (multiLayerCanvas.shape instanceof Ellipse) {
                        ((Ellipse) multiLayerCanvas.shape).radiusXProperty()
                                .set(Math.abs(e.getX() - multiLayerCanvas.getMouseClickedX()));
                        ((Ellipse) multiLayerCanvas.shape).radiusYProperty()
                                .set(Math.abs(e.getY() - multiLayerCanvas.getMouseClickedY()));
                    }
                }
            }

            if (e.getButton().equals(MouseButton.SECONDARY)) {
                multiLayerCanvas.getScene().setCursor(Cursor.MOVE);
                double scale = multiLayerCanvas.getScaleX();

                // FIX !!!
                // Canvas move unideally when scale < 1 .
                multiLayerCanvas.setTranslateX(
                        multiLayerCanvas.getTranslateX() + e.getX() - multiLayerCanvas.getMouseClickedX());
                multiLayerCanvas.setTranslateY(
                        multiLayerCanvas.getTranslateY() + e.getY() - multiLayerCanvas.getMouseClickedY());
            }
        });

        multiLayerCanvas.setOnMouseReleased(e -> {
            if (!paintingCheckBox.isSelected()) {
                GraphicsContext gc = multiLayerCanvas.getCurrentLayer().getGraphicsContext2D();

                if (multiLayerCanvas.shape instanceof Line) {
                    gc.strokeLine(((Line) multiLayerCanvas.shape).startXProperty().get(),
                            ((Line) multiLayerCanvas.shape).startYProperty().get(),
                            ((Line) multiLayerCanvas.shape).endXProperty().get(),
                            ((Line) multiLayerCanvas.shape).endYProperty().get());
                } else if (multiLayerCanvas.shape instanceof Rectangle) {
                    double x = ((Rectangle) multiLayerCanvas.shape).xProperty().get();
                    double y = ((Rectangle) multiLayerCanvas.shape).yProperty().get();
                    double w = ((Rectangle) multiLayerCanvas.shape).widthProperty().get();
                    double h = ((Rectangle) multiLayerCanvas.shape).heightProperty().get();
                    if (w < 0) {
                        x += w;
                        w *= -1;
                    }
                    if (h < 0) {
                        y += h;
                        h *= -1;
                    }
                    gc.strokeRect(x, y, w, h);
                } else if (multiLayerCanvas.shape instanceof Ellipse) {
                    double x = ((Ellipse) multiLayerCanvas.shape).centerXProperty().get();
                    double y = ((Ellipse) multiLayerCanvas.shape).centerYProperty().get();
                    double w = ((Ellipse) multiLayerCanvas.shape).radiusXProperty().get();
                    double h = ((Ellipse) multiLayerCanvas.shape).radiusYProperty().get();
                    if (w < 0) {
                        x += w;
                        w *= -1;
                    }
                    if (h < 0) {
                        y += h;
                        h *= -1;
                    }
                    gc.strokeOval(x - w, y - h, w * 2, h * 2);
                }
                if (multiLayerCanvas.getChildren().get(multiLayerCanvas.getChildren().size() - 1) instanceof Pane)
                    multiLayerCanvas.getChildren().remove(multiLayerCanvas.getChildren().size() - 1);
                multiLayerCanvas.updateLayersDetail(layersGridPane);
            }
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
            this.sizeLabel.setText(
                    (int) (multiLayerCanvas.getImageWidth()) + " x " + (int) (multiLayerCanvas.getImageHeight()));
            multiLayerCanvas.updateLayersDetail(layersGridPane);
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