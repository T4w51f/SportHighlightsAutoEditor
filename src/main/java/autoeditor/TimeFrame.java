package autoeditor;

import lombok.Getter;

public class TimeFrame {
    private String entityDescription;
    @Getter private int startTime;
    @Getter private int endTime;
    private double confidence;

    public TimeFrame(String entityDescription, int startTime, int endTime, double confidence){
        this.entityDescription = entityDescription;
        this.startTime = startTime;
        this.endTime = endTime;
        this.confidence = confidence;
    }

    public TimeFrame(int startTime, int endTime){
        this.entityDescription = "";
        this.startTime = startTime;
        this.endTime = endTime;
        this.confidence = 0;
    }
}
