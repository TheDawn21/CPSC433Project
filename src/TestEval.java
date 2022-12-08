import java.util.*;

// For testing Eval
public class TestEval {
    public static void main(String[] args) {
        Event game1 = new Event("game1", "CMSA", "U12", "T1", 1, true, "game1");
        Event game2 = new Event("game2", "CMSA", "U15", "T1", 9, true, "game2");
        Event game3 = new Event("game3", "CMSA", "U19", "T1", 1, true, "game3");
        Event game4 = new Event("game4", "CMSA", "U16", "T1", 0, true, "game4");
        Event prac1 = new Event("prac1", "CMSA", "U13", "T1", 2, false, "prac1");
        Slot gSlot1 = new Slot("MO", 1800, 1900, 0, 0, false, "gSlot1");
        Slot gSlot2 = new Slot("TU", 1100, 1230, 1, 0, false, "gSlot2");
        Slot gSlot3 = new Slot("TU", 1830, 2000, 1, 0, false, "gSlot3");
        Slot pSlot1 = new Slot("TU", 900, 1000, 2, 0, false, "pSlot1");
        Slot pSlot2 = new Slot("TU", 1000, 1100, 2, 0, false, "pSlot2");
        Slot specialSlot = new Slot("TU", 1800, 1900, 2, 0, true, "special");
        ArrayList<Slot> gSlots = new ArrayList<>(Arrays.asList(gSlot1, gSlot2, gSlot3));
        ArrayList<Slot> pSlots = new ArrayList<>(Arrays.asList(pSlot1, pSlot2, specialSlot));
        ArrayList<Event> games = new ArrayList<>(Arrays.asList(game1, game2, game3, game4));
        ArrayList<Event> pracs = new ArrayList<>(Arrays.asList(prac1));

        HashMap<Event, Slot> testPaMap = new HashMap<>();
        testPaMap.put(game1, gSlot1);
        testPaMap.put(game2, gSlot1);
        HashSet<Slot> u1 = new HashSet<>();
        HashSet<Slot> u2 = new HashSet<>();
        u1.add(gSlot1);
        u1.add(gSlot2);
        u2.add(pSlot1);
        HashMap<Event, HashSet<Slot>> unwanted = new HashMap<>();
        unwanted.put(game1, u1);
        unwanted.put(prac1, u2);
    
        Parser input = new Parser("..\\README.md");
        input.paMap = testPaMap;
        input.games = games;
        input.practices = pracs;
        input.m_game_slots = gSlots;
        input.m_prac_slots = pSlots;
        input.unwantMap = unwanted;

        // Create Eval
        int[] penalties = new int[]{1,1,1,1,1,1,1,1};
        Eval eval = new Eval(input, penalties);


        // assign games to slots
        ArrayList<Event> gs1Events = new ArrayList(Arrays.asList(game1, game2));
        HashMap<Slot, ArrayList<Event>> eventsMap = new HashMap<Slot, ArrayList<Event>>();
        eventsMap.put(gSlot1, gs1Events);
        HashMap<Event, Slot> slotsMap = new HashMap<Event, Slot>();

        // create a Schedule
        ArrayList<Event> eventsLeft = new ArrayList<Event>();
        Schedule schedule = new Schedule(eventsLeft, eventsLeft, eventsMap, slotsMap, 0);


        // Test Hard constraints
        boolean test = eval.gameTuesday11(game1, gSlot3);

        
        
        System.out.println(test);

    }
}