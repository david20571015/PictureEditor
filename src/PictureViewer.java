package src;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;

import src.controller.PictureViewerController;

public class PictureViewer extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public void start(Stage arg0) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resource\\PictureViewer.fxml"));
        Scene scene = new Scene(loader.load());
        arg0.setScene(scene);
        arg0.setTitle("Picture Viewer");
        arg0.show();
        arg0.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                PictureViewerController pictureViewerController = loader.getController();
                pictureViewerController.record();
            }
        });
    }
}