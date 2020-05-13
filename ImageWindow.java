import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class ImageWindow {
    public void show() {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("ImageWindow.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("ImageWindow");
            stage.show();
        } catch (Exception exc) {
        }
    }
}