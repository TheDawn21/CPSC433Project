public class Event {
    // name = "CSMA U13T3 DIV 01"
    public String name;
    public String org;
    public String age;
    public String tier;
    public int div;
    public boolean type; // game or practice

    public Event(String name , String org, String age, String tier, int div, boolean type) {
        this.name = name;
        this.org = org;
        this.age = age;
        this.tier = tier;
        this.div = div;
        this.type = type;
    }
}
