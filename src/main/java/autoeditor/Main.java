package autoeditor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    private double xOfffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.initStyle(StageStyle.UNDECORATED);
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/AutoEditorUI.fxml"));
        primaryStage.setTitle("Video Highlights Generator");
        Scene scene = new Scene(root, 300, 275);
        //scene.getStylesheets().addAll(this.getClass().getResource("css/stylesheet.css").toExternalForm());

        //grab root and move window
        root.setOnMousePressed(event -> {
            xOfffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragEntered(event -> {
            primaryStage.setX(event.getScreenX() - xOfffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });
        primaryStage.setScene(scene);
        primaryStage.show();
        minimizeIconTray();
    }


    //FIXME
    private static void minimizeIconTray(){
        long lhwnd = com.sun.glass.ui.Window.getWindows().get(0).getNativeWindow();
        Pointer lpVoid = new Pointer(lhwnd);
        WinDef.HWND hwnd = new WinDef.HWND(lpVoid);
        final User32 user32 = User32.INSTANCE;
        int oldStyle = user32.GetWindowLong(hwnd, GWL_STYLE);
        int newStyle = oldStyle | 0x00020000;//WS_MINIMIZEBOX
        user32.SetWindowLong(hwnd, GWL_STYLE, newStyle);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
