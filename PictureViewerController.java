import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;

public class PictureViewerController {
    private File currentFolderPath;

    @FXML private MenuItem openMenuItem;

    @FXML private TitledPane folderTree;
    
    @FXML private ImageView imageView;


    public void initialize() {
    }

    @FXML
    void imageViewDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    void imageViewDropped(DragEvent event) throws FileNotFoundException{
        List<File> file = event.getDragboard().getFiles();
        Image img = new Image(new FileInputStream(file.get(0)));
        imageView.setImage(img);

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
