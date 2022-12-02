import java.util.ArrayList;
import java.util.HashMap;

public class Schedule {
    public ArrayList<Event> gamesLeft;
    public ArrayList<Event> pracsLeft;
    // give slot => output events
    public HashMap<Slot, ArrayList<Event>> eventsMap;
    // give element => output slot
    public HashMap<Event, Slot> slotsMap;
    public int score;

    public Schedule(ArrayList<Event> gamesLeft, ArrayList<Event> pracsLeft, HashMap<Slot, ArrayList<Event>> eventsMap, HashMap<Event, Slot> slotsMap, int score) {
        this.gamesLeft = gamesLeft;
        this.pracsLeft = pracsLeft;
        this.eventsMap = eventsMap;
        this.slotsMap = slotsMap;
        this.score = score;
    }

    // copy constructor
    public Schedule (Schedule sched) {
        this.gamesLeft = new ArrayList<>(sched.gamesLeft);
        this.pracsLeft = new ArrayList<>(sched.pracsLeft);
        this.eventsMap = new HashMap<>(sched.eventsMap);
        this.slotsMap = new HashMap<>(sched.slotsMap);
        this.score = sched.score;
    }
}
