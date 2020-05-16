package autoeditor;

public class TimeFrame {
    private String entityDescription;
    private double startTime;
    private double endTime;
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

    public String getEntityDescription() {
        return entityDescription;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public double length() {
        return endTime - startTime;
    }

    public double getConfidence() {
        return confidence;
    }
}
