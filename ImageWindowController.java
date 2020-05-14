import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class ImageWindowController {

    @FXML
    private MenuItem openMenuItem;
    @FXML
    private MenuItem saveMenuItem;
    @FXML
    private Tab fileList;
    @FXML
    private ImageView imageView;
    @FXML
    private Label rightStatusLabel;
    @FXML
    private ProgressBar progressBar;

    public void addImage(File file) {
        System.out.println(file);
        imageView.setImage(new Image(file.getAbsolutePath()));
    }

}