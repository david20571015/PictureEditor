javac --module-path=$env:PATH_TO_FX -encoding UTF-8 --add-modules=javafx.fxml,javafx.swing,javafx.controls -cp "metadata-extractor-2.14.0.jar;." .\src\controller\*.java .\src\window\*.java .\src\operation\*.java
java --module-path=$env:PATH_TO_FX --add-modules=javafx.fxml,javafx.swing,javafx.controls -cp "metadata-extractor-2.14.0.jar;.;.\src\controller\;.\src\window\;.\src\operation\" src.window.PictureViewer

