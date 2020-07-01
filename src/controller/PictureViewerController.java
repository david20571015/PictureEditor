package src.controller;

import src.window.ImageWindow;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
import javafx.util.Callback;
import javafx.scene.text.Text;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.iptc.IptcReader;
import com.drew.metadata.*;

public class PictureViewerController {
    private File currentFolderPath;
    private File currentShowFolderPath;
    private String defaultOpenFolderPath = System.getProperty("user.home") + "/Desktop";

    private ImageWindow imageWindow = null;
    private ArrayList<Text> pathtext = new ArrayList<Text>();
    private TreeItem<File> item = new TreeItem<>();
    private MenuItem favorite = new MenuItem("add to Favorite");
    private MenuItem delete = new MenuItem("delete");
    private ContextMenu cm;
    private File file;
    private FileWriter writer;

    String[] pictureformat = { ".bmp", ".png", ".gif", ".jpeg", ".jpg" };
    String[] exifcanread = { ".jpeg", ".gif", ".png" };

    @FXML
    private MenuItem openMenuItem;
    @FXML
    private MenuItem aboutMenuItem;
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
    private Label rightStatusLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TreeView<File> favoriteTreeView;
    @FXML
    private TitledPane favoriteTitledPane;
    @FXML
    private Text imformationOfFileText;

    @FXML
    void initialize() {
        file = new File(".//src//resource//favorite.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        favoriteTreeView.setShowRoot(false);
        favoriteTreeView.setRoot(item);
        favoriteTreeView.setCellFactory(new Callback<TreeView<File>, TreeCell<File>>() {
            public TreeCell<File> call(TreeView<File> tv) {
                return new TreeCell<File>() {
                    @Override
                    protected void updateItem(File item, boolean empty) {
                        super.updateItem(item, empty);

                        setText((empty || item == null) ? "" : item.getName());
                    }
                };
            }
        });
        try {
            BufferedReader reader = new BufferedReader(new FileReader(".//src//resource//favorite.txt"));
            String favoritepath = reader.readLine();
            while (favoritepath != null) {
                // System.out.println("reading");
                File f = new File(favoritepath);
                if (f.exists()) {
                    favoriteTreeView.getRoot().getChildren().add(new TreeItem<File>(f));
                    favoriteTreeView.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                        if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                            showImagesInFolder(favoriteTreeView.getSelectionModel().getSelectedItems().get(0));
                        } else if (e.getButton() == MouseButton.SECONDARY) {
                            showDeleteBox(e, favoriteTreeView.getSelectionModel().getSelectedItems().get(0));
                        }
                    });
                }
                favoritepath = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void record() {
        try {
            writer = new FileWriter(".//src//resource//favorite.txt");
            if (!favoriteTreeView.getRoot().getChildren().isEmpty()) {
                for (int i = 0; i < favoriteTreeView.getRoot().getChildren().size() - 1; i++) {
                    writer.write(favoriteTreeView.getRoot().getChildren().get(i).getValue().toString() + "\n");
                }
                writer.write(favoriteTreeView.getRoot().getChildren()
                        .get(favoriteTreeView.getRoot().getChildren().size() - 1).getValue().toString() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        folderPathFlowPane.getChildren().clear();
        pathtext.clear();

        folderTreeView.setCellFactory(new Callback<TreeView<File>, TreeCell<File>>() {
            public TreeCell<File> call(TreeView<File> tv) {
                return new TreeCell<File>() {
                    @Override
                    protected void updateItem(File item, boolean empty) {
                        super.updateItem(item, empty);

                        setText((empty || item == null) ? "" : item.getName());
                    }
                };
            }
        });

        folderTreeView.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                showImagesInFolder(folderTreeView.getSelectionModel().getSelectedItems().get(0));
            } else if (e.getButton() == MouseButton.SECONDARY) {
                showFavoriteBox(e, folderTreeView.getSelectionModel().getSelectedItems().get(0));
            }
        });

        folderTitledPane.setContent(folderTreeView);
        changefolderPathFlowPane(parsedPath);
        addImageIntoFolder(currentFolderPath);
    }

    @FXML
    void aboutMenuItemOnAction(final ActionEvent event) {
        try {
            URL srcLink = new URL("https://github.com/david20571015/java_final_project");
            Desktop.getDesktop().browse(srcLink.toURI());
        } catch (Exception e) {
        }
    }

    private void showFavoriteBox(MouseEvent e, TreeItem<File> folderPath) {
        cm = new ContextMenu(favorite);
        cm.show(folderTreeView, e.getScreenX(), e.getScreenY());
        cm.setOnAction(ev -> {
            if (ev.getTarget().equals(favorite)) {
                boolean has = false;
                for (int i = 0; i < favoriteTreeView.getRoot().getChildren().size(); i++) {
                    if (favoriteTreeView.getRoot().getChildren().get(i).getValue() == folderPath.getValue())
                        has = true;
                }
                if (!has) {
                    favoriteTreeView.setShowRoot(false);
                    favoriteTreeView.getRoot().getChildren().add(new TreeItem<File>(folderPath.getValue()));
                    favoriteTreeView.addEventHandler(MouseEvent.MOUSE_CLICKED, eve -> {
                        if (eve.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                            showImagesInFolder(favoriteTreeView.getSelectionModel().getSelectedItems().get(0));
                        } else if (eve.getButton() == MouseButton.SECONDARY) {
                            showDeleteBox(eve, favoriteTreeView.getSelectionModel().getSelectedItems().get(0));
                        }
                    });
                    cm.hide();
                }

            }
        });
    }

    private void showDeleteBox(MouseEvent e, TreeItem<File> folderPath) {
        cm = new ContextMenu(delete);
        cm.show(favoriteTreeView, e.getScreenX(), e.getScreenY());
        cm.setOnAction(ev -> {
            if (ev.getTarget().equals(delete)) {
                favoriteTreeView.getRoot().getChildren().remove(favoriteTreeView.getSelectionModel().getSelectedItem());
            }
        });
    }

    @FXML
    void onMouseClicked(MouseEvent event) {
        if (cm != null)
            cm.hide();
    }

    private void showImagesInFolder(TreeItem<File> folderPath) {
        if (!folderPath.getValue().equals(currentShowFolderPath)) {
            rightStatusLabel.setText("Loading images");
            addImageIntoFolder(folderPath.getValue());
            rightStatusLabel.setText("Complete");

            currentShowFolderPath = folderPath.getValue();

            String[] srt = folderPath.getValue().toString().split("\\\\");

            changefolderPathFlowPane(srt);
        }
    }

    private void addImageIntoFolder(File path) {
        imageFlowPane.getChildren().clear();

        File[] images = path.listFiles(File::isFile);
        double fileCounter = 1;
        for (File image : images) {
            Boolean ispictureformat = false;
            for (String s : pictureformat) {
                if (image.toString().endsWith(s))
                    ispictureformat = true;
            }

            if (ispictureformat) {
                ImageFilePane img = new ImageFilePane(image);
                img.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        if (e.getButton().equals(MouseButton.PRIMARY))
                            if (e.getClickCount() == 2) {
                                if (imageWindow == null)
                                    imageWindow = new ImageWindow();
                                imageWindow.show();
                                imageWindow.getController().addImage(image);
                            }
                        if (e.getClickCount() == 1) {
                            Boolean isexifcanreBoolean = false;
                            for (String s : exifcanread) {
                                if (image.toString().endsWith(s)) {
                                    isexifcanreBoolean = true;
                                }
                            }
                            if (isexifcanreBoolean) {
                                try {
                                    Metadata metadata = ImageMetadataReader.readMetadata(image);
                                    print(metadata);
                                } catch (ImageProcessingException eee) {
                                    print(eee);
                                } catch (IOException ev) {
                                    print(ev);
                                }
                            } else {
                                imformationOfFileText.setText("");
                            }
                        }
                    }
                });
                imageFlowPane.getChildren().add(img);
            }
            progressBar.setProgress(fileCounter++ / images.length);
        }
    }

    private void print(Metadata metadata) {
        imformationOfFileText.setText("");
        // imformationOfFileTextField.setText("");
        for (Directory directory : metadata.getDirectories()) {
            String total = new String();
            for (Tag tag : directory.getTags()) {
                String s = tag.toString();
                int delete = s.indexOf(']', 1);
                s = s.substring(delete + 2);
                imformationOfFileText.setText(imformationOfFileText.getText() + s + "\n");
                // String s = tag.toString();
                // int delete = s.indexOf(']', 1);
                // s = s.substring(delete + 2);
                // total = total +s;
            }
            // System.out.print(total);
            // imformationOfFileTextField.setText(total);
            for (String error : directory.getErrors()) {
                System.err.println("ERROR: " + error);
            }
        }
    }

    private void print(Exception exception) {
        System.err.println("EXCEPTION: " + exception);
    }

    private void changefolderPathFlowPane(String[] s) {
        folderPathFlowPane.getChildren().clear();
        pathtext.clear();
        for (int i = 0; i < s.length; i++) {
            if (i != 0) {
                pathtext.add(new Text(">"));
                FlowPane.setMargin(pathtext.get(pathtext.size() - 1), new Insets(0, 5, 0, 0));
                folderPathFlowPane.getChildren().add(pathtext.get(pathtext.size() - 1));
            }
            pathtext.add(new Text(s[i]));
            FlowPane.setMargin(pathtext.get(pathtext.size() - 1), new Insets(0, 5, 0, 0));
            folderPathFlowPane.getChildren().add(pathtext.get(pathtext.size() - 1));

            pathtext.get(pathtext.size() - 1).setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    if (e.getButton().equals(MouseButton.PRIMARY)) {
                        if (e.getClickCount() == 2) {
                            String path = new String();
                            int j = 0;
                            while (!e.getSource().equals(pathtext.get(j))) {
                                path += pathtext.get(j).getText() + "\\";
                                j += 2;
                            }
                            path += pathtext.get(j).getText();
                            if (path.equals("D:") || path.equals("C:")) {
                                path += "\\";
                            }
                            folderTitledPane.setText(pathtext.get(j).getText());
                            j++;
                            while (j != folderPathFlowPane.getChildren().size()) {
                                folderPathFlowPane.getChildren().remove(j);
                                pathtext.remove(j);
                            }
                            folderTreeView.setRoot(new FolderItem(new File(path)));
                            folderTreeView.setShowRoot(false);
                            folderTreeView.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> showImagesInFolder(
                                    folderTreeView.getSelectionModel().getSelectedItems().get(0)));
                            folderTitledPane.setContent(folderTreeView);

                            addImageIntoFolder(new File(path));
                        }
                    }
                }
            });
        }
    }
}

class ImageFilePane extends BorderPane {
    private Image image;
    private String imageName;
    private File imagePath;

    public ImageFilePane(File imagePath) {
        this.setHeight(300);
        this.setWidth(200);
        setBorder(new Border(
                new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        try {
            this.image = new Image(new FileInputStream(imagePath), getWidth(), getHeight(), true, true);
        } catch (Exception e) {
            System.out.println("Unable to load image from " + imagePath);
        }

        if (this.image != null) {
            this.imageName = imagePath.getName();
            this.imagePath = imagePath;

            ImageView imageView = new ImageView(this.image);
            if (this.image.getHeight() > this.image.getWidth())
                imageView.setFitHeight(200);
            else
                imageView.setFitWidth(200);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setCache(true);

            Label label = new Label(this.imageName);
            label.setMinWidth(200);
            label.setMaxWidth(200);
            label.setWrapText(true);
            label.setTextAlignment(TextAlignment.CENTER);

            setCenter(imageView);
            setBottom(label);
        }
    }

    public File getImagePath() {
        return this.imagePath;
    }
}

class FolderItem extends TreeItem<File> {
    private boolean isFirstTimeChildren = true;
    private boolean isFirstTimeLeaf = true;
    private boolean isLeaf;

    public FolderItem(File rootPath) {
        super(rootPath);
    }

    public FolderItem() {
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
