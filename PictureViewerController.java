import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;

public class PictureViewerController {
    private File currentFolderPath;
    private String defaultOpenFolderPath = "D:/HOMEWORK/2nd_Spring";// System.getProperty("user.home") + "/Desktop"

    @FXML
    private MenuItem openMenuItem;

    @FXML
    private TitledPane folderTitledPane;

    @FXML
    private ImageView imageView;

    @FXML
    private TreeView<File> folderTreeView;

    @FXML
    private FlowPane imageFlowPane;

    @FXML
    private FlowPane folderPathFlowPane;

    @FXML
    void imageViewDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    void imageViewDropped(DragEvent event) throws FileNotFoundException {
        List<File> file = event.getDragboard().getFiles();
        Image img = new Image(new FileInputStream(file.get(0)));
        imageView.setImage(img);
    }

    @FXML
    void openMenuItemOnAction(final ActionEvent event) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Folder");
        directoryChooser.setInitialDirectory(new File(defaultOpenFolderPath));
        currentFolderPath = directoryChooser.showDialog(null);

        String[] parsedPath = currentFolderPath.toString().split("\\\\");

        folderTitledPane.setText(parsedPath[parsedPath.length - 1]);

        folderTreeView = new TreeView<File>(new FolderItem(currentFolderPath));
        folderTreeView.setShowRoot(false);

        folderTreeView.addEventHandler(MouseEvent.MOUSE_CLICKED,
                e -> showImagesInFolder(folderTreeView.getSelectionModel().getSelectedItems().get(0)));

        folderTitledPane.setContent(folderTreeView);

    }

    private void showImagesInFolder(TreeItem<File> folderPath) {
        File[] images = folderPath.getValue().listFiles(File::isFile);
        imageFlowPane.getChildren().clear();
        for (File image : images)
            imageFlowPane.getChildren().add(new ImageFilePane(image));
    }

}

class ImageFilePane extends BorderPane {
    private Image image;
    private String imageName;

    public ImageFilePane(File imagePath) {
        setHeight(200);
        setWidth(200);
        setBorder(new Border(
                new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        try {
            this.image = new Image(new FileInputStream(imagePath), getWidth(), getHeight(), true, true);
        } catch (Exception e) {
            System.out.println("Unable to load image from " + imagePath);
        }

        if (this.image != null) {
            this.imageName = imagePath.getName();

            ImageView imageView = new ImageView(this.image);
            Label label = new Label(this.imageName);
            label.setTextAlignment(TextAlignment.CENTER);

            setCenter(imageView);
            setBottom(label);
        }
    }

}

class FolderItem extends TreeItem<File> {
    private boolean isFirstTimeChildren = true;
    private boolean isFirstTimeLeaf = true;
    private boolean isLeaf;

    public FolderItem(File rootPath) {
        super(rootPath);
    }

    @Override
    public ObservableList<TreeItem<File>> getChildren() {
        if (isFirstTimeChildren) {
            isFirstTimeChildren = false;
            super.getChildren().setAll(buildChildren(this));
        }
        return super.getChildren();
    }

    @Override
    public boolean isLeaf() {
        if (isFirstTimeLeaf) {
            isFirstTimeLeaf = false;
            File f = (File) getValue();
            isLeaf = f.isFile();
        }
        return isLeaf;
    }

    @Override
    public String toString() {
        return this.getValue().getName();
    }

    private ObservableList<TreeItem<File>> buildChildren(TreeItem<File> treeItem) {
        File f = treeItem.getValue();

        if (f != null && f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) {
                ObservableList<TreeItem<File>> children = FXCollections.observableArrayList();
                for (File childrenFile : files)
                    if (childrenFile.isDirectory())
                        children.add(new FolderItem(childrenFile));
                return children;
            }
        }
        return FXCollections.emptyObservableList();
    }
}
