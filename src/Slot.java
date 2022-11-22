public class Slot {
    public String day;
    public int startTime;
    public int endTime;
    public int max;
    public int min;
    public boolean type;
    public boolean isSpecial;

    public Slot (String day, int startTime, int endTime, int max, int min, boolean type, boolean isSpecial) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.max = max;
        this.min = min;
        this.type = type;
        this.isSpecial = isSpecial;
    }
}
