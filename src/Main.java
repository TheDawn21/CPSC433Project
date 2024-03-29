import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 9) {

            System.out.println("Original Algo (random)");

            try {

                Schedule bestSched = new Schedule(new ArrayList<>(), new ArrayList<>(), new HashMap<>(), new HashMap<>(), Integer.MAX_VALUE);

                String filename = args[0];
                // soft constraint weights and penalties
                int[] weights_penalties = new int[8];
                for (int i = 1; i<9; i++) {
                    weights_penalties[i-1] = Integer.parseInt(args[i]);
                }
                Parser parseObj = new Parser(filename);
               // testParserOutput(parseObj);

                TreeSearch aTree = new TreeSearch(parseObj, weights_penalties, bestSched);

                long start = System.currentTimeMillis(); //4 hour in MS
                long end = start + 300 * 1000; //1 minute
                while(System.currentTimeMillis() < end)
                {
                    aTree = new TreeSearch(parseObj, weights_penalties, bestSched);

                    if (aTree.bestSched.score < bestSched.score) {
                        bestSched = new Schedule(aTree.bestSched);
                    }
                
                }

                printSched(aTree.bestSched);
                System.out.println("Eval: " + aTree.bestSched.score);
                
                

                // if (aTree.bestSched.score == Integer.MAX_VALUE) {
                //     System.out.println("Cannot find valid solution");
                // } else {
                //     System.out.println("Eval: " + aTree.bestSched.score);
                //     printSched(aTree.bestSched);
                // }
                

            } catch (NumberFormatException e) {
                System.err.println("Argument error: " + e.getLocalizedMessage());
                System.exit(1);
            }
            
        } else {
            System.out.println("Invalid input");
        }
    }

    public static void printSched (Schedule sched) {
        if(sched.slotsMap != null){
            sched.slotsMap.forEach((event, slot) -> {
                System.out.printf("%-30.30s  %-30.30s%n", event.name, slot.idName);
            });
        }
    }

    public static void testParserOutput(Parser p) {
        System.out.println("\n**TESTING PARSER OUTPUT**\n");
        System.out.println(p.exampleName);
        System.out.println("\nGame Slots");
        for(int i = 0; i < p.m_game_slots.size(); i++) {
            Slot slot = p.m_game_slots.get(i);
            printSlot(slot);
        }
        for(int i = 0; i < p.t_game_slots.size(); i++) {
            Slot slot = p.t_game_slots.get(i);
            printSlot(slot);
        }
        System.out.println("\nPrac Slots");
        for(int i = 0; i < p.m_prac_slots.size(); i++) {
            Slot slot = p.m_prac_slots.get(i);
            printSlot(slot);
        }
        for(int i = 0; i < p.t_prac_slots.size(); i++) {
            Slot slot = p.t_prac_slots.get(i);
            printSlot(slot);
        }
        for(int i = 0; i < p.f_prac_slots.size(); i++) {
            Slot slot = p.f_prac_slots.get(i);
            printSlot(slot);
        }
        System.out.println("\nGames");
        for(int i = 0; i < p.games.size(); i++) {
            Event event = p.games.get(i);
            printEvent(event);
        }
        System.out.println("\nPractices");
        for(int i = 0; i < p.practices.size(); i++) {
            Event event = p.practices.get(i);
            printEvent(event);
        }
        
        System.out.println("\nNot Compatible");
        for (Event key : p.ncMap.keySet()) {
            if (key == null) System.out.print("null -> ");
            else System.out.print(key.id + " -> ");
            HashSet<Event> hs = p.ncMap.get(key);
            System.out.print("[");
            hs.forEach((e) -> System.out.print(e.id + ", "));
            System.out.println("]");
        }
        
        System.out.println("\nUnwanted");
        for (Event key : p.unwantMap.keySet()) {
            if (key == null) System.out.print("null -> ");
            else System.out.print(key.id + " -> ");
            HashSet<Slot> hs = p.unwantMap.get(key);
            System.out.print("[");
            hs.forEach((e) -> System.out.print(e.idName + ", "));
            System.out.println("]");
        }
        
        System.out.println("\nPreferences");
        for (Event key : p.preferMap.keySet()) {
            if (key == null) System.out.print("null -> ");
            else System.out.print(key.id + " -> ");
            //ArrayList<Object[]> arr = p.preferMap.get(key);
            ArrayList<Prefered> arr = p.preferMap.get(key);
            arr.forEach((e) -> {
                System.out.print("[");
                Slot s = e.slot;
                if (s == null) System.out.print("null,");
                else System.out.print(s.idName + ",");
                System.out.print(e.prefValue);
                System.out.print("] ");
            }); 
            System.out.println();
        }

        System.out.println("\nPairs");
        for (Event key : p.pairMap.keySet()) {
            if (key == null) System.out.print("null -> ");
            else System.out.print(key.id + " -> ");
            ArrayList<Event> arr = p.pairMap.get(key);
            arr.forEach(e -> {
                System.out.print("[");
                System.out.print(e.id + ",");
                System.out.println("]");
            });
        }

        System.out.println("\nPart Assign");
        for (Event key : p.paMap.keySet()) {
            if (key == null) System.out.print("null -> ");
            else System.out.print(key.id + " -> ");
            System.out.println(p.paMap.get(key).idName);
        }
        System.out.println();

        System.out.println("\nSpecial Events");
        for(Event ev : p.specialEvents) {
            printEvent(ev);
        }
        System.out.println();
    }

    private static void printSlot(Slot slot) {
        System.out.print(slot.idName + " | ");
        System.out.print("Day: " + slot.day);
        System.out.print(", Start: " + slot.startTime); 
        System.out.print(", End: " + slot.endTime);
        System.out.print(", Max: " + slot.max); 
        System.out.print(", Min: " + slot.min);
        System.out.println(", IsSpecial: " + slot.isSpecial);
    }

    private static void printEvent(Event event) {
        System.out.print(event.id);
        System.out.print(" | " + event.org);
        System.out.print(" " + event.age); 
        System.out.print(" " + event.tier);
        System.out.print(" DIV" + event.div); 
        System.out.println(" | type: " + ((event.type) ? "game" : "prac"));
    }
}
