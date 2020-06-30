javac --module-path=$env:PATH_TO_FX -encoding UTF-8 --add-modules=javafx.fxml,javafx.swing,javafx.controls -cp "metadata-extractor-2.14.0.jar;." .\src\resource\*.java .\src\window\*.java .\src\*.java 
java --module-path=$env:PATH_TO_FX --add-modules=javafx.fxml,javafx.swing,javafx.controls -cp "metadata-extractor-2.14.0.jar;.;.\src\resource\;.\src\window\;.\src\" src.PictureViewer

