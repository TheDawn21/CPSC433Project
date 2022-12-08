import java.util.Arrays;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 9) {
            try {
                String filename = args[0];
                // soft constraint weights and penalties
                int[] weights_penalties = new int[8];
                for (int i = 1; i<9; i++) {
                    weights_penalties[i-1] = Integer.parseInt(args[i]);
                }
                Parser parseObj = new Parser(filename);
                testParserOutput(parseObj);
                TreeSearch aTree = new TreeSearch(parseObj, weights_penalties);
                System.out.println(aTree.bestSched.score);

            } catch (NumberFormatException e) {
                System.err.println("Argument error: " + e.getLocalizedMessage());
                System.exit(1);
            }
            
        } else {
            System.out.println("Invalid input");
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
            printEvent(key);
            System.out.print(" -> ");
            HashSet<Event> hs = p.ncMap.get(key);
            System.out.println(Arrays.toString(hs.toArray()));
        }
        
        // System.out.println(p.ncMap);
        // System.out.println(p.unwantMap);
        // System.out.println(p.preferMap);
        // System.out.println(p.pairMap);
        // System.out.println(p.paMap);
    }

    private static void printSlot(Slot slot) {
        System.out.print("Day: " + slot.day);
        System.out.print(", Start: " + slot.startTime); 
        System.out.print(", End: " + slot.endTime);
        System.out.print(", Max: " + slot.max); 
        System.out.print(", Min: " + slot.min);
        System.out.println(", IsSpecial: " + slot.isSpecial);
    }

    private static void printEvent(Event event) {
        System.out.print(event.name);
        System.out.print(event.org);
        System.out.print(" " + event.age); 
        System.out.print(" " + event.tier);
        System.out.print(" DIV" + event.div); 
        System.out.println(" | type: " + ((event.type) ? "game" : "prac"));
    }
}
