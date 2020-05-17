package autoeditor;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import lombok.Getter;

import java.io.File;


public class AutoEditorUIController {
    private boolean DEBUG = true;


    @Getter private static File outDir;

    @Getter private static File inFile;

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
        if (DEBUG) { System.out.println("A file is being selected as an input");}
        final FileChooser fc = new FileChooser();
        inFile = fc.showOpenDialog(Main.getPrimaryStage());
        if (inFile != null) {
            if (DEBUG) { System.out.println("A file has been selected as an input");}
            inputFileFlag = true;
            if (outputFilePathFlag) generateHighLightScreen();
        }
    }

    @FXML
    private void selectOutputFilePath(MouseEvent event) {
        event.getSource();
        if (DEBUG) { System.out.println("A file path is being selected as an output");}
        final DirectoryChooser dc = new DirectoryChooser();
        outDir = dc.showDialog(Main.getPrimaryStage());
        if (outDir != null){
            if (DEBUG) { System.out.println("A file has been selected as an input"); }
            outputFilePathFlag = true;
            if (inputFileFlag) generateHighLightScreen();
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
