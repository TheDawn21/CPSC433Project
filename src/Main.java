

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
                System.err.println("Argument error");
                System.exit(1);
            }
            
        } else {
            System.out.println("Invalid input");
        }
    }

    public static void testParserOutput(Parser p) {
        System.out.println(p.exampleName);
        System.out.println(p.m_game_slots);
        System.out.println(p.t_game_slots);
        System.out.println(p.m_prac_slots);
        System.out.println(p.t_prac_slots);
        System.out.println(p.f_prac_slots);

        System.out.println(p.games);
        System.out.println(p.practices);

        System.out.println(p.ncMap);
        System.out.println(p.unwantMap);
        System.out.println(p.preferMap);
        System.out.println(p.pairMap);
        System.out.println(p.paMap);
    }
}
