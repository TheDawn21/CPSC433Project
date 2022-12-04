import java.util.*;


// Contains static methods that either evaluate a Schedule or evaluate
// an assignment of an Event into a Schedule.
public class Eval {
    // Problem description
    public Parser input;

    // overlap[s] = {s1,...,sn} where Slot s intersects in time with
    // Slot objects s1...sn (practices).
    public HashMap<Slot, ArrayList<Slot>> overlap;

    // index 0-3 = weights, 4-7 = penalties
    public int wminfilled;
    public int wpref;
    public int wpair;
    public int wsecdiff;
    public int pengamemin;
    public int penpracticemin;
    public int pennotpaired;
    public int pensection;


    public ArrayList<Slot> sortedDecrGameMin;
    public ArrayList<Slot> sortedDecrPracMin;
    


    // Constructor
    public Eval(Parser input, int[] commandLineInputs) {
        this.input = input;
        this.wminfilled = commandLineInputs[0];
        this.wpref = commandLineInputs[1];
        this.wpair = commandLineInputs[2];
        this.wsecdiff = commandLineInputs[3];
        this.pengamemin = commandLineInputs[4];
        this.penpracticemin = commandLineInputs[5];
        this.pennotpaired = commandLineInputs[6];
        this.pensection = commandLineInputs[7];

        overlap = new HashMap<Slot, ArrayList<Slot>>();

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

            // add gameSlot to overlap
            for (int j = 0; j < overlapPractices.size(); j++) {
                Slot pracSlot = overlapPractices.get(j);
                if (overlap.containsKey(pracSlot)) {
                    ArrayList<Slot> slots = overlap.get(pracSlot);
                    slots.add(gameSlot);
                } else {
                    ArrayList<Slot> gameSet = new ArrayList<Slot>();
                    gameSet.add(gameSlot);
                    overlap.put(pracSlot, gameSet);
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

            // add gameSlot to overlap
            for (int j = 0; j < overlapPractices.size(); j++) {
                Slot pracSlot = overlapPractices.get(j);
                if (overlap.containsKey(pracSlot)) {
                    ArrayList<Slot> slots = overlap.get(pracSlot);
                    slots.add(gameSlot);
                } else {
                    ArrayList<Slot> gameSet = new ArrayList<Slot>();
                    gameSet.add(gameSlot);
                    overlap.put(pracSlot, gameSet);
                }
            }
        }

    }

    


    //------------ Evaluation Functions ---------------------//

    // Calculates the new total penalty after assigning an event to an incomplete schedule
    //
    // Input:
    // sched: valid incomplete Schedule object (score field will be updated)
    // e: Event being assigned
    // s: Slot in sched that e is assigned to
    //
    // Return: sum of all soft constraint penalties (modified gamemin, practicemin functions)
    public int getPen(Schedule sched, Event e, Slot s) {
        /* INCOMPLETE */
        // Get old score and add any new penalties to it
        int score = sched.score;

        // calculate penalty for gamemin or practicemin (modified)
        int oldMinPenalties = sched.minPenalties;
        if (oldMinPenalties < 0)
            oldMinPenalties = 0;
        int newMinPenalties = getMinPenalties(sched, e, s);
        if (newMinPenalties < 0)
            newMinPenalties = 0;
        if (e.type == true) {
            // e is a game Event
            score += (newMinPenalties - oldMinPenalties) * pengamemin * wminfilled;
        } else {
            // e is a practice Event
            score += (newMinPenalties - oldMinPenalties) * penpracticemin * wminfilled;
        }


        // calculate penalty for games with same division in a slot
        score += calcSection(sched, e, s) * wsecdiff;


        // pair
        //eval += calcPair(sched, e, s);

        
        // Update score field in Schedule and return score
        sched.score = score;
        return score;
    }
    

    // Checks if an event can be assigned to a slot in an incomplete schedule
    // Note: doesn't check partassign or special practice assignment, assumes they are correct
    //
    // Input:
    // sched: valid incomplete Schedule (before adding e)
    // e: Event that we attempt to assign into sched
    // s: Slot in sched to assign e to
    //
    // Return: true if schedule is still valid after assignment, false otherwise
    public boolean isValid(Schedule sched, Event e, Slot s) {
        // Go through all hard constraints, if any of them are broken set valid to false.
        boolean valid = true;
        

        // Check game and practice max
        if (maxInvalid(sched, s) == true) {
            valid = false;
        }

        // Check if s is unwanted slot
        // unwanted should be Hashset
        else if (slotUnwanted(e, s) == true) {
            valid = false;
        }

        // If e is in Div 9 check if s is an evening Slot
        else if (div9Early(e, s) == true) {
            valid = false;
        }

        // Check for overlap with special practices
        else if (specialOverlap(e, s) == true) {
            valid = false;
        }

        // Make sure game is not scheduled on Tuesday 11-1230
        else if (gameTuesday11(e, s) == true) {
            valid = false;
        }

        // Check if e is incompatible with a game already assigned to s
        // notcompatible should be Hashset for efficiency
        else if (notCompatible(sched, e, s) == true) {
            valid = false;
        }

        // Check if e intersects with another event with same division
        else if (practiceIntersect(sched, e, s) == true) {
            valid = false;
        }
        
        // Check for U15/.../U19 overlap
        else if (ageOverlap(sched, e, s) == true) {
            valid = false;
        }


        return valid;
    }






    //---------------- Helper functions ---------------------//

    // Input: gameSlot, slotList
    // Returns: all practice slots in slotList that overlap with gameSlot
    public static ArrayList<Slot> findOverlap(Slot gameSlot, ArrayList<Slot> slotList) {
        ArrayList<Slot> overlappingPractices = new ArrayList<Slot>();

        for (int i = 0; i < slotList.size(); i++) {
            Slot practiceSlot = slotList.get(i);
            if ((gameSlot.day.equals("MO")
                    && (practiceSlot.day.equals("MO")
                            || practiceSlot.day.equals("FR")))
                 || (gameSlot.day.equals("TU")
                    && practiceSlot.day.equals("TU")))
            {
                if (gameSlot.startTime == practiceSlot.startTime) {
                    overlappingPractices.add(practiceSlot);
                } else if (gameSlot.endTime == practiceSlot.endTime) {
                    overlappingPractices.add(practiceSlot);
                } else if (gameSlot.endTime > practiceSlot.endTime) {
                    if (gameSlot.startTime < practiceSlot.endTime) 
                        overlappingPractices.add(practiceSlot);
                } else if (gameSlot.startTime < practiceSlot.endTime) {
                    if (gameSlot.endTime > practiceSlot.startTime)
                        overlappingPractices.add(practiceSlot);
                }
            }
        }
        

        return overlappingPractices;
    }


    // Returns true if assigning e to Slot s violates gamemax
    public boolean maxInvalid(Schedule sched, Slot s) {
        int max = s.max;
        HashMap<Slot, ArrayList<Event>> assignment = sched.eventsMap;
        ArrayList<Event> assigned = assignment.get(s);
        
        if (assigned.size() == max) {
            return true;
        } 

        return false;
    }


    // Returns true if e conflicts with another event of the same division in s
    public boolean practiceIntersect(Schedule sched, Event e, Slot s) {
        boolean invalid = false;

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
        
        return invalid;
    } 


    // Returns true if there is an event in s that is notcompatible with e
    public boolean notCompatible(Schedule sched, Event e, Slot s) {
        boolean invalid = false;

        HashMap<Slot, ArrayList<Event>> assignment = sched.eventsMap;
        ArrayList<Event> otherEvents = assignment.get(s);
        
        HashSet<Event> noncompatibleEvents = input.ncMap.get(e);
        
        for (int i = 0; i < otherEvents.size(); i++) {
            Event other = otherEvents.get(i);
            if (noncompatibleEvents.contains(other)) {
                invalid = true;
                break;
            }
        }

        return invalid;
    }


    // Returns true if s is an unwanted slot of e
    public boolean slotUnwanted(Event e, Slot s) {
        boolean invalid = false;

        HashSet<Slot> unwantedSlots = input.unwantMap.get(e);
        if (unwantedSlots.contains(s))
            invalid = true;
        
        return invalid;
    }


    // Returns true if e is in Div 9 and s doesn't start after 1800
    public boolean div9Early(Event e, Slot s) {
        boolean invalid = false;

        if (e.div == 9) {
            if (s.startTime < 1800) {
                invalid = true;
            }
        }

        return invalid;
    }


    // Returns true if e in CMSA U12/U13 T1 and it overlaps with a special practice
    public boolean specialOverlap(Event e, Slot s) {
        boolean invalid = false;

        if (e.org.equals("CMSA") && e.tier.equals("T1")) {
            if (e.age.equals("U12") || e.age.equals("U13")) {
                if (e.type == false) {
                    // e is practice, cannot be in Tuesday 1800-1900
                    if (s.day.equals("TU") && s.startTime == 1800) {
                        invalid = true;
                    }
                } else {
                    // e is game, cannot intersect with Tuesday 1800-1900 practice slot
                    if (s.day.equals("TU")) {
                        if (s.startTime == 1800) {
                            invalid = true;
                        } else if (s.endTime == 1900) {
                            invalid = true;
                        }  
                          else if (s.startTime < 1900 && s.endTime > 1800) {
                            invalid = true;
                        } else if (s.endTime > 1800 && s.startTime < 1900) {
                            invalid = true;
                        }
                        
                    }
                }
            }
        }

        return invalid;
    }


    // Returns true if there are 2 games in age level U15/U16/U17/U18/U19 that overlap
    public boolean ageOverlap(Schedule sched, Event e, Slot s) {
        boolean invalid = false;

        if (inAgeRange(e)) {
            HashMap<Slot, ArrayList<Event>> assignment = sched.eventsMap;
            ArrayList<Event> otherGames = assignment.get(s);

            for (int i = 0; i < otherGames.size(); i++) {
                Event game = otherGames.get(i);
                if (inAgeRange(game)) {
                    invalid = true;
                    break;
                }
            }
        }

        return invalid;
    }

    // Helper for ageOverlap: returns true if e is in U15-U19
    private boolean inAgeRange(Event e) {
        if (e.age.equals("U15"))
            return true;
        if (e.age.equals("U16"))
            return true;
        if (e.age.equals("U17"))
            return true;
        if (e.age.equals("U18"))
            return true;
        if (e.age.equals("U19"))
            return true;

        return false;
    }


    // Returns true if e is a game and s is Tuesday 1100 - 1230
    public boolean gameTuesday11(Event e, Slot s) {
        boolean invalid = false;

         
        if (e.type == true) {
            if (s.day.equals("TU") && s.startTime == 1100) {
                invalid = true;
            }
        }
        

        return invalid;
    }


    // Returns the new number of gameMin + pracMin penalties after
    // assigning Event e.
    // Also updates the index and minPenalties field of the Schedule
    public int getMinPenalties(Schedule sched, Event e, Slot s) {
        int gIndex = sched.gameIndex;
        int pIndex = sched.pracIndex;
        int penalties = sched.minPenalties;

        // Assign e and calculate new min penalty
        if (e.type == true) { // assign game
            // check if putting event in slot reduces the penalty
            ArrayList<Event> assignedEvents = sched.eventsMap.get(s);
            int numAssigned = assignedEvents.size();
            if (numAssigned < s.min) {
                penalties -= 1;
            }

            int numGamesLeft = sched.gamesLeft.size() - 1;
            Slot highestMinSlot;
            if (gIndex < sortedDecrGameMin.size()) {
                highestMinSlot = sortedDecrGameMin.get(gIndex);
            } else {
                highestMinSlot = sortedDecrGameMin.get(gIndex - 1);
            }
            

            // Add a penalty for every previous slot in sorted list of slots,
            // each of which has greater or equal min requirement.
            if (gIndex > 0) {
                penalties += gIndex;
            }

            // Search for the next slot in list that has min <= # games left.
            // Add up penalties for all slots encountered along the way.
            while (numGamesLeft < highestMinSlot.min && gIndex < sortedDecrGameMin.size()) {
                int diff = highestMinSlot.min - numGamesLeft;
                penalties += diff;

                gIndex += 1;
                // Once the gamesleft goes under the min value of the slot with lowest
                // gameMin, gameIndex will go over array index
                sched.gameIndex = gIndex;
                if (gIndex < sortedDecrGameMin.size())
                    highestMinSlot = sortedDecrGameMin.get(gIndex);
            }

            // Update penalty counter for schedule
            sched.minPenalties = penalties;
            
        } else { // assign practice
            // check if putting event in slot reduces the penalty
            ArrayList<Event> assignedEvents = sched.eventsMap.get(s);
            int numAssigned = assignedEvents.size();
            if (numAssigned < s.min) {
                penalties -= 1;
            }

            int numPracsLeft = sched.pracsLeft.size() - 1;
            Slot highestMinSlot;
            if (pIndex < sortedDecrPracMin.size()) {
                highestMinSlot = sortedDecrPracMin.get(pIndex);
            } else {
                highestMinSlot = sortedDecrPracMin.get(pIndex - 1);
            }
            

            // Add a penalty for every previous slot in sorted list of slots,
            // each of which has greater or equal min requirement.
            if (pIndex > 0) {
                penalties += pIndex;
            }

            // Search for the next slot in list that has min <= # games left.
            // Add up penalties for all slots encountered along the way.
            while (numPracsLeft < highestMinSlot.min && pIndex < sortedDecrPracMin.size()) {
                int diff = highestMinSlot.min - numPracsLeft;
                penalties += diff;

                pIndex += 1;
                // Once the gamesleft goes under the min value of the slot with lowest
                // gameMin, gameIndex will go over array index
                sched.pracIndex = pIndex;
                if (pIndex < sortedDecrPracMin.size())
                    highestMinSlot = sortedDecrPracMin.get(pIndex);
            }

            // Update penalty counter for schedule
            sched.minPenalties = penalties;
        }

        return penalties;
    }


    // Returns the additional Eval score to be added based on the
    // "different divisional games should be in different times"
    // soft constraint.
    public int calcSection(Schedule sched, Event e, Slot s) {
        int sectionPenalties = 0;

        ArrayList<Event> assignedGames = sched.eventsMap.get(s);
        for (int i = 0; i < assignedGames.size(); i ++) {
            Event otherGame = assignedGames.get(i);
            if (otherGame.age.equals(e.age) && otherGame.tier.equals(e.tier))
                sectionPenalties += 1;
        }

        return sectionPenalties * pensection;
    }


    // Returns additional pair penalty
    public int calcPair(Schedule sched, Event e, Slot s) {
        int pairPen = 0;
        ArrayList<Event> pairList = input.pairMap.get(e);
        // For every event in pair list, check if event is assigned and two events are not in the same slot, then add pen, else 0
        for (Event event : pairList) {
            if (sched.slotsMap.containsKey(event) && sched.slotsMap.get(event) != s) {
                pairPen += pennotpaired;
            }
        }
        return pairPen;
    }

    // Returns additional preferences penalty
    // public int caclPref(Schedule sched, Event e, Slot s) {
    //     if (input.preferMap.get(e) != s) {

    //     }
    //     return 0;
    // }
}
