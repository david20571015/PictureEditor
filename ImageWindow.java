import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ImageWindow extends Application {

    public ImageWindowController imageWindowController;// = new ImageWindowController();

    @Override
    public void start(Stage arg0) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("resources/ImageWindow.fxml"));
        // fxmlLoader.setController(imageWindowController);

        imageWindowController = fxmlLoader.getController();

        System.out.println("new stage1");

        // Parent root = fxmlLoader.load();
        System.out.println("new stage2");

        arg0.setScene(new Scene(root));
        arg0.setTitle("Image Window");
        arg0.show();

    }
}