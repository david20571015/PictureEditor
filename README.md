# PictureEditor

## Compile
```sh
$ javac --module-path=$env:PATH_TO_FX -encoding UTF-8 --add-modules=javafx.fxml,javafx.swing,javafx.controls -cp "metadata-extractor-2.14.0.jar;." .\src\controller\*.java .\src\window\*.java .\src\operation\*.java
```

## Execute
```sh
$ java --module-path=$env:PATH_TO_FX --add-modules=javafx.fxml,javafx.swing,javafx.controls -cp "metadata-extractor-2.14.0.jar;.;.\src\controller\;.\src\window\;.\src\operation\" src.window.PictureViewer
```

## PictureViewerWindow
| Action | Do |
| ---- | ---- |
| File > Open | Choose the Folder be opened |
| Right-click the folder and click "add to favorite" | Add the folder to favorite |
| Click the image | Show details of the image |
| Double-click the image | Open a ImageWindow to edit the image |

## ImageWindow
| Action | Do |
| ---- | ---- |
| Move the mouse in the image | Show information of the pixel | 
| Drag the image by right mouse button | Drag the image |
| Scroll the mouse wheel | Zoom in/out |
| Chick button in Filter area | Apply the filter on the image |
| Drag the mouse by left mouse button with the "Painting" checked box selected | Draw line on the image |
| Drag the mouse by left mouse button without the "Painting" checked box selected | Draw a shape on the image |
| Click "Add New Layer" button | Add a new space layer |
| Click "Delete" button | Delete that layer |
| Click the layer | Select active layer | 
| Edit > Undo | Recovery last action |
| File > Save | Save the image |

## Reference
* [https://docs.oracle.com/en/java/javase/13/docs/api/index.html](https://docs.oracle.com/en/java/javase/13/docs/api/index.html)
* [https://github.com/drewnoakes/metadata-extractor](https://github.com/drewnoakes/metadata-extractor)
* [https://www.itread01.com/](https://www.itread01.com/)
* [https://www.youtube.com/watch?v=Xj1s8dT9cTE](https://www.youtube.com/watch?v=Xj1s8dT9cTE)
* [https://www.coder.work/article/5556058](https://www.coder.work/article/5556058)
* [https://www.itread01.com/content/1546273296.html](https://www.itread01.com/content/1546273296.html)

