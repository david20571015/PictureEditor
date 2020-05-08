import java.io.File;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.stage.DirectoryChooser;

public class PictureViewerController {
    private File currentFolderPath;
    private String defaultOpenFolderPath = "D:/PxDownloader";// System.getProperty("user.home") + "/Desktop"

    @FXML
    private MenuItem openMenuItem;

    @FXML
    private TitledPane folderTree;

    public void initialize() {
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
