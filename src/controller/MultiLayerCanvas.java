package src.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class MultiLayerCanvas extends StackPane {
    private ArrayList<SingleLayerCanvas> layerRecord = new ArrayList<SingleLayerCanvas>();
    private int imgWidth;
    private int imgHeight;
    private SingleLayerCanvas currentSLC;
    private int currentSLCIndex;

    public MultiLayerCanvas(Image img) {
        imgWidth = (int) img.getWidth();
        imgHeight = (int) img.getHeight();
        SingleLayerCanvas baseLayer = new SingleLayerCanvas(imgWidth, imgHeight, img);
        currentSLC = baseLayer;
        currentSLCIndex = 0;
        getChildren().add(baseLayer);
    }

    public int getImageWidth() {
        return imgWidth;
    }

    public int getImageHeight() {
        return imgHeight;
    }

    public void addLayer() {
        SingleLayerCanvas subLayer = new SingleLayerCanvas(imgWidth, imgHeight);
        getChildren().add(subLayer);
    }

    public SingleLayerCanvas getLayer(int index) {
        if (index > 0 && index < layerRecord.size())
            return layerRecord.get(index);
        return null;
    }

    public void setCurrrntLayer(int index) {
        ObservableList<Node> canvases = this.getChildren();
        this.currentSLC = (SingleLayerCanvas) (canvases.get(index));
        this.currentSLCIndex = index;

        this.currentSLC.getGraphicsContext2D().fillRect(100, 100, 100, 100);
    }

    public SingleLayerCanvas getCurrentLayer() {
        return currentSLC;
    }

    public void updateLayersDetail(GridPane layersGridPane) {
        ObservableList<Node> childrens = layersGridPane.getChildren();
        ObservableList<Node> canvases = this.getChildren();

        if (canvases.size() < layersGridPane.getRowCount() - 1) {
            Integer rowIndex = canvases.size();
            rowIndex = (rowIndex == null ? 0 : rowIndex);
            Set<Node> deleteNodes = new HashSet<>();
            for (Node node : layersGridPane.getChildren()) {
                Integer row = GridPane.getRowIndex(node);
                row = (row == null ? 0 : row);
                if (row > rowIndex) {
                    deleteNodes.add(node);
                } else if (row == rowIndex) {
                }
            }
            layersGridPane.getChildren().removeAll(deleteNodes);
        } else if (canvases.size() > layersGridPane.getRowCount() - 1) {
            Integer rowNum = canvases.size() - layersGridPane.getRowCount() + 1;
            for (int i = 0; i < rowNum; i++) {
                int newRowIndex = layersGridPane.getRowCount();

                ColumnConstraints column = new ColumnConstraints();
                layersGridPane.getColumnConstraints().add(column);

                Label label = new Label(String.valueOf(newRowIndex - 1));
                label.setPadding(new Insets(5, 5, 5, 5));
                layersGridPane.add(label, 0, newRowIndex, 1, 1);
                layersGridPane.add(new ImageView(), 1, newRowIndex, 1, 1);

                CheckBox cb = new CheckBox();
                cb.setOnAction(e -> this.setLayerVisible(GridPane.getRowIndex(cb), cb.isSelected()));
                layersGridPane.add(cb, 2, newRowIndex, 1, 1);

                Button deletebuButton = new Button("delete");
                layersGridPane.add(deletebuButton, 3, newRowIndex, 1, 1);
                deletebuButton.setOnAction(e -> {
                    Integer rowIndex = GridPane.getRowIndex(deletebuButton);
                    rowIndex = (rowIndex == null ? 0 : rowIndex);

                    this.deleteLayer(rowIndex);

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

                    this.updateLayersDetail(layersGridPane);
                });
            }
        }

        for (Node node : childrens) {
            if (node instanceof Group)
                continue;
            Integer colIndex = GridPane.getColumnIndex(node);
            colIndex = (colIndex == null ? 0 : colIndex);
            Integer rowIndex = GridPane.getRowIndex(node);
            rowIndex = (rowIndex == null ? 0 : rowIndex) - 1;

            if (colIndex == 0 && rowIndex >= 0) {
                Label label = (Label) node;
                if (rowIndex == currentSLCIndex)
                    label.setStyle("-fx-background-color : yellow");
                else
                    label.setStyle(null);
            }

            if (colIndex == 1 && rowIndex >= 0) {
                ImageView iv = (ImageView) node;
                iv.setPreserveRatio(true);
                iv.setSmooth(true);
                iv.setCache(true);

                iv.setImage(((SingleLayerCanvas) canvases.get(rowIndex)).snapshot(null, null));
                iv.setFitWidth(50);
            }
            if (colIndex == 2 && rowIndex >= 0) {
                int no = GridPane.getRowIndex(node) - 1;
                CheckBox cb = (CheckBox) node;
                cb.setSelected(((SingleLayerCanvas) canvases.get(no)).isVisible());
            }
        }
    }

    public void setLayerVisible(int index, boolean visible) {
        ObservableList<Node> canvases = this.getChildren();
        ((SingleLayerCanvas) (canvases.get(index - 1))).setVisible(visible);
    }

    public void deleteLayer(int index) {
        ObservableList<Node> canvases = this.getChildren();
        SingleLayerCanvas deletedLayer = (SingleLayerCanvas) canvases.get(index - 1);

        this.getChildren().remove(deletedLayer);
        for (SingleLayerCanvas canvas : layerRecord)
            if (canvas.equals(deletedLayer))
                layerRecord.remove(canvas);
    }

    public void undo() {
        if (!layerRecord.isEmpty()) {
            SingleLayerCanvas lastSLC = layerRecord.remove(layerRecord.size() - 1);
            lastSLC.undo();
        }
    }

    public class SingleLayerCanvas extends Canvas {
        ArrayList<Image> snapShotRecord = new ArrayList<Image>();

        public SingleLayerCanvas(int width, int height) {
            super(width, height);
        }

        public SingleLayerCanvas(int width, int height, Image img) {
            super(width, height);
            super.getGraphicsContext2D().drawImage(img, 0, 0);
        }

        public void addStep() {
            WritableImage shot = new WritableImage((int) (getWidth()), (int) (getHeight()));
            SnapshotParameters sp = new SnapshotParameters();
            sp.setFill(Color.TRANSPARENT);
            snapshot(sp, shot);
            snapShotRecord.add(shot);
            layerRecord.add(this);
        }

        public void undo() {
            if (!snapShotRecord.isEmpty()) {
                Image lastImage = snapShotRecord.remove(snapShotRecord.size() - 1);
                getGraphicsContext2D().drawImage(lastImage, 0, 0);
            }
        }
    }
}