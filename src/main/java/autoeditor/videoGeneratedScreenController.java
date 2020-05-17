package autoeditor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.IOException;

public class videoGeneratedScreenController {
    private boolean DEBUG = true;

    @FXML
    private ImageView minButton;
    @FXML
    private ImageView closeButton;
    @FXML
    private Text returnFileUpload;


    @FXML
    public void returnToFileUpload(MouseEvent event) {
        Text textClicked = (Text) event.getSource();
        if (DEBUG) { System.out.println("Going back to file select");}
        returnScreen();
    }
    private void returnScreen() {
        //load autoeditorUI fxml onto a view
        Main.set_pane(0);
    }

    @FXML
    private void minimizeWindow() {
        Main.getPrimaryStage().setIconified(true);
    }

    @FXML
    private void closeWindow() {
        System.exit(0);
    }

}
