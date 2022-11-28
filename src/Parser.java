import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
    ArrayList<NotCompatible> ncList;
    ArrayList<Unwanted> unwants;
    ArrayList<Prefered> prefers;
    ArrayList<Pair> pairs;
    ArrayList<PartAssign> paList;

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
                    if(slotInfo[0] == "MO"){
                        String day = slotInfo[0];
                        String startTime = slotInfo[1];
                        Integer startHour = Integer.parseInt((slotInfo[1].split(":"))[0])+1;
                        String endTime = (startHour).toString() +":"+ (slotInfo[1].split(":"))[1];
                        int gameMax = Integer.parseInt(slotInfo[2]); 
                        int gameMin = Integer.parseInt(slotInfo[3]); 
                        // Special must be false on mondays
                        m_game_slots.add(new Slot(day, startTime, endTime, gameMax, gameMin, false));
                    }
                    // Tuesday specific slots
                    else if(slotInfo[0] == "TU") {
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
                        if(startMins==30){
                            startHour += 2;
                            endTime = (startHour).toString() +":00";
                        }
                        else{
                            startHour+=1;
                            endTime = (startHour).toString() +":30";
                        }
                        int gameMax = Integer.parseInt(slotInfo[2]); 
                        int gameMin = Integer.parseInt(slotInfo[3]); 
                        m_game_slots.add(new Slot(day, startTime, endTime, gameMax, gameMin, special));
                    }
                }
            case "practice slots":
                // Starts at 1 to skip header
                for(int i = 1; i < lines.length; i++) {
                    // Read each line and split them based on days
                    String[] slotInfo = lines[i].split(",");
                    // This should trim all whitespace around the line coming from spaces after commas
                    for(int j = 0; i < slotInfo.length; i++) {
                        slotInfo[j] = slotInfo[j].trim();
                    }
                    // Monday specific slots
                    if(slotInfo[0] == "MO"){
                        String day = slotInfo[0];
                        String startTime = slotInfo[1];
                        Integer startHour = Integer.parseInt((slotInfo[1].split(":"))[0])+1;
                        String endTime = (startHour).toString() +":"+ (slotInfo[1].split(":"))[1];
                        int gameMax = Integer.parseInt(slotInfo[2]); 
                        int gameMin = Integer.parseInt(slotInfo[3]); 
                        // Special must be false on mondays
                        m_game_slots.add(new Slot(day, startTime, endTime, gameMax, gameMin, false));
                    }
                    // Tuesday specific slots
                    else if(slotInfo[0] == "TU") {
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
                        if(startMins==30){
                            startHour += 2;
                            endTime = (startHour).toString() +":00";
                        }
                        else{
                            startHour+=1;
                            endTime = (startHour).toString() +":30";
                        }
                        int gameMax = Integer.parseInt(slotInfo[2]); 
                        int gameMin = Integer.parseInt(slotInfo[3]); 
                        m_game_slots.add(new Slot(day, startTime, endTime, gameMax, gameMin, special));
                    }
                }
            case "games":
            case "practices":
            case "not compatible":
            case "unwanted":
            case "preferences":
            case "pair":
            case "partial assignments":
        }



    }

}
