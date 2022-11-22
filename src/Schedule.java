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

        // TODO: replace with eval
        this.score = score;
    }
}
