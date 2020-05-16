package autoeditor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;

public class VideoIntelligenceResponseParser {
    public static ArrayList<TimeFrame> getTimeRanges(){
        ArrayList<TimeFrame> initialTimeRangeList = new ArrayList<>();
        ArrayList<TimeFrame> finalTimeRangeList = new ArrayList<>();
        JSONParser parser = new JSONParser();
        double REF_CONFIDENCE_LEVEL = 0.97;

        try{
            Object obj = parser.parse(new FileReader("D:\\SportHighlightsAutoEditor\\src\\main\\resources\\annotations.json"));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray annotationResults = (JSONArray) jsonObject.get("annotationResults");
            JSONObject zeroIndexVal = (JSONObject) annotationResults.get(0);
            JSONArray shotLabelAnnotations = (JSONArray) zeroIndexVal.get("shotLabelAnnotations");

            for (int i = 0; i < shotLabelAnnotations.size(); i++) {
                JSONObject entityObject = (JSONObject) shotLabelAnnotations.get(i);
                JSONObject entityValue = (JSONObject) entityObject.get("entity");
                String entityDescription = (String) entityValue.get("description");

                if(checkValidEntity(entityDescription)){
                    JSONArray segments = (JSONArray) entityObject.get("segments");

                    for (int j = 0; j < segments.size(); j++) {
                        JSONObject segmentObject = (JSONObject) segments.get(j);
                        double confidence = (double) segmentObject.get("confidence");

                        if(confidence > REF_CONFIDENCE_LEVEL) {
                            JSONObject segment = (JSONObject) segmentObject.get("segment");
                            String startTime = (String) segment.get("startTimeOffset");
                            String endTime = (String) segment.get("endTimeOffset");

                            TimeFrame timeFrame = new TimeFrame(entityDescription, startTime, endTime, confidence);
                            initialTimeRangeList.add(timeFrame);
                        }
                    }
                }
            }

            JSONObject entireVideoSegment = (JSONObject) zeroIndexVal.get("segment");
            String entireVideoEnd = (String) entireVideoSegment.get("endTimeOffset");
            finalTimeRangeList = processTimeFrameList(initialTimeRangeList, entireVideoEnd);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return finalTimeRangeList;
    }

    private static boolean checkValidEntity(String entityDescription) {
        return entityDescription.toLowerCase().contains("goal") ||
                entityDescription.toLowerCase().contains("score") ||
                entityDescription.toLowerCase().contains("yellow") ||
                entityDescription.toLowerCase().contains("red");
    }

    private static ArrayList<TimeFrame> processTimeFrameList(ArrayList<TimeFrame> timeFramesList, String end){
        double endTimeOffset = Double.parseDouble(end.replace("s", ""));
        int endTimeCeiling = (int) Math.ceil(endTimeOffset);
        int[] timeFrameArray = new int[endTimeCeiling + 1];
        ArrayList<TimeFrame> finalTimeFrameList = new ArrayList<>();

        for(TimeFrame timeFrame : timeFramesList){
            int frameStart = (int) Math.ceil(timeFrame.getStartTime());
            int frameEnd = (int) Math.ceil(timeFrame.getEndTime());
            timeFrameArray[frameStart]++;
            timeFrameArray[frameEnd]--;
        }

        for(int i = 1; i < timeFrameArray.length; i++){
            timeFrameArray[i] = timeFrameArray[i - 1] + timeFrameArray[i];
        }

        int finalStart = endTimeCeiling;
        int finalEnd = 0;

        for (int j = 0; j < timeFrameArray.length; j++) {

            //mark the start
            if(timeFrameArray[j] > 0 && finalStart == endTimeCeiling){
                finalStart = j;
            }

            //mark the end
            if(timeFrameArray[j] == 0 && finalStart < j){
                finalEnd = j;
                TimeFrame timeFrame = new TimeFrame(finalStart, finalEnd);
                finalTimeFrameList.add(timeFrame);

                finalStart = endTimeCeiling;
            }
        }

        return finalTimeFrameList;
    }
}
