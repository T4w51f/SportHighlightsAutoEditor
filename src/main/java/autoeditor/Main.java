package autoeditor;

import com.sun.glass.ui.Window;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.sun.jna.platform.win32.WinUser.GWL_STYLE;

public class Main extends Application {
    private static AnchorPane root;
    private static FXMLLoader loader;
    private static List<Pane> paneList = new ArrayList<>();

    private double xOffset = 0;
    private double yOffset = 0;
    private static int current_page = 0;
    @Getter
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Main.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.UNDECORATED);
        loader = new FXMLLoader(getClass().getResource("/fxml/ParentPane.fxml"));
        paneList.add(FXMLLoader.load(getClass().getResource("/fxml/AutoEditorUI.fxml")));
        paneList.add(FXMLLoader.load(getClass().getResource("/fxml/generateHighlightScreen.fxml")));
        paneList.add(FXMLLoader.load(getClass().getResource("/fxml/videoGeneratedScreen.fxml")));
        root = loader.load();
        root.getChildren().add(paneList.get(0));
        primaryStage.setTitle("Video Highlights Generator");
        Scene scene = new Scene(root, 382, 401);
        scene.getStylesheets().addAll(this.getClass().getResource("/css/stylesheet.css").toExternalForm());

        //grab root and move window
        root.setOnMousePressed(pressEvent -> root.setOnMouseDragged(dragEvent -> {
            primaryStage.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
            primaryStage.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
        }));


        primaryStage.setScene(scene);
        primaryStage.show();
        minimizeIconTray();
    }

    public static void set_pane(int page){
        root.getChildren().remove(paneList.get(current_page));
        root.getChildren().add(paneList.get(page));
        current_page = page;
    }

    private static void minimizeIconTray(){
        long lhwnd = Window.getWindows().get(0).getNativeWindow();
        Pointer lpVoid = new Pointer(lhwnd);
        WinDef.HWND hwnd = new WinDef.HWND(lpVoid);
        final User32 user32 = User32.INSTANCE;
        int oldStyle = user32.GetWindowLong(hwnd, GWL_STYLE);
        int newStyle = oldStyle | 0x00020000;//WS_MINIMIZEBOX
        user32.SetWindowLong(hwnd, GWL_STYLE, newStyle);
    }

    public static void main(String[] args) throws IOException {
        //launch(args);
        //TODO: Ahnaf run this
        VideoIntelligenceResponseParser.gcpVidTool();
    }
}
