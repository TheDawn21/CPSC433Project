import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.HashMap;

public class Parser {
    
    String exampleName;
    File file;
    Scanner scan;
    // All parser arrays on pictures
    ArrayList<Slot> m_game_slots;
    ArrayList<Slot> t_game_slots;
    ArrayList<Slot> m_prac_slots;
    ArrayList<Slot> t_prac_slots;
    ArrayList<Slot> f_prac_slots;
    ArrayList<Event> games;
    ArrayList<Event> practices;
    // Hashmaps
    HashMap<Event, HashSet<Event>> ncMap;// = new HashMap<Event, ArrayList<Event>>();
    HashMap<Event, HashSet<Slot>> unwantMap;
    HashMap<Event, ArrayList<Object[]>> preferMap;
    HashMap<Event, ArrayList<Event>> pairMap;
    HashMap<Event, Slot> paMap;

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
            if(s.length() == 0) {

            }
            else{

            }
        }

    }

    public void openFile(String filename) {
        try {
            file = new File(filename);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Fill list will fill the correct array given a String buffer which are a section of the file
    public void fillList(String buff) {
        String[] lines = buff.split("\n");
        if(lines.length <= 1) return;
        // Switch based on the section head in the file, lowercase only
        switch(lines[0].toLowerCase()){
            case "name":
                exampleName = lines[1];
            case "game slots":
                // Starts at 1 to skip header
                for(int i = 1; i < lines.length; i++) {
                    // Read each line and split them based on days
                    String[] slotInfo = lines[i].split(",");
                    // This should trim all whitespace around the line coming from spaces after commas
                    for(int j = 0; i < slotInfo.length; i++) {
                        slotInfo[j] = slotInfo[j].trim();
                    }
                    // Monday specific slots
                    if(slotInfo[0].equals("MO")){
                        String day = slotInfo[0];
                        String startTime = slotInfo[1];
                        Integer startHour = Integer.parseInt((slotInfo[1].split(":"))[0])+1;
                        String endTime = (startHour).toString() +":"+ (slotInfo[1].split(":"))[1];
                        int gameMax = Integer.parseInt(slotInfo[2]); 
                        int gameMin = Integer.parseInt(slotInfo[3]); 
                        // Special must be false on mondays
                        boolean special = false; 
//                        m_game_slots.add(new Slot(day, startTime, endTime, gameMax, gameMin, special));
                    }
                    // Tuesday specific slots
                    else if(slotInfo[0].equals("TU")) {
                        // IF a game slot is 11-12:30 Tues/Thurs don't include in available slots?
                        String day = slotInfo[0];
                        String startTime = slotInfo[1];
                        String endTime;
                        Integer startHour = Integer.parseInt((slotInfo[1].split(":"))[0]);
                        Integer startMins = Integer.parseInt((slotInfo[1].split(":"))[1]);
                        boolean special = false;
                        // Account for a special game slot, the only special game slot
                        if(startHour == 11) special = true;
                        // MAke the end time for which the minutes will flip and hour will be 1 or 2 hours ahead
                        if(startMins == 30){
                            startHour += 2;
                            endTime = (startHour).toString() +":00";
                        }
                        else{
                            startHour+=1;
                            endTime = (startHour).toString() +":30";
                        }
                        int gameMax = Integer.parseInt(slotInfo[2]); 
                        int gameMin = Integer.parseInt(slotInfo[3]); 
//                        t_game_slots.add(new Slot(day, startTime, endTime, gameMax, gameMin, special));
                    }
                }
            case "practice slots":
                // Starts at 1 to skip header
                for(int i = 1; i < lines.length; i++) {
                    // Read each line and separate them based on days
                    String[] slotInfo = lines[i].split(",");
                    // This should trim all whitespace around the line coming from spaces after commas
                    for(int j = 0; i < slotInfo.length; i++) {
                        slotInfo[j] = slotInfo[j].trim();
                    }
                    // Monday specific slots
                    if(slotInfo[0].equals("MO")){
                        String day = slotInfo[0];
                        String startTime = slotInfo[1];
                        Integer startHour = Integer.parseInt((slotInfo[1].split(":"))[0])+1;
                        String endTime = (startHour).toString() +":"+ (slotInfo[1].split(":"))[1];
                        int gameMax = Integer.parseInt(slotInfo[2]); 
                        int gameMin = Integer.parseInt(slotInfo[3]); 
                        // Special must be false on mondays
                        boolean special = false; 
//                        m_prac_slots.add(new Slot(day, startTime, endTime, gameMax, gameMin, special));
                    }
                    // Tuesday specific slots
                    else if(slotInfo[0].equals("TU")) {
                        // IF a game slot is 11-12:30 Tues/Thurs don't include in available slots?
                        String day = slotInfo[0];
                        String startTime = slotInfo[1];
                        Integer startHour = Integer.parseInt((slotInfo[1].split(":"))[0])+1;
                        String endTime = (startHour).toString() +":"+ (slotInfo[1].split(":"))[1];
                        boolean special = false; 
                        // Account for a special practice slot, the only special practice slot
                        if(startHour == 18) special = true;
                        int gameMax = Integer.parseInt(slotInfo[2]); 
                        int gameMin = Integer.parseInt(slotInfo[3]); 
//                        t_prac_slots.add(new Slot(day, startTime, endTime, gameMax, gameMin, special));
                    }
                    // Friday specific slots
                    else if(slotInfo[0].equals("FR")) {
                        // IF a game slot is 11-12:30 Tues/Thurs don't include in available slots?
                        String day = slotInfo[0];
                        String startTime = slotInfo[1];
                        Integer startHour = Integer.parseInt((slotInfo[1].split(":"))[0])+2; // Add 2 this time for Friday
                        String endTime = (startHour).toString() +":"+ (slotInfo[1].split(":"))[1];
                        // Special must be false on fridays
                        boolean special = false; 
                        int gameMax = Integer.parseInt(slotInfo[2]); 
                        int gameMin = Integer.parseInt(slotInfo[3]); 
//                        f_prac_slots.add(new Slot(day, startTime, endTime, gameMax, gameMin, special));
                    }
                }
            case "games":
                // Starts at 1 to skip header
                for(int i = 1; i < lines.length; i++) {
                    // Read each line and assign them into the games list
                    String[] slotInfo = lines[i].split(" ");
                    // Dealing with the age group and tier
                    String ageAndTier = slotInfo[1];
                    String age;
                    String tier;
                    if(ageAndTier.charAt(0) == 'O') { //Idk if char needs to be .equals
                        age = "O18";
                        tier = "T0"; // DEFAULT FOR NOW, SUBJECT TO CHANGE
                    }
                    else {
                        // Assume age group always has 2 numbers after the U, so 3 letters in
                        age = ageAndTier.substring(0, 3);
                        tier = ageAndTier.substring(3); // Can contain special S charcter
                    }
                    // Dealing with division
                    int divFinal = Integer.parseInt(slotInfo[2]); // Should always be here
                    // Dealing with the type of event
                    boolean pracOrGame = true; // game by default
                    // Add the final game
                    games.add(new Event(lines[i], slotInfo[0], age, tier, divFinal, pracOrGame));
                }
            case "practices":
                for(int i = 1; i < lines.length; i++) {
                    // Read each line and assign them into the games list
                    String[] slotInfo = lines[i].split(" ");
                    // Dealing with the age group and tier
                    String ageAndTier = slotInfo[1];
                    String age;
                    String tier;
                    if(ageAndTier.charAt(0) == 'O') { //Idk if char needs to be .equals
                        age = "O18";
                        tier = "T0"; // DEFAULT FOR NOW, SUBJECT TO CHANGE
                    }
                    else {
                        // Assume age group always has 2 numbers after the U, so 3 letters in
                        age = ageAndTier.substring(0, 3);
                        tier = ageAndTier.substring(3); // Can contain special S charcter
                    }
                    // Dealing with division, for practices div can be in different positions
                    String div = "";
                    for(int j = 0; i < slotInfo.length; i++) {
                        if (slotInfo[j].equals("DIV")) {
                            div = slotInfo[j+1]; // Should be safe, always next entry
                        }
                    }
                    int divFinal = Integer.parseInt(div);
                    // Dealing with the type of event
                    boolean pracOrGame = false; // prac is false
                    // Add the final practice
                    practices.add(new Event(lines[i], slotInfo[0], age, tier, divFinal, pracOrGame));
                }
            case "not compatible":
                for(int i = 1; i < lines.length; i++) {
                    // Read each line and assign them into the games list
                    String[] slotInfo = lines[i].split(",");
                    boolean isPractice = false;
                    String eventName1 = slotInfo[0];
                    String eventName2 = slotInfo[1];
                    Event event1 = null;
                    Event event2 = null;
                    // Handle the first event, find the reference in games or practices
                    String[] eventInfo1 = eventName1.split(" ");
                    for(int k = 0; k < eventInfo1.length; k++) {
                        if(eventInfo1[k].equals("PRC") || eventInfo1[k].equals("OPN")) {
                            isPractice = true;
                        }
                    }
                    if(isPractice) {
                        for(int j = 0; j < practices.size(); j++) {
                            if(practices.get(j).name.equals(eventName1) ) {
                                event1 = practices.get(j);
                            }
                        }
                    }
                    else {
                        for(int j = 0; j < games.size(); j++) {
                            if(games.get(j).name.equals(eventName1) ) {
                                event1 = games.get(j);
                            }
                        }
                    }
                    isPractice = false; // Make sure to reset this value
                    // Handle the second event, find the reference in games or practices
                    String[] eventInfo2 = eventName2.split(" ");
                    for(int k = 0; k < eventInfo2.length; k++) {
                        if(eventInfo2[k].equals("PRC") || eventInfo2[k].equals("OPN")) {
                            isPractice = true;
                        }
                    }
                    if(isPractice) {
                        for(int j = 0; j < practices.size(); j++) {
                            if(practices.get(j).name.equals(eventName2) ) {
                                event2 = practices.get(j);
                            }
                        }
                    }
                    else {
                        for(int j = 0; j < games.size(); j++) {
                            if(games.get(j).name.equals(eventName1) ) {
                                event2 = games.get(j);
                            }
                        }
                    }
                    // Check for if one of the events does not exist in games/practices
                    if(event1 == null || event2 == null) {
                        System.out.println("Event does not exist in possible Events");
                        System.exit(0);
                    }
                    // Add hashmap entry for event 1 -> event 2
                    if(ncMap.get(event1) == null) {
                        ArrayList<Event> mapVal = new ArrayList<Event>();
                        mapVal.add(event2);
//                        ncMap.put(event1, mapVal);
                    }
                    else {
                        ncMap.get(event1).add(event2);
                    }
                    // Add hashmap entry for event 2 -> event 1
                    if(ncMap.get(event2) == null) {
                        ArrayList<Event> mapVal = new ArrayList<Event>();
                        mapVal.add(event1);
//                        ncMap.put(event2, mapVal);
                    }
                    else {
                        ncMap.get(event2).add(event1);
                    }
                }
            case "unwanted":
            case "preferences":
            case "pair":
            case "partial assignments":
        }
    }
}
