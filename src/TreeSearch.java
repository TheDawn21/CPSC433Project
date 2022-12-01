import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;


public class TreeSearch {
    Parser input;
    Schedule bestSched;
    // Eval eval;
    Stack<Schedule> stack;
    ArrayList<Slot> gSlots;
    ArrayList<Slot> pSlots;
    String path = "";


    public TreeSearch(Parser input) {
        this.input = input;

        // Initialize eval
        // this.eval = new Eval(input);

        // Group slot
        gSlots = new ArrayList<>();
        gSlots.addAll(input.m_game_slots);
        // gSlots.addAll(input.t_game_slots);
        // pSlots = new ArrayList<>();
        // pSlots.addAll(input.m_prac_slots);
        // pSlots.addAll(input.t_prac_slots);
        // pSlots.addAll(input.f_prac_slots);

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

        // stack.add(sched);
        // while (stack.size() != 0) {
        //     sched = stack.pop();
        //     sched.gamesLeft.get(0);
        //     for (Slot gSlot : gSlots) {
                
        //         sched = new Schedule(sched);
        //     }
        // }
        

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
        input.paMap.forEach((event, slot) ->  {
            assign(sched, event, slot);
        });
    }

    public void assign(Schedule sched, Event event, Slot slot) {
        // if (eval.isValid(sched, event, slot)) {
        if (true) {
            // calc score
            // sched.score = eval.getPen(sched, event, slot);

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
        ArrayList<Slot> slots = new ArrayList<>(Arrays.asList(slot1,slot2));
        ArrayList<Event> games = new ArrayList<>(Arrays.asList(game1, game2, game3));

        HashMap<Event, Slot> testPaMap = new HashMap<>();
        testPaMap.put(game1, slot1);
        testPaMap.put(game2, slot1);
        testPaMap.put(game3, slot2);
    
        Parser input = new Parser("README.md");
        input.paMap = testPaMap;
        input.games = games;
        input.m_game_slots = slots;
        TreeSearch testSearch = new TreeSearch(input);
    }
}
