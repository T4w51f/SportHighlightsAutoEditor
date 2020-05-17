package autoeditor;

import com.squareup.okhttp.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class VideoIntelligenceResponseParser {
    public static ArrayList<TimeFrame> processVideo(String AbsoluteInputFilePath, String inputFileName, String storageFileName) throws IOException, InterruptedException {
        System.out.println("Initiated video processing");

        String bucket = "videoattempt1";
        String hashedStorageName = storageFileName + (AbsoluteInputFilePath + storageFileName).hashCode() + String.valueOf(Math.round(999999 * Math.random()));
        int responseCode = 404;
        Response getBucketItem;

        uploadInputVideo(AbsoluteInputFilePath, bucket, hashedStorageName);

        System.out.println("Uploading input video:");

        do {
            System.out.print(".");
            getBucketItem = getBucketResponse(bucket, hashedStorageName, ".mp4");
            responseCode = getBucketItem.code();
            Thread.sleep(5000);
        } while (responseCode != 200);

        System.out.println("");
        System.out.println("Upload complete!");

        Response annotationOperation = postAnnotationRequest(inputFileName, hashedStorageName);

        System.out.println("Checking for annotated JSON on cloud storage:");

        do {
            System.out.print(".");
            getBucketItem = getBucketResponse(bucket, hashedStorageName, ".json");
            responseCode = getBucketItem.code();
            Thread.sleep(5000);
        } while (responseCode != 200);

        System.out.println("");
        System.out.println("JSON file found!");


        String mediaLinkResponse = getBucketItem.body().string();

        JSONObject jsonObject = new JSONObject(mediaLinkResponse);

        String mediaLink = (String) jsonObject.get("mediaLink");

        Response labelResponse = getLabelResponse(mediaLink);
        String labelString = labelResponse.body().string();
        JSONObject labelJson = new JSONObject(labelString);

        return getTimeRanges(labelJson);
    }

    public static void uploadInputVideo(String AbsoluteInputPath, String bucket, String storageFileName) throws IOException {
        System.out.println("Uploading user input video to cloud storage");

        String command =
                "curl -X POST --data-binary @"+AbsoluteInputPath+" -H \"Content-Type: video/mp4\" \"https://storage.googleapis.com/upload/storage/v1/b/"+bucket+"/o?uploadType=media&name="+storageFileName+".mp4\"";

        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        processBuilder.directory(new File("C:\\Users\\"));
        Process process = processBuilder.start();
        InputStream inputStream = process.getInputStream();

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        System.out.println(result.toString(StandardCharsets.UTF_8.name()));
    }

    public static Response postAnnotationRequest(String inputFile, String outputFile) throws IOException {
        System.out.println("Requesting Google Video Intelligence API to process the input video");

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"inputUri\": \"gs://videoattempt1/"+inputFile+"\",\"outputUri\": \"gs://videoattempt1/"+outputFile+".json\",\"features\": [\"LABEL_DETECTION\"]}");
        Request request = new Request.Builder()
                .url("https://videointelligence.googleapis.com/v1/videos:annotate?key=AIzaSyAuqtvuB_tpDRE3PqjoZ78lYwWK5ij7Wmc")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
         Response response = client.newCall(request).execute();
        return response;
    }

    public static Response getBucketResponse(String bucket, String object, String fileExtension) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://storage.googleapis.com/storage/v1/b/"+bucket+"/o/"+object+fileExtension)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public static Response getLabelResponse(String mediaLink) throws IOException {
        System.out.println("Collecting the annotated time frame results from the video analysis");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(mediaLink)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();

        return response;
    }

    public static ArrayList<TimeFrame> getTimeRanges(JSONObject jsonObject){
        double REF_CONFIDENCE_LEVEL = 0.90;
        System.out.println("Processing relevant time frames");
        System.out.println("Confidence floor is set to " + String.valueOf(REF_CONFIDENCE_LEVEL));

        ArrayList<TimeFrame> initialTimeRangeList = new ArrayList<>();
        ArrayList<TimeFrame> finalTimeRangeList = new ArrayList<>();
        JSONParser parser = new JSONParser();
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

        System.out.println("Returning time frame list for video modification");
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
