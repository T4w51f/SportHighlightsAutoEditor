package autoeditor;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.videointelligence.v1.*;

public class GCPVideoIntelligenceAPI {
    public static void getVideoIntelligenceResponse(String gsBucketUri) throws Exception {
        try (VideoIntelligenceServiceClient client = VideoIntelligenceServiceClient.create()) {
            AnnotateVideoRequest request = AnnotateVideoRequest.newBuilder()
                    .setInputUri(gsBucketUri)
                    .addFeatures(Feature.LABEL_DETECTION)
                    .build();

            OperationFuture<AnnotateVideoResponse, AnnotateVideoProgress> response =
                    client.annotateVideoAsync(request);


        }
    }
}
