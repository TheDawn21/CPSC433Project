

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
                TreeSearch aTree = new TreeSearch(parseObj);
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
        System.out.println("Monday Game Slots");
        for(int i = 0; i < p.m_game_slots.size(); i++) {
            Slot slot = p.m_game_slots.get(i);
            System.out.println(slot.day);
            System.out.println(slot.startTime); System.out.println(slot.endTime);
            System.out.println(slot.max); System.out.println(slot.min);
            System.out.println(slot.isSpecial);
        }
        System.out.println("Tuesday Game Slots");
        for(int i = 0; i < p.t_game_slots.size(); i++) {
            Slot slot = p.t_game_slots.get(i);
            System.out.println(slot.day);
            System.out.println(slot.startTime); System.out.println(slot.endTime);
            System.out.println(slot.max); System.out.println(slot.min);
            System.out.println(slot.isSpecial);
        }
        System.out.println("Monday Practice Slots");
        for(int i = 0; i < p.m_prac_slots.size(); i++) {
            Slot slot = p.m_prac_slots.get(i);
            System.out.println(slot.day);
            System.out.println(slot.startTime); System.out.println(slot.endTime);
            System.out.println(slot.max); System.out.println(slot.min);
            System.out.println(slot.isSpecial);
        }
        System.out.println("Tuesday Practice Slots");
        for(int i = 0; i < p.t_prac_slots.size(); i++) {
            Slot slot = p.t_prac_slots.get(i);
            System.out.println(slot.day);
            System.out.println(slot.startTime); System.out.println(slot.endTime);
            System.out.println(slot.max); System.out.println(slot.min);
            System.out.println(slot.isSpecial);
        }
        System.out.println("Friday Practice Slots");
        for(int i = 0; i < p.f_prac_slots.size(); i++) {
            Slot slot = p.f_prac_slots.get(i);
            System.out.println(slot.day);
            System.out.println(slot.startTime); System.out.println(slot.endTime);
            System.out.println(slot.max); System.out.println(slot.min);
            System.out.println(slot.isSpecial);
        }
        System.out.println("Games");
        for(int i = 0; i < p.games.size(); i++) {
            Event event = p.games.get(i);
            System.out.println(event.org);
            System.out.println(event.age); System.out.println(event.tier);
            System.out.println(event.div); 
            System.out.println(event.type);
        }
        System.out.println("Practices");
        for(int i = 0; i < p.practices.size(); i++) {
            Event event = p.practices.get(i);
            System.out.println(event.org);
            System.out.println(event.age); System.out.println(event.tier);
            System.out.println(event.div); 
            System.out.println(event.type);
        }

        System.out.println(p.ncMap);
        System.out.println(p.unwantMap);
        System.out.println(p.preferMap);
        System.out.println(p.pairMap);
        System.out.println(p.paMap);
    }
}
