import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TreeSearch {
    Parser input;
    Schedule bestSched;

    public TreeSearch(Parser input, ArrayList<Slot> slots) {
        this.input = input;
        
        // Initialize root node
        ArrayList<Event> gameLeft = input.games;
        ArrayList<Event> pracsLeft = input.practices;
        HashMap<Slot, ArrayList<Event>> eventsMap = new HashMap<>();
        HashMap<Event, Slot> slotsMap = new HashMap<>();
        int score = 0;
        Schedule root = new Schedule(gameLeft, pracsLeft, eventsMap, slotsMap, score);

        // run search
        startTreeSearch(root);
    }

    public void startTreeSearch(Schedule sched) {
        sched = partAssign(sched);

        for (Slot slot: sched.eventsMap.keySet()) {
            String key = slot.day;
            ArrayList<Event> value = sched.eventsMap.get(slot);
            System.out.print(key + ": [");
            value.forEach(event-> System.out.print(event.name + ", "));
            System.out.println("]");
        }
    }

    public void assignSpecial() {

    }
    
    public Schedule partAssign(Schedule sched) {
        ArrayList<PartAssign> partAssignList = input.paList;
        partAssignList.forEach(element ->  {
            assign(sched, element.event, element.slot);
        });

        return sched;
    }

    public void assign(Schedule sched, Event event, Slot slot) {
        ArrayList<Event> temp = (sched.eventsMap.get(slot) != null) ? sched.eventsMap.get(slot) : new ArrayList<>();
        temp.add(event);
        sched.eventsMap.put(slot, temp);
    }

    public static void main(String[] args) throws Exception {
        Event game1 = new Event("game1", null, null, null, 0, false);
        Slot slot1 = new Slot("slot1", null, null, 0, 0, false);
        ArrayList<Event> games = new ArrayList<>(Arrays.asList(game1));
        ArrayList<Slot> slots = new ArrayList<>(Arrays.asList(slot1));

        ArrayList<PartAssign> testPartAssignList = new ArrayList<>();
        testPartAssignList.add(new PartAssign(game1, slot1));
    
        Parser input = new Parser("README.md");
        input.paList = testPartAssignList;
        input.games = games;
        TreeSearch testSearch = new TreeSearch(input, slots);
    }
}
