package autoeditor;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

import java.awt.*;


public class AutoEditorController {
    private boolean DEBUG = true;
    @FXML
    private ImageView minButton;
    @FXML
    private ImageView closeButton;
    @FXML
    private Label addFile;
    @FXML
    private Label outputFileSelect;

    @FXML
    private void initialize(){

    }

    @FXML
    private void selectInputFile(Event event) {
        Label labelClicked = (Label) event.getSource();
        if (DEBUG) {System.out.println("A file has been selected as an input");}
    }

    @FXML
    private void selectOutputFilePath(Event event) {
        Label labelClicked = (Label) event.getSource();
        if (DEBUG) {System.out.println("A file path has been selected as an output");}
    }

    @FXML
    private void minimizeWindow() {
        //x`Main.getPrimaryStage().setIconified(true);
    }
    @FXML
    private void closeWindow() {
        System.exit(0);
    }
}
