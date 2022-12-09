import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.HashMap;

/*
 * Refactoring TODO
 * Create function to handle time conversions to int
 * Create a method for each hashmap
 * //Create a method for extracting the Age and Tier from string// *DONE*
 * //Create constants to compare against for string comparisons// *DONE*
 * //Create general method for slots// *DONE*
 * //Create an event creation method// *DONE*
 * //Create method for testing if an event is a practice// *DONE*
 * //Create method(s) for searching through the games/practices lists for a specific event// *DONE*
 * //Create method(s) for searching through the slots lists for a specific slot// *DONE*
 * //Create method to handing trimming the input information// *DONE*
 */

public class Parser {
    
    String exampleName;
    File file;
    Scanner scan;
    // All parser arrays on pictures
    ArrayList<Slot> m_game_slots = new ArrayList<>();
    ArrayList<Slot> t_game_slots = new ArrayList<>();
    ArrayList<Slot> m_prac_slots = new ArrayList<>();
    ArrayList<Slot> t_prac_slots = new ArrayList<>();
    ArrayList<Slot> f_prac_slots = new ArrayList<>();
    ArrayList<Event> games  = new ArrayList<>();
    ArrayList<Event> practices = new ArrayList<>();
    ArrayList<Event> specialEvents = new ArrayList<>();
    // Hashmaps
    HashMap<Event, HashSet<Event>> ncMap = new HashMap<Event, HashSet<Event>>();// = new HashMap<Event, ArrayList<Event>>();
    HashMap<Event, HashSet<Slot>> unwantMap = new HashMap<Event, HashSet<Slot>>();
    HashMap<Event, ArrayList<Object[]>> preferMap = new HashMap<Event, ArrayList<Object[]>>();
    HashMap<Event, ArrayList<Event>> pairMap = new HashMap<Event, ArrayList<Event>>();
    HashMap<Event, Slot> paMap = new HashMap<Event, Slot>();

    static final int MONDAY = 1; // Represents M/W/F or M/W
    static final int TUESDAY = 2; // Represents T/TH
    static final int FRIDAY = 3; // Only for Practices

    public Parser(String inFilename) {
        openFile(inFilename);
        try {
            parse();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     * Parse will read a stored file line by line, and parse the lines into 
     * the appropriate arrays to pass on to the Tree Search
     */
    public void parse() throws FileNotFoundException {

        String buffer = new String("");
        scan = new Scanner(file);

        while(scan.hasNextLine()){
            String s = scan.nextLine();
            if(s.length() > 2 && s.substring(0,2).equals("//")) {
                continue;
            }
            if(s.length() == 0) {
                // If an empty line is found send the buffer to fill List as a section has been filled
                fillList(buffer);
                buffer = ""; // reset buffer
            }
            else{
                buffer = buffer + s + "\n"; // Assuming \n is included, can add here if not
            }
        }
        // One last buffer that might be filled
        fillList(buffer);
        buffer = ""; // reset buffer
        scan.close();
    }

    public void openFile(String filename) {
        try {
            file = new File(filename);
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    // Fill list will fill the correct array given a String buffer which are a section of the file
    public void fillList(String buff) {
        String[] lines = buff.split("\n");
        if(lines.length <= 1) return;
        // System.out.println(buff);
        String header = lines[0].toLowerCase().trim();
        // Switch based on the section head in the file, lowercase only
        switch(header){
            case "name:":
                exampleName = lines[1];
                break;
            case "game slots:":
                // Starts at 1 to skip header
                for(int i = 1; i < lines.length; i++) {
                    // Read each line and split them based on days
                    String[] slotInfo = lines[i].split(",");
                    trimInput(slotInfo); // Get rid of whitespace
                    // Monday specific slots
                    if(slotInfo[0].equals("MO")){
                        addSlot(false, slotInfo, MONDAY);
                    }
                    // Tuesday specific slots
                    else if(slotInfo[0].equals("TU")) {
                        addSlot(false, slotInfo, TUESDAY);
                    }
                }
                break;
            case "practice slots:":
                // Starts at 1 to skip header
                for(int i = 1; i < lines.length; i++) {
                    // Read each line and separate them based on days
                    String[] slotInfo = lines[i].split(",");
                    trimInput(slotInfo); // Get rid of whitespace
                    // Monday specific slots
                    if(slotInfo[0].equals("MO")){
                        addSlot(true, slotInfo, MONDAY);
                    }
                    // Tuesday specific slots
                    else if(slotInfo[0].equals("TU")) {
                        addSlot(true, slotInfo, TUESDAY);
                    }
                    // Friday specific slots
                    else if(slotInfo[0].equals("FR")) {
                        addSlot(true, slotInfo, FRIDAY);
                    }
                }
                break;
            case "games:":
                // Starts at 1 to skip header
                for(int i = 1; i < lines.length; i++) {
                    // Read each line and assign them into the games list
                    String[] slotInfo = lines[i].split(" ");
                    // Dealing with the age group and tier
                    String ageAndTier = slotInfo[1];
                    String[] ageTier = extractAgeAndTier(ageAndTier);
                    String age = ageTier[0];
                    String tier = ageTier[1];
                    // Dealing with division
                    int divFinal = Integer.parseInt(slotInfo[3]); // Should always be here
                    // Dealing with the type of event
                    boolean pracOrGame = true; // game by default
                    // Create id for game
                    String id = "game" + i; 
                    boolean isCMSA = slotInfo[0].trim().equals("CMSA");
                    boolean ageIs12or13 = age.trim().equals("U12") || age.trim().equals("U13");
                    boolean tierIs1 = tier.trim().equals("T1");
                    if(isCMSA && ageIs12or13 && tierIs1) {
                        String name = lines[i] + "s";
                        specialEvents.add(new Event(name, slotInfo[0], age, tier, divFinal, pracOrGame, id));
                    }
                    // Add the final game
                    games.add(new Event(lines[i], slotInfo[0], age, tier, divFinal, pracOrGame, id));
                }
                break;
            case "practices:":
                for(int i = 1; i < lines.length; i++) {
                    // Read each line and assign them into the games list
                    String[] slotInfo = lines[i].split(" ");
                    // Dealing with the age group and tier
                    String ageAndTier = slotInfo[1];
                    String[] ageTier = extractAgeAndTier(ageAndTier);
                    String age = ageTier[0];
                    String tier = ageTier[1];
                    // Dealing with division, for practices div can be in different positions
                    String div = "0"; // Default for no division given, all divs share practice
                    for(int j = 0; j < slotInfo.length; j++) {
                        if (slotInfo[j].equals("DIV")) {
                            div = slotInfo[j+1]; // Should be safe, always next entry
                        }
                    }
                    int divFinal = Integer.parseInt(div);
                    // Dealing with the type of event
                    boolean pracOrGame = false; // prac is false
                    // create id for practice
                    String id = "prac" + i;
                    // Add the final practice
                    practices.add(new Event(lines[i], slotInfo[0], age, tier, divFinal, pracOrGame, id));
                }
                break;
            case "not compatible:":
                for(int i = 1; i < lines.length; i++) {
                    // Read each line and assign them into the games list
                    String[] slotInfo = lines[i].split(",");
                    if(slotInfo.length != 2) continue; // Make sure there are two events in this line
                    trimInput(slotInfo); // Get rid of whitespace
                    boolean isPractice = false;
                    String eventName1 = slotInfo[0];
                    String eventName2 = slotInfo[1];
                    Event event1 = null;
                    Event event2 = null;
                    // Handle the events, find the reference in games or practices
                    isPractice = isPractice(eventName1);
                    event1 = getEvent(isPractice, eventName1);
                    isPractice = isPractice(eventName2);
                    event2 = getEvent(isPractice, eventName2);
                    // Check for if one of the events does not exist in games/practices
                    if(event1 == null || event2 == null) {
                        System.out.println("Event does not exist in possible Events, skipping");
                        System.out.println(eventName1); System.out.println(eventName2);
                        System.exit(0);
                    }
                    // Add hashmap entry for event 1 -> event 2
                    if(ncMap.get(event1) == null) {
                        HashSet<Event> mapVal = new HashSet<Event>();
                        mapVal.add(event2);
                        ncMap.put(event1, mapVal);
                    }
                    else {
                        ncMap.get(event1).add(event2);
                    }
                    // Add hashmap entry for event 2 -> event 1
                    if(ncMap.get(event2) == null) {
                        HashSet<Event> mapVal = new HashSet<Event>();
                        mapVal.add(event1);
                        ncMap.put(event2, mapVal);
                    }
                    else {
                        ncMap.get(event2).add(event1);
                    }
                }
                break;
            case "unwanted:":
                // Data container = HashMap<Event, HashSet<Slot>> unwantMap
                for(int i = 1; i < lines.length; i++) {
                    // Split should be Event info, Slot Day, Slot Time
                    String[] slotInfo = lines[i].split(",");
                    if(slotInfo.length != 3) {
                        System.out.println("Unwanted does not have 3 items, skipping line");
                        // System.exit(0); // Make sure there are an event, slot day, and slot time
                    }
                    trimInput(slotInfo); // Get rid of whitespace
                    boolean isPractice = false;
                    String eventName = slotInfo[0]; 
                    String slotDay = slotInfo[1];
                    int slotTime = Integer.parseInt((slotInfo[2]).replace(":", ""));
                    Event event = null;
                    Slot slot = null;
                    // Handle the event
                    isPractice = isPractice(eventName);
                    event = getEvent(isPractice, eventName);
                    //Handle the Slot
                    slot = getSlot(isPractice, slotDay, slotTime);
                    // Check for if one of the events does not exist in games/practices
                    if(event == null || slot == null) {
                        System.out.println("Event or slot does not exist in possible unwanted, skipping");
                        continue;
                        //System.exit(0); // exit behaviour for now
                    }
                    // Add hashmap entry for event -> Slot
                    if(unwantMap.get(event) == null) {
                        HashSet<Slot> mapVal = new HashSet<Slot>();
                        mapVal.add(slot);
                        unwantMap.put(event, mapVal);
                    }
                    else {
                        unwantMap.get(event).add(slot);
                    }
                }
                break;
            case "preferences:":
                // Form = HashMap<Event, ArrayList<Object[]>> preferMap;
                // Object because there is a Slot and preference value
                for(int i = 1; i < lines.length; i++) {
                    String[] slotInfo = lines[i].split(",");
                    if(slotInfo.length != 4) {
                        System.out.println("Preference does not have 4 entries, skipping line");
                        // System.exit(0); // Make sure there are two events
                    }
                    trimInput(slotInfo); // Get rid of whitespace
                    boolean isPractice = false;
                    String eventName = slotInfo[2]; 
                    String slotDay = slotInfo[0];
                    int slotTime = Integer.parseInt((slotInfo[1]).replace(":", ""));
                    int prefVal = Integer.parseInt((slotInfo[3]));
                    Event event = null;
                    Slot slot = null;
                    // Handle the Event
                    isPractice = isPractice(eventName);
                    event = getEvent(isPractice, eventName);
                    //Handle the Slot
                    slot = getSlot(isPractice, slotDay, slotTime);
                    // Check for if one of the entries does not exist
                    if(event == null || slot == null) {
                        System.out.println("Event or slot does not exist for preference, skipping");
                        System.out.println(eventName); System.out.println(slotDay); System.out.println(slotTime); 
                        continue;
                        //System.exit(0); // exit behaviour for now
                    }
                    // Add hashmap entry for event -> Object[2](slot,prefVal)
                    if(preferMap.get(event) == null) {
                        ArrayList<Object[]> mapVal = new ArrayList<Object[]>();
                        Object[] slotAndPref = new Object[2];
                        slotAndPref[0] = slot;
                        slotAndPref[1] = prefVal;
                        mapVal.add(slotAndPref);
                        preferMap.put(event, mapVal);
                    }
                    else {
                        Object[] slotAndPref = new Object[2];
                        slotAndPref[0] = slot;
                        slotAndPref[1] = prefVal;
                        preferMap.get(event).add(slotAndPref);
                    }
                }
                break;
            case "pair:":
                for(int i = 1; i < lines.length; i++) {
                    // Read each line and assign them into the games list
                    String[] slotInfo = lines[i].split(",");
                    if(slotInfo.length != 2) {
                        System.out.println("Pair does not have two Events, skipping line");
                        continue; // Make sure there are two events
                    }
                    trimInput(slotInfo); // Get rid of whitespace
                    boolean isPractice = false;
                    String eventName1 = slotInfo[0]; 
                    String eventName2 = slotInfo[1];
                    Event event1 = null;
                    Event event2 = null;
                    // Handle the events, find the reference in games or practices
                    isPractice = isPractice(eventName1);
                    event1 = getEvent(isPractice, eventName1);
                    isPractice = isPractice(eventName2);
                    event2 = getEvent(isPractice, eventName2);
                    // Check for if one of the events does not exist in games/practices
                    if(event1 == null || event2 == null) {
                        System.out.println("Event does not exist in possible Events, skipping");
                        continue;
                        //System.exit(0); // exit behaviour for now
                    }
                    // Add hashmap entry for event 1 -> event 2
                    if(pairMap.get(event1) == null) {
                        ArrayList<Event> mapVal = new ArrayList<Event>();
                        mapVal.add(event2);
                        pairMap.put(event1, mapVal);
                    }
                    else {
                        pairMap.get(event1).add(event2);
                    }
                    // Add hashmap entry for event 2 -> event 1
                    if(pairMap.get(event2) == null) {
                        ArrayList<Event> mapVal = new ArrayList<Event>();
                        mapVal.add(event1);
                        pairMap.put(event2, mapVal);
                    }
                    else {
                        pairMap.get(event2).add(event1);
                    }
                }
                break;
            case "partial assignments:":
                for(int i = 1; i < lines.length; i++) {
                    // Split should be Event info, Slot Day, Slot Time
                    String[] slotInfo = lines[i].split(",");
                    if(slotInfo.length != 3) {
                        System.out.println("Partial Assignments does not have 3 items, skipping line");
                        // System.exit(0); // Make sure there are an event, slot day, and slot time
                    }
                    trimInput(slotInfo); // Get rid of whitespace
                    boolean isPractice = false;
                    String eventName = slotInfo[0]; 
                    String slotDay = slotInfo[1];
                    int slotTime = Integer.parseInt((slotInfo[2]).replace(":", ""));
                    Event event = null;
                    Slot slot = null;
                    // Handle the event
                    isPractice = isPractice(eventName);
                    event = getEvent(isPractice, eventName);
                    //Handle the Slot
                    slot = getSlot(isPractice, slotDay, slotTime);
                    // Check for if one of the events does not exist in games/practices
                    if(event == null || slot == null) {
                        System.out.println("Event or slot does not exist in possible partial assignments, skipping");
                        continue;
                        //System.exit(0);
                        //System.exit(0); Not exit behaviour for now
                    }
                    // Add hashmap entry for event -> Slot
                    // Should only be one entry
                    if(paMap.get(event) == null) {
                        paMap.put(event, slot);
                    }
                }
                break;
        }
    }

    /*
     * A method for retrieving an event from either practice or games 
     * Takes in a Boolean to tell if a practice or game is wanted
     * and an Identifier (event name) to uniquely identify the wanted event
     */
    public Event getEvent(boolean isPractice, String identifier) {
        Event event = null;
        if(isPractice) {
            for(int j = 0; j < practices.size(); j++) {
                String pracName = practices.get(j).name.trim();
                if(pracName.equals(identifier.trim()) ) {
                    event = practices.get(j);
                }
                // Search for a substitutable PRC or OPN
                //else if(substitutePrcOpn(pracName).equals(identifier.trim())) {
                //    event = practices.get(j);
                //}
            }
        }
        else {
            for(int j = 0; j < games.size(); j++) {
                String gameName = games.get(j).name.trim();
                if(gameName.equals(identifier.trim()) ) {
                    event = games.get(j);
                }
            }
        }
        // Null check
        //if(event == null) {
          //  System.out.println("getEvent could not find a matching Event, exiting program");
          //  System.out.println(identifier);
          //  System.exit(0);
        //}
        return event;
    }

    /*
     * A method for retrieving a slot from the appropriate slot list
     * Takes in a Boolean to tell if a practice or game is wanted
     * and a day (slot day) and a time to uniquely identify the slot
     */
    public Slot getSlot(boolean isPractice, String slotDay, int slotTime) {
        // If the event is a practice it must be in practice slots
        if(isPractice) {
            if(slotDay.trim().equals("MO")) {
                for(int j = 0; j < m_prac_slots.size(); j++) {
                    // Same day and time slot should be identifier
                    if(m_prac_slots.get(j).startTime == slotTime) {
                        return m_prac_slots.get(j);
                    }
                }
            }
            else if(slotDay.trim().equals("TU")) {
                for(int j = 0; j < t_prac_slots.size(); j++) {
                    // Same day and time slot should be identifier
                    if(t_prac_slots.get(j).startTime == slotTime) {
                        return t_prac_slots.get(j);
                    }
                }
            }
            // Must be Friday
            else {
                for(int j = 0; j < f_prac_slots.size(); j++) {
                    // Same day and time slot should be identifier
                    if(f_prac_slots.get(j).startTime == slotTime) {
                        return f_prac_slots.get(j);
                    }
                }
            }
        }
        // Must be games then
        else {
            if(slotDay.trim().equals("MO")) {
                for(int j = 0; j < m_game_slots.size(); j++) {
                    // Same day and time slot should be identifier
                    if(m_game_slots.get(j).startTime == slotTime) {
                        return m_game_slots.get(j);
                    }
                }
            }
            // Must be Tuesday
            else {
                for(int j = 0; j < t_game_slots.size(); j++) {
                    // Same day and time slot should be identifier
                    if(t_game_slots.get(j).startTime == slotTime) {
                        return t_game_slots.get(j);
                    }
                }
            }
        }
        //System.out.println("Slot was not found in possible slots, exiting program");
        //System.out.println(slotDay);System.out.println(slotTime);
        //System.exit(0);
        Slot slot = null;// Will never be reached, only for compiler
        return slot;
    }

    /*
     * A method for deciding if an unknown event is a practice or not
     * Takes in the event name, splits it apart and looks for PRC or OPN
     * to decide if the event is a practice 
     */
    public boolean isPractice(String eventName) {
        String[] eventInfo = eventName.split(" ");
        for(int i = 0; i < eventInfo.length; i++) {
            if(eventInfo[i].trim().equals("PRC") || eventInfo[i].trim().equals("OPN")) {
                return true;
            }
        }
        return false;
    }

    /*
     * A method for creating a slot 
     * Takes in a boolean for identifying practice and the slot info 
     * as well as a daycode to identify day
     */
    public void addSlot(boolean isPractice, String[] slotInfo, int dayCode) {
        String day = slotInfo[0];
        int startTime = Integer.parseInt(slotInfo[1].replace(":", ""));
        int max = Integer.parseInt(slotInfo[2]); 
        int min = Integer.parseInt(slotInfo[3]); 
        boolean special = false; 
        String idName = "(" + day + "," + startTime + ")";
        if(isPractice) {
            if(dayCode == MONDAY) {
                int endTime = startTime + 100; // Add in an hour          
                m_prac_slots.add(new Slot(day, startTime, endTime, max, min, special, idName));
            }
            else if(dayCode == TUESDAY) {
                if(startTime == 1800) special = true; // Special showcase practice
                int endTime = startTime + 100; // Add in an hour    
                t_prac_slots.add(new Slot(day, startTime, endTime, max, min, special, idName));
            }
            else if(dayCode == FRIDAY) {
                int endTime = startTime + 200; // Add in two hours    
                f_prac_slots.add(new Slot(day, startTime, endTime, max, min, special, idName));
            }
        }
        else {
            if(dayCode == MONDAY) {
                int endTime = startTime + 100; // Add in an hour          
                m_game_slots.add(new Slot(day, startTime, endTime, max, min, special, idName));
            }
            else if(dayCode == TUESDAY) {
                if(startTime == 1100) special = true; // Special league-wide meeting at 11
                int endTime = 0; // Defualt, should be replaced
                // Get last two digits from string, Max checks for length less than 2
                int minutes = Integer.parseInt(slotInfo[1].substring(Math.max(slotInfo[1].length()-2, 0)));
                if(minutes == 30) endTime = startTime + 200 - 30; // Add in an hour and a half to 30 min
                else endTime = startTime + 100 + 30; // Add in an hour and a half to flat hour
                t_game_slots.add(new Slot(day, startTime, endTime, max, min, special, idName));
            }
        }
    }

    /*
     * A method to trim off whitespace for each entry in a given array of info
     */
    public void trimInput(String[] toTrim) {
        for(int i = 0; i < toTrim.length; i++) {
            toTrim[i] = toTrim[i].trim();
        }
    }

    /*
     * A method to extract the age and tier out of their combined string
     */
    public String[] extractAgeAndTier(String ageAndTier) {
        String[] ageTier = new String[2];
        if(ageAndTier.length() == 3) { // if there is no tier
            ageTier[0] = ageAndTier.substring(0, 3);
            ageTier[1] = "T0"; // DEFAULT FOR NOW, SUBJECT TO CHANGE
        }
        else {
            // Assume age group always has 2 numbers after the U, so 3 letters in
            ageTier[0] = ageAndTier.substring(0, 3);
            ageTier[1] = ageAndTier.substring(3); // Can contain special S charcter
        }
        // terminate if either age or tier are null , commented out for now, assumption is to assign T0
        // if(ageTier[0] == null || ageTier[1] == null) {
        //     System.out.println("Age and Tier was null, exiting");
        //     System.exit(0);
        // }
        return ageTier;
    }

    /*
     * Method to swap out the PRC and OPN tags for one another for searching purposes
     */
    public String substitutePrcOpn(String s) {
        String retString = "";
        if(s.length() <= 1) System.out.println("Something is wrong with the string size, too low");
        String[] pracInfo = s.split(" ");
        for(int i = 0; i < pracInfo.length; i++) {
            if(pracInfo[i].trim().equals("PRC")) {
                retString = retString + "OPN "; // Space is important
            }
            else if(pracInfo[i].trim().equals("OPN")) {
                retString = retString + "PRC "; // Space is important
            }
            else {
                retString = retString + pracInfo[i].trim() + " ";
            }
        }
        return retString.trim();
    }

}