package src.controller;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class MultiLayerCanvas extends StackPane {
    private ArrayList<SingleLayerCanvas> layerRecord = new ArrayList<SingleLayerCanvas>();
    private Image originalImage;
    private int imgWidth;
    private int imgHeight;
    private SingleLayerCanvas currentSLC;
    private int currentSLCIndex;

    public MultiLayerCanvas(Image img) {
        originalImage = img;
        imgWidth = (int) img.getWidth();
        imgHeight = (int) img.getHeight();
        SingleLayerCanvas baseLayer = new SingleLayerCanvas(imgWidth, imgHeight, img);
        currentSLC = baseLayer;
        currentSLCIndex = 0;
        getChildren().add(baseLayer);
    }

    int testIndex = 1;// test

    public void addLayer() {
        SingleLayerCanvas subLayer = new SingleLayerCanvas(imgWidth, imgHeight, testIndex++);// test
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
        System.out.println("set current layer " + index);
    }

    public SingleLayerCanvas getCurrentLayer() {
        return currentSLC;
    }

    public void updateLayersDetail(GridPane layersGridPane) {
        ObservableList<Node> childrens = layersGridPane.getChildren();
        ObservableList<Node> canvases = this.getChildren();
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

    public class SingleLayerCanvas extends Canvas {
        ArrayList<Image> snapShotRecord = new ArrayList<Image>();

        // test
        public SingleLayerCanvas(int width, int height, int testIndex) {
            super(width, height);
            super.getGraphicsContext2D().fillText(String.valueOf(testIndex), testIndex * 50, testIndex * 50);
        }
        // test

        public SingleLayerCanvas(int width, int height) {
            super(width, height);
        }

        public SingleLayerCanvas(int width, int height, Image img) {
            super(width, height);
            super.getGraphicsContext2D().drawImage(img, 0, 0);
        }

        public void addStep() {
            WritableImage shot = new WritableImage((int) (getWidth()), (int) (getHeight()));
            snapshot(null, shot);
            snapShotRecord.add(shot);
            layerRecord.add(this);
        }
    }
}