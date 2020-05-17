package autoeditor;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class generateHighlightScreenController {
    private boolean DEBUG = true;

    @FXML
    private ImageView minButton;
    @FXML
    private ImageView closeButton;
    @FXML
    private Label highlightGenerator;

    private boolean highlightButtonFlag = false;

    @FXML
    private void initialize(){
        highlightButtonFlag = false;
    }
    @FXML
    public void generateHighlight(javafx.scene.input.MouseEvent event) {
        event.getSource();
        if (DEBUG) { System.out.println("Video has been generated");}
        highlightButtonFlag = true;
        videoGenScreen();
    }

    private void videoGenScreen() {
        //load videogeneratedscreen fxml onto a view
        Main.set_pane(2);
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
