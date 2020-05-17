package autoeditor;

import com.squareup.okhttp.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.ArrayList;

public class VideoIntelligenceResponseParser {
    public static ArrayList<TimeFrame> processVideo() throws IOException, InterruptedException {
        String inputFile = "videoplayback.mp4";
        String bucket = "videoattempt1";
        String object = "gommennasa.json";
        int responseCode = 404;
        Response getBucketItem;

        Response annotationOperation = postAnnotationRequest(inputFile, object);

        do {
            getBucketItem = getBucketResponse(bucket, object);
            responseCode = getBucketItem.code();
            Thread.sleep(5000);
        } while (responseCode != 200);


        String mediaLinkResponse = getBucketItem.body().string();

        JSONObject jsonObject = new JSONObject(mediaLinkResponse);

        String mediaLink = (String) jsonObject.get("mediaLink");

        Response labelResponse = getLabelResponse(mediaLink);
        String labelString = labelResponse.body().string();
        JSONObject labelJson = new JSONObject(labelString);

        return getTimeRanges(labelJson);
    }

    public static Response postAnnotationRequest(String inputFile, String outputFile) throws IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"inputUri\": \"gs://videoattempt1/"+inputFile+"\",\"outputUri\": \"gs://videoattempt1/"+outputFile+"\",\"features\": [\"LABEL_DETECTION\"]}");
        Request request = new Request.Builder()
                .url("https://videointelligence.googleapis.com/v1/videos:annotate?key=AIzaSyAuqtvuB_tpDRE3PqjoZ78lYwWK5ij7Wmc")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
         Response response = client.newCall(request).execute();
        return response;
    }

    public static Response getBucketResponse(String bucket, String object) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://storage.googleapis.com/storage/v1/b/"+bucket+"/o/"+object)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public static Response getLabelResponse(String mediaLink) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(mediaLink)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();

        return response;
    }

    public static ArrayList<TimeFrame> getTimeRanges(JSONObject jsonObject){
        ArrayList<TimeFrame> initialTimeRangeList = new ArrayList<>();
        ArrayList<TimeFrame> finalTimeRangeList = new ArrayList<>();
        JSONParser parser = new JSONParser();
        double REF_CONFIDENCE_LEVEL = 0.90;
        int FRAME_FACTOR = 5;

        try{
            JSONArray annotationResults = (JSONArray) jsonObject.get("annotation_results");
            JSONObject zeroIndexVal = (JSONObject) annotationResults.get(0);
            JSONArray shotLabelAnnotations = (JSONArray) zeroIndexVal.get("shot_label_annotations");

            for (int i = 0; i < shotLabelAnnotations.length(); i++) {
                JSONObject entityObject = (JSONObject) shotLabelAnnotations.get(i);
                JSONObject entityValue = (JSONObject) entityObject.get("entity");
                String entityDescription = (String) entityValue.get("description");

                if(checkValidEntity(entityDescription)){
                    JSONArray segments = (JSONArray) entityObject.get("segments");

                    for (int j = 0; j < segments.length(); j++) {
                        JSONObject segmentObject = (JSONObject) segments.get(j);
                        double confidence = (double) segmentObject.get("confidence");

                        if(confidence > REF_CONFIDENCE_LEVEL) {
                            JSONObject segment = (JSONObject) segmentObject.get("segment");

                            JSONObject startTimeObject = (JSONObject) segment.get("start_time_offset");
                            int startTime = (int) startTimeObject.get("seconds");

                            JSONObject endTimeObject = (JSONObject) segment.get("end_time_offset");
                            int endTime = (int) endTimeObject.get("seconds");

                            TimeFrame timeFrame = new TimeFrame(entityDescription, startTime, endTime, confidence);
                            initialTimeRangeList.add(timeFrame);
                        }
                    }
                }
            }

            JSONObject entireVideoSegment = (JSONObject) zeroIndexVal.get("segment");
            JSONObject entireVideoEndObject = (JSONObject) entireVideoSegment.get("end_time_offset");
            int entireVideoEnd = (int) entireVideoEndObject.get("seconds");
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

    //TODO - @t4w51f - Convert array indices from 1s differential to 5s
    private static ArrayList<TimeFrame> processTimeFrameList(ArrayList<TimeFrame> timeFramesList, int end){
        int[] timeFrameArray = new int[end + 1];
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

        int finalStart = end;
        int finalEnd = 0;

        for (int j = 0; j < timeFrameArray.length; j++) {

            //mark the start
            if(timeFrameArray[j] > 0 && finalStart == end){
                finalStart = j;
            }

            //mark the end
            if(timeFrameArray[j] == 0 && finalStart < j){
                finalEnd = j;
                TimeFrame timeFrame = new TimeFrame(finalStart, finalEnd);
                finalTimeFrameList.add(timeFrame);

                finalStart = end;
            }
        }

        return finalTimeFrameList;
    }
}
