import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;

public class PictureViewerController {
    private File currentFolderPath;
    private String defaultOpenFolderPath = "D:/PxDownloader";// System.getProperty("user.home") + "/Desktop"

    @FXML
    private MenuItem openMenuItem;

    @FXML
    private TitledPane folderTree;

    @FXML
    private ImageView imageView;

    public void initialize() {
    }

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

        folderTree.setText(parsedPath[parsedPath.length - 1]);
    }

}
