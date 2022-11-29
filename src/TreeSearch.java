import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;


public class TreeSearch {
    Parser input;
    Schedule bestSched;
//    Eval eval;
    Stack<Schedule> stack;
    String path = "";


    public TreeSearch(Parser input) {
        this.input = input;

        // Initialize eval
//        this.eval = new Eval(input);

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
        specialAssign(sched);
        partAssign(sched);

//        for (game : sched.gamesLeft) {
//
//        }

        for (Slot slot: sched.eventsMap.keySet()) {
            String key = slot.day;
            ArrayList<Event> value = sched.eventsMap.get(slot);
            System.out.print(key + ": [");
            value.forEach(event-> System.out.print(event.name + ", "));
            System.out.println("]");
        }
        System.out.println(path);
    }

    public void specialAssign(Schedule sched) {
        // TODO implement specialAssign
    }
    
    public void partAssign(Schedule sched) {
        ArrayList<PartAssign> partAssignList = input.paList;
        partAssignList.forEach(element ->  {
            assign(sched, element.event, element.slot);
        });
    }

    public void assign(Schedule sched, Event event, Slot slot) {
//        if (eval.isValid(sched, event, slot)) {
        if (true) {
            // calc score
//            sched.score = eval.getPen(sched, event, slot);

            // update eventsMap
            ArrayList<Event> temp = (sched.eventsMap.get(slot) != null) ? sched.eventsMap.get(slot) : new ArrayList<>();
            temp.add(event);
            sched.eventsMap.put(slot, temp);

            // update slotsMap
            sched.slotsMap.put(event, slot);

            // update gamesLeft/pracsLeft
            if (event.type) sched.gamesLeft.remove(event);
            else sched.pracsLeft.remove(event);

            // update path
            addPath(event, slot, true, sched.score);
        }
    }

    public void addPath(Event event, Slot slot, Boolean isValid, int score) {
        path += "Event: " + event.name + ", Slot: " + slot.day + ", IsValid: " + isValid + ", Score: " + score + "\n";
    }

    public static void main(String[] args) throws Exception {
        Event game1 = new Event("game1", null, null, null, 0, true);
        Event game2 = new Event("game2", null, null, null, 0, true);
        Event game3 = new Event("game3", null, null, null, 0, true);
        Slot slot1 = new Slot("slot1", null, null, 0, 0, false);
        Slot slot2 = new Slot("slot2", null, null, 0, 0, false);
        ArrayList<Event> games = new ArrayList<>(Arrays.asList(game1, game2, game3));

        ArrayList<PartAssign> testPartAssignList = new ArrayList<>();
        testPartAssignList.add(new PartAssign(game1, slot1));
        testPartAssignList.add(new PartAssign(game2, slot1));
        testPartAssignList.add(new PartAssign(game3, slot2));
    
        Parser input = new Parser("README.md");
        input.paList = testPartAssignList;
        input.games = games;
        TreeSearch testSearch = new TreeSearch(input);
    }
}
