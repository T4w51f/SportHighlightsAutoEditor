package autoeditor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.IOException;

public class videoGeneratedScreenController {
    private boolean DEBUG = true;
    @FXML
    private Text returnFileUpload;
    @FXML
    private BorderPane firstPageBorderPane;

    @FXML
    public void returnToFileUpload(MouseEvent event) {
        javafx.scene.control.Label labelClicked = (Label) event.getSource();
        if (DEBUG) { System.out.println("Going back to file select");}
        returnScreen();
    }
    private void returnScreen() {
        //load autoeditorUI fxml onto a view
        FXMLLoader emptyLoader = new FXMLLoader(getClass().getResource("../FXML/AutoEditorUI.fxml"));
        Pane pane = null;
        try {
            pane = emptyLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        firstPageBorderPane.setCenter(pane);
    }
}
