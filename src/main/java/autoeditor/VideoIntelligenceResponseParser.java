package autoeditor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class VideoIntelligenceResponseParser {
    public static ArrayList<TimeFrame> getTimeRanges(){
        ArrayList<TimeFrame> timeRangeList = new ArrayList<>();
        JSONParser parser = new JSONParser();
        double REF_CONFIDENCE_LEVEL = 0.90;

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

                            TimeFrame timeFrame = new TimeFrame(startTime, endTime);
                            timeRangeList.add(timeFrame);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(timeRangeList);
        return timeRangeList;
    }

    private static boolean checkValidEntity(String entityDescription) {
        return entityDescription.toLowerCase().contains("goal") ||
                entityDescription.toLowerCase().contains("yellow") ||
                entityDescription.toLowerCase().contains("red");
    }
}
