import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;

public class PictureViewerController {
    private File currentFolderPath;

    @FXML
    private MenuItem openMenuItem;

    @FXML
    private TitledPane folderTree;

    public void initialize() {
    }

    @FXML
    void openMenuItemOnAction(ActionEvent event) {
        currentFolderPath = getFolderPath();
        folderTree.setText(currentFolderPath.toString());
    }

    private File getFolderPath() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
            return chooser.getSelectedFile();
        else
            return null;
    }

}
