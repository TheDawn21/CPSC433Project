import java.util.*;

// For testing Eval
public class TestEval {
    public static void main(String[] args) {
        Event game1 = new Event("game1", "CMSA", "U10", "T1", 1, true);
        Event game2 = new Event("game2", "CMSA", "U19", "T1", 9, true);
        Event game3 = new Event("game3", "CMSA", "U19", "T1", 1, true);
        Event game4 = new Event("game4", "CMSA", "U17", "T1", 0, true);
        Event prac1 = new Event("prac1", "CMSA", "U14", "T1", 1, false);
        Event prac2 = new Event("prac2", "CMSA", "U14", "T1", 1, false);
        Slot gSlot1 = new Slot("MO", 1100, 1200, 0, 0, false);
        Slot gSlot2 = new Slot("TU", 1100, 1230, 1, 0, false);
        Slot gSlot3 = new Slot("TU", 1230, 1400, 1, 0, false);
        Slot pSlot1 = new Slot("TU", 1100, 1200, 2, 0, false);
        Slot pSlot2 = new Slot("TU", 1200, 1300, 2, 0, false);
        Slot specialSlot = new Slot("TU", 1800, 1900, 2, 0, true);
        ArrayList<Slot> gSlots = new ArrayList<>(Arrays.asList(gSlot1, gSlot2, gSlot3));
        ArrayList<Slot> pSlots = new ArrayList<>(Arrays.asList(pSlot1, pSlot2, specialSlot));
        ArrayList<Event> games = new ArrayList<>(Arrays.asList(game1, game2, game3, game4));
        ArrayList<Event> pracs = new ArrayList<>(Arrays.asList(prac1, prac2));
        
        HashMap<Event, Slot> testPaMap = new HashMap<>();
        testPaMap.put(game1, gSlot1);
        testPaMap.put(game2, gSlot1);
        //unwanted
        HashSet<Slot> u1 = new HashSet<>();
        HashSet<Slot> u2 = new HashSet<>();
        u1.add(gSlot1);
        u1.add(gSlot2);
        u2.add(pSlot1);
        HashMap<Event, HashSet<Slot>> unwanted = new HashMap<>();
        unwanted.put(game1, u1);
        unwanted.put(prac1, u2);
        //notcompatible
        HashSet<Event> g3 = new HashSet<>();
        g3.add(game1);
        g3.add(game2);
        HashSet<Event> g1 = new HashSet<>();
        g1.add(prac2);
        HashSet<Event> pr2 = new HashSet<>();
        pr2.add(game1);
        HashMap<Event, HashSet<Event>> notCompatible = new HashMap<>();
        notCompatible.put(game3, g3);
        notCompatible.put(game1, g1);
        notCompatible.put(prac2, pr2);
    
        Parser input = new Parser("..\\README.md");
        input.paMap = testPaMap;
        input.games = games;
        input.practices = pracs;
        input.m_game_slots = gSlots;
        input.m_prac_slots = pSlots;
        input.unwantMap = unwanted;
        input.ncMap = notCompatible;

        // Create Eval
        int[] penalties = new int[]{1,1,1,1,1,1,1,1};
        Eval eval = new Eval(input, penalties);

        // assign games to slots
        ArrayList<Event> gs1Events = new ArrayList<>(Arrays.asList(game1, game3));
        //ArrayList<Event> gs3Events = new ArrayList<>(Arrays.asList(game3));
        ArrayList<Event> gs2Events = new ArrayList<>(Arrays.asList(game2));
        ArrayList<Event> ps1Events = new ArrayList<>(Arrays.asList(prac1));
        ArrayList<Event> ps2Events = new ArrayList<>(Arrays.asList(prac2));
        HashMap<Slot, ArrayList<Event>> eventsMap = new HashMap<Slot, ArrayList<Event>>();
        eventsMap.put(gSlot1, gs1Events);
        //eventsMap.put(gSlot3, gs3Events);
        eventsMap.put(gSlot2, gs2Events);
        eventsMap.put(pSlot1, ps1Events);
        eventsMap.put(pSlot2, ps2Events);
        HashMap<Event, Slot> slotsMap = new HashMap<Event, Slot>();

        // create a Schedule
        ArrayList<Event> eventsLeft = new ArrayList<Event>();
        Schedule schedule = new Schedule(eventsLeft, eventsLeft, eventsMap, slotsMap, 0);


        // Test Hard constraints
        System.out.println("ageOverlap:");
        boolean test = eval.ageOverlap(schedule, game4, gSlot2);
        

        System.out.println(test);

    }
}