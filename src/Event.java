public class Event {
    // name = "CSMA U13T3 DIV 01"
    public String name;
    public String org;
    public String age;
    public String tier;
    public int div;
    public boolean type; // game = true | practice = false

    // Constructor
    public Event(String name , String org, String age, String tier, int div, boolean type) {
        this.name = name;
        this.org = org;
        this.age = age;
        this.tier = tier;
        this.div = div;
        this.type = type;
    }

    
    // Check if another Event is in the same division
    // Used in Eval.practiceIntersect()
    public boolean sameDiv(Event event) {
        boolean same = false;

        if (this.org.equals(event.org)) {
            if (this.age.equals(event.age)) {
                if (this.tier.equals(event.tier)) {
                    if (this.div == event.div)
                        same = true;
                }
            }
        }

        return same;
    }
}
