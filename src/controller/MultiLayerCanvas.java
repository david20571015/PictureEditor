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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import src.operation.Pen;
import javafx.embed.swing.SwingFXUtils;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MultiLayerCanvas extends StackPane {
    public Pen pen = null;
    public Shape shape = null;
    private ArrayList<Integer> layerRecord = new ArrayList<Integer>();
    private int imgWidth;
    private int imgHeight;
    private double mouseClickedX;
    private double mouseClickedY;
    private double mouseLastX;
    private double mouseLastY;
    private SingleLayerCanvas currentSLC;
    private int currentSLCIndex;
    private Integer slcNum = 1;

    public MultiLayerCanvas(Image img) {
        imgWidth = (int) img.getWidth();
        imgHeight = (int) img.getHeight();
        SingleLayerCanvas baseLayer = new SingleLayerCanvas(imgWidth, imgHeight, img);
        baseLayer.num = 0;
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

    public void setMouseClickedPos(double x, double y) {
        this.mouseClickedX = x;
        this.mouseClickedY = y;
    }

    public double getMouseClickedX() {
        return mouseClickedX;
    }

    public double getMouseClickedY() {
        return mouseClickedY;
    }

    public void setMouseLastPos(double x, double y) {
        this.mouseLastX = x;
        this.mouseLastY = y;
    }

    public double getMouseLastX() {
        return mouseLastX;
    }

    public double getMouseLastY() {
        return mouseLastY;
    }

    public void addLayer() {
        SingleLayerCanvas subLayer = new SingleLayerCanvas(imgWidth, imgHeight);
        subLayer.num = slcNum++;
        getChildren().add(subLayer);
    }

    public void setCurrrntLayer(int index) {
        ObservableList<Node> canvases = this.getChildren();
        this.currentSLC = (SingleLayerCanvas) (canvases.get(index));
        this.currentSLCIndex = index;
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
        int deleteInteger = deletedLayer.num;

        this.getChildren().remove(deletedLayer);

        for (int i = 0; i < layerRecord.size() - 1; i++)
            if (layerRecord.get(i).intValue() == deleteInteger)
                layerRecord.remove(i--);
    }

    public void undo() {
        if (!layerRecord.isEmpty()) {
            // System.out.println("MultiLayerCanvas.undo()");
            // System.out.println("layer num = " + layerRecord.size() + " " +
            // layerRecord.get(layerRecord.size() - 1));
            int lastSLCnum = layerRecord.remove(layerRecord.size() - 1).intValue();
            for (int i = 0; i < getChildren().size(); i++) {
                SingleLayerCanvas slc = (SingleLayerCanvas) getChildren().get(i);
                if (slc.num == lastSLCnum) {
                    slc.undo();
                    break;
                }
            }
        }
    }

    public class SingleLayerCanvas extends Canvas {
        ArrayList<Image> snapShotRecord = new ArrayList<Image>();

        public int num;

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
            layerRecord.add(num);

            System.out.println("add step  size : " + layerRecord.size() + " index : " + num);

            // BufferedImage bImage = SwingFXUtils.fromFXImage(shot, null);
            // BufferedImage bbImage = new BufferedImage((int) shot.getWidth(), (int)
            // shot.getHeight(),
            // BufferedImage.TYPE_INT_RGB);
            // Graphics2D g = bbImage.createGraphics();
            // g.drawImage(bImage, 0, 0, null);
            // g.dispose();

            // try {
            // ImageIO.write(bbImage, "png",
            // new File("C:\\Users\\david\\Desktop\\test" + snapShotRecord.size() +
            // ".png"));
            // } catch (IOException e) {
            // System.out.println(e.getMessage());
            // }
        }

        public void undo() {
            if (!snapShotRecord.isEmpty()) {
                // System.out.println("MultiLayerCanvas.SingleLayerCanvas.undo()");
                // System.out.println("snapshot num = " + snapShotRecord.size());
                Image lastImage = snapShotRecord.remove(snapShotRecord.size() - 1);
                BufferedImage bImage = SwingFXUtils.fromFXImage(lastImage, null);
                BufferedImage bbImage = new BufferedImage((int) lastImage.getWidth(), (int) lastImage.getHeight(),
                        BufferedImage.TYPE_INT_RGB);
                Graphics2D g = bbImage.createGraphics();
                g.drawImage(bImage, 0, 0, null);
                g.dispose();

                try {
                    ImageIO.write(bbImage, "png",
                            new File("C:\\Users\\david\\Desktop\\test" + num + "-" + snapShotRecord.size() + ".png"));
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                System.out.println("undo index : " + num);
                getGraphicsContext2D().drawImage(lastImage, 0, 0);
            }
        }
    }
}