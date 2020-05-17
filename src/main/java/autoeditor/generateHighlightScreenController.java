package autoeditor;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.ArrayList;

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

        File outDir = AutoEditorUIController.getOutDir();
        File inFile = AutoEditorUIController.getInFile();

        String fileNameNoExtension = inFile.getName().substring(0,inFile.getName().lastIndexOf('.'));
        String fileExtension =inFile.getName().substring(inFile.getName().lastIndexOf('.'));

        String outputPath = outDir+"\\"+fileNameNoExtension+"_highlights"+fileExtension;

        try{
            ArrayList<TimeFrame> vidTimeFrames = VideoIntelligenceResponseParser.processVideo(inFile.getAbsolutePath() ,inFile.getName(), inFile.getName());
            videoEditor.videoEditor.createHighlights(inFile.getAbsolutePath(), outputPath, vidTimeFrames);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not parse video");
        }

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
