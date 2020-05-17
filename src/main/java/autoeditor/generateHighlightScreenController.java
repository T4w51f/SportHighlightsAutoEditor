package autoeditor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class generateHighlightScreenController {
    private boolean DEBUG = true;

    @FXML
    private Label highlightGenerator;
    @FXML
    private BorderPane thirdPageBorderPane;

    private boolean highlightButtonFlag;

    @FXML
    private void initialize(){
        highlightButtonFlag = false;

        videoGenScreen();
    }
    @FXML
    public void generateHighlight(javafx.scene.input.MouseEvent event) {
        Label labelClicked = (Label) event.getSource();
        if (DEBUG) { System.out.println("Video has been generated");}
        highlightButtonFlag = true;
    }

    private void videoGenScreen() {
        //load videogeneratedscreen fxml onto a view
        FXMLLoader emptyLoader = new FXMLLoader(getClass().getResource("../FXML/videoGeneratedScreen.fxml"));
        Pane pane = null;
        try {
            pane = emptyLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        thirdPageBorderPane.setCenter(pane);
    }


}
