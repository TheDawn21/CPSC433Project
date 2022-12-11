import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;
import java.util.Random;


public class TreeSearch {
    Parser input;
    public Schedule bestSched;
    Eval eval;
    Stack<Schedule> stack;
    ArrayList<Slot> gSlots;
    int numGameSlots;
    ArrayList<Slot> pSlots;
    int numPracSlots;
    //String path = "";

    Random rng = new Random();

    public TreeSearch(Parser input, int[] commandLineInputs, Schedule sched) {
        this.input = input;

        // Initialize eval
        this.eval = new Eval(input, commandLineInputs);
        // Initialize stack
        this.stack = new Stack<>();

        // Initalize best schedule
        bestSched = new Schedule(sched);

        // Group slot
        gSlots = new ArrayList<>();
        gSlots.addAll(input.m_game_slots);
        gSlots.addAll(input.t_game_slots);
        pSlots = new ArrayList<>();
        pSlots.addAll(input.m_prac_slots);
        pSlots.addAll(input.t_prac_slots);
        pSlots.addAll(input.f_prac_slots);


        // Find number of slots
        numGameSlots = gSlots.size();
        numPracSlots = pSlots.size();


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
        //     String key = slot.idName;
        //     ArrayList<Event> value = sched.eventsMap.get(slot);
        //     System.out.print(key + ": [");
        //     value.forEach(event-> System.out.print(event.id + ", "));
        //     System.out.println("]");
        // }
        //System.out.println(path);
    }

    private void specialAssign(Schedule sched) {
        //path+= "Special Assign\n";
        Slot specialSlot = null;
        for (Slot s : input.t_prac_slots) {
            if (s.isSpecial)
                specialSlot = s;
        }
        if (input.specialEvents.size() != 0 && specialSlot == null) {
            System.out.println("No special slot");
            System.exit(0);
        }
        if (specialSlot == null) return;

        for (Event e : input.specialEvents) {
            Boolean valid = assign(sched, e, specialSlot);
            if (!valid) {
                System.out.println("Cannot assign Special Event!");
                System.exit(0);
            }
        }
    }
    
    private void partAssign(Schedule sched) {
       // path+= "Part Assign\n";
        input.paMap.forEach((event, slot) ->  {
            Boolean valid = assign(sched, event, slot);
            if (!valid) {
                System.out.println("Cannot assign Part Assign!");
                System.exit(0);
            }
        });
    }


    // *** Experimenting with random assignment for events ***
    private void eventsAssign(Schedule sched) {
        if (sched.gamesLeft.size() != 0) {
           // path += "Expanding games\n";

            
            Event game = sched.gamesLeft.get(0);
            ArrayList<Slot> gameSlotsCopy = new ArrayList<>(gSlots);
            Collections.shuffle(gameSlotsCopy);

            Schedule schedCopy = new Schedule(sched);

            for (int i = 0; i < numGameSlots; i++) {
                Slot randomSlot = gameSlotsCopy.get(i);
                if (assign(schedCopy, game, randomSlot) && (schedCopy.score < bestSched.score)) 
                    stack.add(schedCopy);
            }

            
            

            /* for (Slot gSlot : gSlots) {
                Schedule schedCopy = new Schedule(sched);
                if (assign(schedCopy, game, gSlot) && (schedCopy.score < bestSched.score)) 
                if (assign(schedCopy, game, gSlot) && (schedCopy.score < bestSched.score)) 
                    stack.add(schedCopy);
            }*/
            
        }
        else if (sched.pracsLeft.size() != 0) {
           // path += "Expanding pracs\n";
            Event prac = sched.pracsLeft.get(0);

            ArrayList<Slot> pracSlotsCopy = new ArrayList<>(pSlots);
            Collections.shuffle(pracSlotsCopy);

            Schedule schedCopy = new Schedule(sched);

            for (int i = 0; i < numPracSlots; i++) {
                Slot randomSlot = pracSlotsCopy.get(i);
                if (assign(schedCopy, prac, randomSlot) && (schedCopy.score < bestSched.score)) 
                    stack.add(schedCopy);
            }


            /* for (Slot pSlot : pSlots) {
                Schedule schedCopy = new Schedule(sched);
                if (assign(schedCopy, prac, pSlot) && (schedCopy.score < bestSched.score)) 
                if (assign(schedCopy, prac, pSlot) && (schedCopy.score < bestSched.score)) 
                    stack.add(schedCopy);
            }*/
            
        }
        else {
            if (bestSched.score > sched.score) {
                bestSched = sched;
                System.out.println("Eval: " + bestSched.score);
                printSched(bestSched);
            }
          //  path += "Leaf\n";
        }
    }  

    public static void printSched (Schedule sched) {
        if(sched.slotsMap != null){
            sched.slotsMap.forEach((event, slot) -> {
                System.out.println(event.name + ": " + slot.idName);
            });
        }

        System.out.println("Score: " + sched.score);
    }

    // return if assign isValid
    private Boolean assign(Schedule sched, Event event, Slot slot) {

        

        if (eval.isValid(sched, event, slot)) {
            // calc score
            sched.score = eval.getPen(sched, event, slot);

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
           // path += "Event: " + event.id + ", Slot: " + slot.idName + ", IsValid: true, Score: " + sched.score + "\n";

            return true;
        }
        //else path += "Event: " + event.id + ", Slot: " + slot.idName + ", IsValid: false\n";

        return false;
    }
}
