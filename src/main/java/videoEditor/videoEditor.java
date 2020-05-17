package videoEditor;

import javafx.util.Pair;
import ws.schild.jave.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class videoEditor {

    public static int createHighlights(String filepath, String outputName, List<Pair<Integer, Integer>> sequences) {

        DefaultFFMPEGLocator df = new DefaultFFMPEGLocator();

        String inputFilePath = filepath;

        File in = new File(filepath);

        String outputFilePath = in.getParent()+"\\"+outputName+filepath.substring(filepath.indexOf("."));

        ArrayList<String> cmd = new ArrayList<>();
        cmd.add(df.getFFMPEGExecutablePath());
        cmd.add("-i");
        cmd.add(inputFilePath);
        cmd.add("-filter_complex");

        StringBuilder filterComplex = new StringBuilder("\"");
        StringBuilder voutStatement = new StringBuilder();
        StringBuilder aoutStatement = new StringBuilder();

        for(int i = 0; i < sequences.size(); i++) {
            String startVal = sequences.get(i).getKey().toString();
            String endVal = sequences.get(i).getValue().toString();
            String vNumber = "v"+String.valueOf(i);
            String aNumber = "a"+String.valueOf(i);

            String vStatement = "[0:v]trim=start="+startVal+":end="+endVal+",setpts=PTS-STARTPTS["+vNumber+"];";
            String aStatement = "[0:a]atrim=start="+startVal+":end="+endVal+",asetpts=PTS-STARTPTS["+aNumber+"];";

            filterComplex.append(vStatement);
            filterComplex.append(aStatement);

            voutStatement.append("["+vNumber+"]");
            aoutStatement.append("["+aNumber+"]");
        }

        voutStatement.append("concat=n="+sequences.size()+":v=1[vout];");
        aoutStatement.append("concat=n="+sequences.size()+":v=0:a=1[aout]\"");

        filterComplex.append(voutStatement);
        filterComplex.append(aoutStatement);


        cmd.add(filterComplex.toString());
        cmd.add("-map");
        cmd.add("[vout]");
        cmd.add("-map");
        cmd.add("[aout]");
        cmd.add(outputFilePath);

        int errorStatus = startFFMPEG(cmd);

        return errorStatus;
    }

    private static int startFFMPEG(ArrayList<String> cmd) {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        int errorStatus = 1;
        try {
            pb.redirectErrorStream(true);
            Process p = pb.start();

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // Read the output from the command
            System.out.println("Here is the standard output of the command:\n");
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // Read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
                errorStatus=-1;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR: FAILED");
            errorStatus = -1;
        }

        return errorStatus;
    }
}
