package autoeditor;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;


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

    private boolean inputFileFlag;
    private boolean outputFilePathFlag;

    @FXML
    private void initialize(){
        inputFileFlag = false;
        outputFilePathFlag = false;
    }

    @FXML
    public void selectInputFile(MouseEvent event) {
        event.getSource();
        if (DEBUG) { System.out.println("A file has been selected as an input");}
        inputFileFlag = true;
        if (outputFilePathFlag){
            generateHighLightScreen();
        }
    }

    @FXML
    private void selectOutputFilePath(MouseEvent event) {
        event.getSource();
        if (DEBUG) { System.out.println("A file path has been selected as an output");}
        outputFilePathFlag = true;
        if (inputFileFlag){
            generateHighLightScreen();
        }
    }

    @FXML
    private void minimizeWindow() {
        Main.getPrimaryStage().setIconified(true);
    }
    @FXML
    private void closeWindow() {
        System.exit(0);
    }

    private void generateHighLightScreen() {
        //load generateHighlightScreen fxml onto a view
        inputFileFlag = false;
        outputFilePathFlag = false;
        Main.set_pane(1);
    }
}
