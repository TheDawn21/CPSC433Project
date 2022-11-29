import java.util.*;

// Contains static methods that either evaluate a Schedule or evaluate
// an assignment of an Event into a Schedule.
public class Eval {
    // Problem description
    public Parser input;

    // overlap[s] = {s1,...,sn} where Slot s (game) intersects in time with
    // Slot objects s1...sn (practices).
    public HashMap<Slot, ArrayList<Slot>> overlap;
    // same but with practice slot as key
    public HashMap<Slot, ArrayList<Slot>> overlapReverse;


    // Constructor
    public Eval(Parser input) {
        this.input = input;
        overlap = new HashMap<Slot, ArrayList<Slot>>();
        overlapReverse = new HashMap<Slot, ArrayList<Slot>>();

        // Find game-practice overlaps
        ArrayList<Slot> mwf = input.m_game_slots;
        for (int i = 0; i < mwf.size(); i++) {
            Slot gameSlot = mwf.get(i);
            ArrayList<Slot> overlapPractices = new ArrayList<Slot>();

            ArrayList<Slot> m_practices = findOverlap(gameSlot, input.m_prac_slots);
            ArrayList<Slot> f_practices = findOverlap(gameSlot, input.f_prac_slots);

            overlapPractices.addAll(m_practices);
            overlapPractices.addAll(f_practices);
            
            overlap.put(gameSlot, overlapPractices);

            // add gameSlot to overlapReverse
            for (int j = 0; j < overlapPractices.size(); j++) {
                Slot pracSlot = overlapPractices.get(j);
                if (overlapReverse.containsKey(pracSlot)) {
                    ArrayList<Slot> slots = overlapReverse.get(pracSlot);
                    slots.add(gameSlot);
                } else {
                    ArrayList<Slot> gameSet = new ArrayList<Slot>();
                    gameSet.add(gameSlot);
                    overlapReverse.put(pracSlot, gameSet);
                }
            }
        }

        ArrayList<Slot> tth = input.t_game_slots;
        for (int i = 0; i < tth.size(); i++) {
            Slot gameSlot = tth.get(i);
            ArrayList<Slot> overlapPractices = new ArrayList<Slot>();

            ArrayList<Slot> t_practices = findOverlap(gameSlot, input.t_prac_slots);

            overlapPractices.addAll(t_practices);
            
            overlap.put(gameSlot, overlapPractices);

            // add gameSlot to overlapReverse
            for (int j = 0; j < overlapPractices.size(); j++) {
                Slot pracSlot = overlapPractices.get(j);
                if (overlapReverse.containsKey(pracSlot)) {
                    ArrayList<Slot> slots = overlapReverse.get(pracSlot);
                    slots.add(gameSlot);
                } else {
                    ArrayList<Slot> gameSet = new ArrayList<Slot>();
                    gameSet.add(gameSlot);
                    overlapReverse.put(pracSlot, gameSet);
                }
            }
        }
    }

    


    //------------ Evaluation Functions ---------------------//

    // Calculates the total penalty after assigning e to the schedule
    // Input:
    // sched: valid incomplete Schedule (before adding e)
    // e: Event being assigned
    // s: Slot in sched that e is assigned to
    //
    // Return: sum of all soft constraint penalties
    public int getPen(Schedule sched, Event e, Slot s) {
        /* INCOMPLETE */

        return 0;
    }
    

    // Checks if an event can be assigned to a slot in an incomplete schedule
    // Input:
    // sched: valid incomplete Schedule (before adding e)
    // e: Event to be assigned into sched
    // s: Slot in sched to assign e to
    //
    // Return: true if schedule is still valid after insertion, false otherwise
    public boolean isValid(Schedule sched, Event e, Slot s) {
        boolean valid = true;
        // Go through all hard constraints, if any of them are broken
        // set valid to false.

        // Check game and practice max
        if (maxInvalid(sched, s) == true) {
            valid = false;
        }

        // Check if e intersects with another event with same division
        if (practiceIntersect(sched, e, s) == true) {
            valid = false;
        }

        /* INCOMPLETE */

        return valid;
    }






    //---------------- Helper functions ---------------------//

    // Input: gameSlot, slotList
    // Returns: all practice slots in slotList that overlap with gameSlot
    private static ArrayList<Slot> findOverlap(Slot gameSlot, ArrayList<Slot> slotList) {
        ArrayList<Slot> overlappingPractices = new ArrayList<Slot>();

        for (int i = 0; i < slotList.size(); i++) {
//            Slot practiceSlot = slotList.get(i);
//            if ((gameSlot.day.equals("Monday")
//                    && (practiceSlot.day.equals("Monday")
//                            || practiceSlot.day.equals("Friday")))
//                 || (gameSlot.day.equals("Tuesday")
//                    && practiceSlot.day.equals("Tuesday")))
//            {
//                if (gameSlot.startTime == practiceSlot.startTime) {
//                    overlappingPractices.add(practiceSlot);
//                } else if (gameSlot.endTime == practiceSlot.endTime) {
//                    overlappingPractices.add(practiceSlot);
//                } else if (gameSlot.endTime > practiceSlot.endTime) {
//                    if (gameSlot.startTime < practiceSlot.endTime)
//                        overlappingPractices.add(practiceSlot);
//                } else if (gameSlot.startTime < practiceSlot.endTime) {
//                    if (gameSlot.endTime > practiceSlot.startTime)
//                        overlappingPractices.add(practiceSlot);
//                }
//            }
        }

        return overlappingPractices;
    }


    // Returns true if assigning e makes sched invalid
    private boolean maxInvalid(Schedule sched, Slot s) {
        int max = s.max;
        HashMap<Slot, ArrayList<Event>> assignment = sched.eventsMap;
        ArrayList<Event> assigned = assignment.get(s);
        
        if (assigned.size() == max) {
            return true;
        } 

        return false;
    }


    // INCOMPLETE
    // Returns true if e conflicts with another event in s
    private boolean practiceIntersect(Schedule sched, Event e, Slot s) {
        boolean invalid = false;

        if (e.type == true) {
            // e is game
            ArrayList<Slot> overlappingSlots = overlap.get(s);
            for (int i = 0; i < overlappingSlots.size(); i++) {
                HashMap<Slot, ArrayList<Event>> assignment = sched.eventsMap;
                Slot slot = overlappingSlots.get(i);
                ArrayList<Event> practices = assignment.get(slot);

                for (int j = 0; j < practices.size(); j++) {
                    if (e.sameDiv(practices.get(j))) {
                        invalid = true;
                        break;
                    }
                }
            }
        } else {
            // e is practice

        }

        return invalid;
    }  

}
