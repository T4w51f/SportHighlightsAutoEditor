package autoeditor;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.videointelligence.v1.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class VideoIntelligenceResponseParser {
    public static void gcpVidTool() throws IOException {
        authImplicit();
        // Instantiate a com.google.cloud.videointelligence.v1.VideoIntelligenceServiceClient
        try (VideoIntelligenceServiceClient client = VideoIntelligenceServiceClient.create()) {
            // Provide path to file hosted on GCS as "gs://bucket-name/..."
            AnnotateVideoRequest request = AnnotateVideoRequest.newBuilder()
                    .setInputUri("gs://videoattempt1/messi.mp4")
                    .addFeatures(Feature.LABEL_DETECTION)
                    .build();
            // Create an operation that will contain the response when the operation completes.
            OperationFuture<AnnotateVideoResponse, AnnotateVideoProgress> response =
                    client.annotateVideoAsync(request);

            System.out.println("Waiting for operation to complete...");
            for (VideoAnnotationResults results : response.get().getAnnotationResultsList()) {
                // process video / segment level label annotations
                System.out.println("Locations: ");
                for (LabelAnnotation labelAnnotation : results.getSegmentLabelAnnotationsList()) {
                    System.out
                            .println("Video label: " + labelAnnotation.getEntity().getDescription());
                    // categories
                    for (Entity categoryEntity : labelAnnotation.getCategoryEntitiesList()) {
                        System.out.println("Video label category: " + categoryEntity.getDescription());
                    }
                    // segments
                    for (LabelSegment segment : labelAnnotation.getSegmentsList()) {
                        double startTime = segment.getSegment().getStartTimeOffset().getSeconds()
                                + segment.getSegment().getStartTimeOffset().getNanos() / 1e9;
                        double endTime = segment.getSegment().getEndTimeOffset().getSeconds()
                                + segment.getSegment().getEndTimeOffset().getNanos() / 1e9;
                        System.out.printf("Segment location: %.3f:%.3f\n", startTime, endTime);
                        System.out.println("Confidence: " + segment.getConfidence());
                    }
                }

                // process shot label annotations
                for (LabelAnnotation labelAnnotation : results.getShotLabelAnnotationsList()) {
                    System.out
                            .println("Shot label: " + labelAnnotation.getEntity().getDescription());
                    // categories
                    for (Entity categoryEntity : labelAnnotation.getCategoryEntitiesList()) {
                        System.out.println("Shot label category: " + categoryEntity.getDescription());
                    }
                    // segments
                    for (LabelSegment segment : labelAnnotation.getSegmentsList()) {
                        double startTime = segment.getSegment().getStartTimeOffset().getSeconds()
                                + segment.getSegment().getStartTimeOffset().getNanos() / 1e9;
                        double endTime = segment.getSegment().getEndTimeOffset().getSeconds()
                                + segment.getSegment().getEndTimeOffset().getNanos() / 1e9;
                        System.out.printf("Segment location: %.3f:%.3f\n", startTime, endTime);
                        System.out.println("Confidence: " + segment.getConfidence());
                    }
                }

                // process frame label annotations
                for (LabelAnnotation labelAnnotation : results.getFrameLabelAnnotationsList()) {
                    System.out
                            .println("Frame label: " + labelAnnotation.getEntity().getDescription());
                    // categories
                    for (Entity categoryEntity : labelAnnotation.getCategoryEntitiesList()) {
                        System.out.println("Frame label category: " + categoryEntity.getDescription());
                    }
                    // segments
                    for (LabelSegment segment : labelAnnotation.getSegmentsList()) {
                        double startTime = segment.getSegment().getStartTimeOffset().getSeconds()
                                + segment.getSegment().getStartTimeOffset().getNanos() / 1e9;
                        double endTime = segment.getSegment().getEndTimeOffset().getSeconds()
                                + segment.getSegment().getEndTimeOffset().getNanos() / 1e9;
                        System.out.printf("Segment location: %.3f:%.2f\n", startTime, endTime);
                        System.out.println("Confidence: " + segment.getConfidence());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void authImplicit() {
        // If you don't specify credentials when constructing the client, the client library will
        // look for credentials via the environment variable GOOGLE_APPLICATION_CREDENTIALS.
        Storage storage = StorageOptions.getDefaultInstance().getService();

        System.out.println("Buckets:");
        Page<Bucket> buckets = storage.list();
        for (Bucket bucket : buckets.iterateAll()) {
            System.out.println(bucket.toString());
        }
    }


    public static ArrayList<TimeFrame> getTimeRanges(){
        ArrayList<TimeFrame> initialTimeRangeList = new ArrayList<>();
        ArrayList<TimeFrame> finalTimeRangeList = new ArrayList<>();
        JSONParser parser = new JSONParser();
        double REF_CONFIDENCE_LEVEL = 0.97;
        int FRAME_FACTOR = 5;

        try{
            Object obj = parser.parse(new FileReader("SportHighlightsAutoEditor\\src\\main\\resources\\annotations.json"));
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

    //TODO - @t4w51f - Convert array indices from 1s differential to 5s
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
