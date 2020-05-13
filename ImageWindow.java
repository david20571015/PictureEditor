import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class ImageWindow {

    @FXML private MenuItem openMenuItem;
    @FXML private MenuItem saveMenuItem;
    @FXML private Tab fileList;
    @FXML private ImageView imageView;
    @FXML private Label rightStatusLabel;
    @FXML private ProgressBar progressBar;

    Stage stage;

    public ImageWindow(){
        try {
            stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("ImageWindow.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("ImageWindow");
            stage.show();
        } catch (Exception exc) {
        }
    }

    public void show() {
        stage.show();
    }


    public void setimage(String file){
        imageView.setImage(new Image(file));
    }

}