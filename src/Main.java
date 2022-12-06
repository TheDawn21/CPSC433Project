

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
}
