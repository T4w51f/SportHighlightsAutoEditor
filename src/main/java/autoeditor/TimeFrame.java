package autoeditor;

import lombok.Getter;

public class TimeFrame {
    private String entityDescription;
    @Getter private double startTime;
    @Getter private double endTime;
    private double confidence;

    public TimeFrame(String entityDescription, String startTime, String endTime, double confidence){
        String stringDoubleStart = startTime.replace("s", "");
        String stringDoubleEnd = endTime.replace("s", "");

        this.entityDescription = entityDescription;
        this.startTime = Double.parseDouble(stringDoubleStart);
        this.endTime = Double.parseDouble(stringDoubleEnd);
        this.confidence = confidence;
    }

    public TimeFrame(int startTime, int endTime){
        this.entityDescription = "";
        this.startTime = (double) startTime;
        this.endTime = (double) endTime;
        this.confidence = 0;
    }
}
