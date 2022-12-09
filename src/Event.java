public class Event {
    // name = "CSMA U13T3 DIV 01"
    public String name;
    public String org;
    public String age;
    public String tier;
    public int div;
    public boolean type; // game = true | practice = false
    // id such as game1, so it's easier to debug
    public String id;

    // Constructor
    public Event(String name , String org, String age, String tier, int div, boolean type, String id) {
        this.name = name;
        this.org = org;
        this.age = age;
        this.tier = tier;
        this.div = div;
        this.type = type;
        this.id = id;
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

                    // for practice without div
                    if (this.div == 0 || event.div == 0) {
                        same = true;
                    }
                }
            }
        }

        return same;
    }
}
