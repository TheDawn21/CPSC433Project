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
        // Initialize stack
        this.stack = new Stack<>();

        // Initalize best schedule
        bestSched = new Schedule(null, null, null, null, Integer.MAX_VALUE);

        // Group slot
        gSlots = new ArrayList<>();
        gSlots.addAll(input.m_game_slots);
        // gSlots.addAll(input.t_game_slots);
        pSlots = new ArrayList<>();
        pSlots.addAll(input.m_prac_slots);
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

    private void startTreeSearch(Schedule sched) {
        specialAssign(sched);
        partAssign(sched);

        stack.add(sched);
        while (stack.size() != 0) {
            sched = stack.pop();
            eventsAssign(sched);
        }
        

        // for (Slot slot: sched.eventsMap.keySet()) {
        //     String key = slot.day;
        //     ArrayList<Event> value = sched.eventsMap.get(slot);
        //     System.out.print(key + ": [");
        //     value.forEach(event-> System.out.print(event.name + ", "));
        //     System.out.println("]");
        // }
        System.out.println(path);
    }

    private void specialAssign(Schedule sched) {
        // TODO implement specialAssign
    }
    
    private void partAssign(Schedule sched) {
        path+= "Part Assign\n";
        input.paMap.forEach((event, slot) ->  {
            assign(sched, event, slot);
        });
    }

    private void eventsAssign(Schedule sched) {
        if (sched.gamesLeft.size() != 0) {
            path += "Expanding games\n";
            Event game = sched.gamesLeft.get(0);
            for (Slot gSlot : gSlots) {
                Schedule schedCopy = new Schedule(sched);
                if (assign(schedCopy, game, gSlot) && schedCopy.score <= bestSched.score) 
                    stack.add(schedCopy);
            }
        }
        else if (sched.pracsLeft.size() != 0) {
            path += "Expanding pracs\n";
            Event prac = sched.pracsLeft.get(0);
            for (Slot pSlot : pSlots) {
                Schedule schedCopy = new Schedule(sched);
                if (assign(schedCopy, prac, pSlot) && schedCopy.score <= bestSched.score) 
                    stack.add(schedCopy);
            }
        }
        else {
            bestSched = (bestSched.score > sched.score) ? sched : bestSched;
            path += "Leaf\n";
        }
    }  

    // return if assign isValid
    private Boolean assign(Schedule sched, Event event, Slot slot) {
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
            path += "Event: " + event.name + ", Slot: " + slot.day + ", IsValid: true, Score: " + sched.score + "\n";

            return true;
        }
        else path += "Event: " + event.name + ", Slot: " + slot.day + ", IsValid: false\n";

        return false;
    }

    public static void main(String[] args) throws Exception {
        Event game1 = new Event("game1", null, null, null, 0, true);
        Event game2 = new Event("game2", null, null, null, 0, true);
        Event game3 = new Event("game3", null, null, null, 0, true);
        Event game4 = new Event("game4", null, null, null, 0, true);
        Event prac1 = new Event("prac1", null, null, null, 0, false);
        Slot gSlot1 = new Slot("gSlot1", null, null, 0, 0, false);
        Slot gSlot2 = new Slot("gSlot2", null, null, 0, 0, false);
        Slot pSlot1 = new Slot("pSlot1", null, null, 0, 0, false);
        ArrayList<Slot> gSlots = new ArrayList<>(Arrays.asList(gSlot1,gSlot2));
        ArrayList<Slot> pSlots = new ArrayList<>(Arrays.asList(pSlot1));
        ArrayList<Event> games = new ArrayList<>(Arrays.asList(game1, game2, game3, game4));
        ArrayList<Event> pracs = new ArrayList<>(Arrays.asList(prac1));

        HashMap<Event, Slot> testPaMap = new HashMap<>();
        testPaMap.put(game1, gSlot1);
        testPaMap.put(game2, gSlot1);
    
        Parser input = new Parser("README.md");
        input.paMap = testPaMap;
        input.games = games;
        input.practices = pracs;
        input.m_game_slots = gSlots;
        input.m_prac_slots = pSlots;
        TreeSearch testSearch = new TreeSearch(input);
    }
}
