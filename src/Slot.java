public class Slot {
    public String day; // Start day: "Monday", "Tuesday", or "Friday"
    public int startTime;
    public int endTime;
    public int max;
    public int min;
    //public boolean type; What this do?
    public boolean isSpecial;

    public Slot (String day, int startTime, int endTime, int max, int min, boolean isSpecial) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.max = max;
        this.min = min;
        //this.type = type;
        this.isSpecial = isSpecial;
    }
}
