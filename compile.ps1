javac --module-path=$env:PATH_TO_FX -encoding UTF-8 --add-modules=javafx.fxml,javafx.swing,javafx.controls .\src\resource\*.java .\src\window\*.java .\src\*.java 
java --module-path=$env:PATH_TO_FX --add-modules=javafx.fxml,javafx.swing,javafx.controls src.PictureViewer

