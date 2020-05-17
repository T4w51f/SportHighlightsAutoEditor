package autoeditor;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

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
        event.getSource();
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
