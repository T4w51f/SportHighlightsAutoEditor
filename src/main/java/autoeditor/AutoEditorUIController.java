package autoeditor;

import javafx.fxml.FXML;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;


import java.awt.*;
import java.io.IOException;


public class AutoEditorUIController {
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
    private BorderPane diffPane;

    private boolean inputFileFlag;
    private boolean outputFilePathFlag;

    @FXML
    private void initialize(){
        inputFileFlag = false;
        outputFilePathFlag = false;

        generateHighLightScreen();
    }

    @FXML
    public void selectInputFile(MouseEvent event) {
        Label labelClicked = (Label) event.getSource();
        if (DEBUG) { System.out.println("A file has been selected as an input");}
        inputFileFlag = true;
    }

    @FXML
    private void selectOutputFilePath(MouseEvent event) {
        Label labelClicked = (Label) event.getSource();
        if (DEBUG) { System.out.println("A file path has been selected as an output");}
        outputFilePathFlag = true;
    }

    @FXML
    private void minimizeWindow() {
        //Main.getPrimaryStage().setIconified(true);
    }
    @FXML
    private void closeWindow() {
        System.exit(0);
    }

    private void generateHighLightScreen() {
        //load generateHighlightScreen fxml onto a view
        FXMLLoader emptyLoader = new FXMLLoader(getClass().getResource("../FXML/generateHighlightScreen.fxml"));
        Pane pane = null;
        try {
            pane = emptyLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        diffPane.setCenter(pane);
    }
}
