import java.io.*;
import java.util.*;

/**
 * Created by sridharyadav on 10/10/15.
 */
class Board implements Cloneable{
    int number_pits;
    int player1[];
    int player2[];
    int chance;
    String value;
    String name;
    String Alpha;
    String Beta;

    public Board(){

    }
    public Board(Board b) {
        this.number_pits = b.number_pits;
        this.player1 = b.player1.clone();
        this.player2 = b.player2.clone();
        this.chance = b.chance;
        this.name = b.name;
        this.value = b.value;
        this.Alpha = b.Alpha;
        this.Beta = b.Beta;
    }

    public Board(int no_pits) {
        this.number_pits = no_pits;
        this.player1 = new int[no_pits+1];
        this.player2 = new int[no_pits+1];
        this.chance = 0;
        this.name = "";
        this.value = "Infinity";
        this.Alpha = "-Infinity";
        this.Beta = "Infinity";
    }

    public int Eval(int turn){
        return (turn== 1) ? (this.player1[this.number_pits] - this.player2[0]) : (-this.player1[this.number_pits] + this.player2[0]);
    }

    public void GameOver(int player) {
        if(player == 2) {
            for (int i = 0; i < this.number_pits; i++) {
                this.player1[this.number_pits] += this.player1[i];
                this.player1[i] = 0;
            }
        }
        else{
            for (int i = 1; i <= this.number_pits; i++) {
                this.player2[0] += this.player2[i];
                this.player2[i] = 0;
            }
        }
    }
    public int Empty(){
        int a = 0;
        int b = 0;
        for (int i = 0; i < this.number_pits; i++) {
            if (this.player1[i] != 0) {
                a = 0;
                break;
            }
            else{
                a = 1;
            }
        }
        if(a == 1) {
            this.GameOver(1);
            return 1;
        }

        for (int i = 1; i <= this.number_pits; i++) {
            if (this.player2[i] != 0) {
                b = 0;
                break;
            }
            else {
                b = 1;
            }
        }
        if(b == 1){
            this.GameOver(2);
            return 1;
        }


        return 0;
    }
    public int Eq(Board b){
        int flag = 0;
        if((Arrays.equals(this.player1,b.player1)) && (Arrays.equals(this.player2,b.player2))) {
            flag = 1;
        }
        return flag;
    }
}

public class mancala {

    static Board root = new Board();
    static int maximum_value = Integer.MIN_VALUE;
    static int depth = 1;
    static ArrayList<String> traverse = new ArrayList<String>();
    static int play = 1;
    static Board final_state = new Board();
    static Board minmax_state = new Board();
    static int minmax_value = Integer.MIN_VALUE;
    static String tmpBeta = "-Infinity";
    static String tmpAlpha = "-Infinity";
    static String tAlpha = "-Infinity";
    static String tBeta = "-Infinity";


    static int prune = 0;

    static int s_flag = 0;

    public static void main(String[] args) {
            String Filename = args[1];
            if(!Filename.toLowerCase().contains(".txt"))
            Filename = Filename + ".txt";
            String task = "";
            try {
            File file = new File(Filename);
            BufferedReader in = new BufferedReader(new FileReader(file));
            task = in.readLine();
            int player = Integer.parseInt(in.readLine());
            int cutoff_depth = Integer.parseInt(in.readLine());

            String player2_board = in.readLine();
            String player1_board = in.readLine();
            int pits = (player1_board.length() - player1_board.replace(" ", "").length())+1;
            int player2_mancala = Integer.parseInt(in.readLine());
            int player1_mancala = Integer.parseInt(in.readLine());
            Board b = new Board(pits);
            int j;
            for (j = 0; j < b.number_pits; j++)
                b.player1[j] = Integer.parseInt(player1_board.split("\\s+")[j]);
            b.player1[j] = player1_mancala;

            for (int i = 1; i <= b.number_pits; i++)
                b.player2[i] = Integer.parseInt(player2_board.split("\\s+")[i - 1]);

            b.player2[0] = player2_mancala;

            final_state = new Board();
            minmax_state = new Board();
            root = new Board(b);
            root.name = "root";
            switch (task) {
                case "1":
                    if(player==1)
                        Greedy(b, player);
                    else
                        Greedy2(b,player);
                    if(final_state.player1 == null) {
                        final_state = new Board(root);
                    }
                    break;
                case "2":
                    traverse.add("Node,Depth,Value");
                    traverse.add("root,0,-Infinity");
                    if (player == 2) {
                        MaxValue2(b, 0, cutoff_depth, player);
                    } else {
                        MaxValue(b, 0, cutoff_depth, player);
                    }
                    if(minmax_state.player1 == null) {
                        final_state = new Board(root);
                        break;
                    }
                    if (s_flag == 1 || minmax_state.chance == 0) {
                        final_state = new Board(minmax_state);
                        break;
                    } else {
                        final_state = new Board(minmax_state);
                        if (player == 2) {
                            Greedy2(minmax_state, player);
                        }
                        else {
                            Greedy(minmax_state, player);
                        }
                    }
                    break;
                case "3":
                    traverse.add("Node,Depth,Value,Alpha,Beta");
                    traverse.add("root,0,-Infinity,-Infinity,Infinity");
                    if (player == 2) {
                        AlphaMax2(b, 0, cutoff_depth, player);
                    } else {
                        AlphaMax(b, 0, cutoff_depth, player);
                    }
                    if(minmax_state.player1 == null) {
                        final_state = new Board(root);
                        break;
                    }
                    if (s_flag == 1 || minmax_state.chance == 0) {
                        final_state = new Board(minmax_state);
                        break;
                    } else {
                        final_state = new Board(minmax_state);
                        if (player == 2) {
                            Greedy2(minmax_state, player);
                        }
                        else {
                            Greedy(minmax_state, player);
                        }
                    }
                    break;
            }
        } catch (Exception FileNotFoundException) {
            System.out.print(FileNotFoundException);
        }
        if (Integer.parseInt(task) != 1) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter("traverse_log.txt"));
                int p = 1;
                for (String ou : traverse) {
                    out.write(ou);
                    if (p < traverse.size()) {
                        out.newLine();
                    }
                    p++;
                }
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            ArrayList<String> o = new ArrayList<String>();
            String s = "";
            for (int i = 1; i <= final_state.number_pits; i++) {
                s = s + Integer.toString(final_state.player2[i]);
                if (i != final_state.number_pits)
                    s = s + " ";
            }
            o.add(s);
            String m = "";
            for (int i = 0; i < final_state.number_pits; i++) {
                m = m + Integer.toString(final_state.player1[i]);
                if (i != final_state.number_pits)
                    m = m + " ";
            }
            o.add(m);
            o.add(Integer.toString(final_state.player2[0]));
            o.add(Integer.toString(final_state.player1[final_state.number_pits]));
            BufferedWriter out = new BufferedWriter(new FileWriter("next_state.txt"));
            int p = 1;
            for (String ou : o) {
                out.write(ou);
                if (p < o.size()) {
                    out.newLine();
                }
                p++;
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Board reverse(Board b) {
        for (int i = 0; i < b.player1.length / 2; i++) {
            int temp = b.player1[i];
            b.player1[i] = b.player1[b.player1.length - i - 1];
            b.player1[b.player1.length - i - 1] = temp;
        }
        for (int i = 0; i < b.player2.length / 2; i++) {
            int temp = b.player2[i];
            b.player2[i] = b.player2[b.player2.length - i - 1];
            b.player2[b.player2.length - i - 1] = temp;
        }
        int temp[] = new int[b.number_pits + 1];
        temp = b.player1.clone();
        b.player1 = b.player2.clone();
        b.player2 = temp.clone();
        return b;
    }

    public static Board MaxValue2(Board b, int depth, int max_depth, int turn) {
        depth = depth + 1;
        int max_eval = Integer.MIN_VALUE;
        Board next_state = new Board(b);
        for (int k = 1; k <= b.number_pits; k++) {
            Board game = new Board(b);
            game.value = "Infinity";
            if (game.player2[k] != 0) {
                int flag = 1;
                int stones = 0;
                int zero = 0;
                stones = game.player2[k];
                game.player2[k] = 0;
                int man_flag = 1;
                int pit_flag = 0;
                game.chance = 0;
                int m = k;
                while (stones != 0) {
                    if (man_flag != 0) {

                        for (int i = m - 1; i > 0; i--) {
                            if (stones != 0) {
                                game.player2[i] += 1;
                                stones -= 1;
                                if ((stones == 0) && (game.player2[i] == 1)) {
                                    pit_flag = i;
                                }
                            } else {
                                flag = 0;
                                break;
                            }
                        }

                        if ((flag == 0) || (stones == 0)) {
                            man_flag = 0;
                            break;
                        }

                        if (stones != 0) {
                            game.player2[0] += 1;
                            stones -= 1;
                        }
                        if (stones == 0) {
                            game.chance = 1;
                            if (game.Empty() == 1)
                                break;
                            game.name = "A" + Integer.toString(k + 1);
                            String t = game.name + "," + depth + "," + "-Infinity";
                            traverse.add(t);
                            MaxValue2(game, depth - 1, max_depth, turn);
                            break;
                        }
                        for (int i = 0; i < game.number_pits; i++) {
                            if (stones != 0) {
                                game.player1[i] += 1;
                                stones -= 1;
                            } else {
                                flag = 0;
                                break;
                            }
                        }
                        if (stones == 0 || flag == 0) {
                            man_flag = 0;
                            break;
                        }
                        for (int i = game.number_pits; i >= m; i--) {
                            if (stones != 0) {
                                game.player2[i] += 1;
                                stones -= 1;
                                if ((stones == 0) && (game.player2[i] == 1)) {
                                    pit_flag = i;
                                }
                            } else {
                                flag = 0;
                                break;
                            }
                        }
                        if ((flag == 0) || (stones == 0)) {
                            man_flag = 0;
                            break;
                        }
                    }
                    m = k;
                }
                if (pit_flag != 0) {
                    game.player2[pit_flag] = 0;
                    game.player2[0] += game.player1[pit_flag - 1] + 1;
                    game.player1[pit_flag - 1] = 0;
                }
                if (game.Empty() == 1) {

                    if (depth == max_depth && game.chance == 1) {
                        game.name = "A" + Integer.toString(k + 1);
                        String t = game.name + "," + depth + "," + "-Infinity";
                        traverse.add(t);
                    }
                    if (depth < max_depth && game.chance == 1) {
                        game.name = "A" + Integer.toString(k + 1);
                        String t = game.name + "," + depth + "," + "-Infinity";
                        traverse.add(t);
                    }
                    if (depth < max_depth && game.chance == 0) {
                        game.name = "A" + Integer.toString(k + 1);
                        String t = game.name + "," + depth + "," + "Infinity";
                        traverse.add(t);
                    }
                    game.chance = 1;
                    game.name = "A" + Integer.toString(k + 1);
                    String t = game.name + "," + depth + "," + game.Eval(turn);
                    traverse.add(t);
                }


                if (game.Eval(turn) > max_eval && game.chance == 0) {
                    max_eval = game.Eval(turn);
                    next_state = new Board(game);
                }
                if (game.chance == 0) {
                    if (depth < max_depth) {
                        game.name = "A" + Integer.toString(k + 1);
                        String t = game.name + "," + depth + "," + game.value;
                        traverse.add(t);
                    } else {
                        game.name = "A" + Integer.toString(k + 1);
                        String t;
                        t = game.name + "," + depth + "," + game.Eval(turn);
                        traverse.add(t);
                    }
                }

                if (minmax_state.player1 == null) {
                    minmax_state = new Board(game);
                }

                if (b.Eq(root) == 1 && b.value != "Infinity" && game.value != "Infinity") {
                    b.name = "root";
                    if ((game.value != "-Infinity") && (game.value != "Infinity"))
                        b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                    else
                        b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                    String tt = b.name + "," + 0 + "," + b.value;
                    traverse.add(tt);
                    if (Integer.parseInt(b.value) > minmax_value) {
                        minmax_value = Integer.parseInt(b.value);
                        minmax_state = new Board(game);
                    }
                    game = new Board(b);
                    continue;
                }
                int val = 0;
                if ((depth < max_depth) && (game.chance == 0)) {
                    Board sub = new Board(MinValue2(game, depth, max_depth, turn));
                    val = sub.Eval(turn);
                }
                if (depth == max_depth && b.chance == 0) {
                    if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                        if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                            b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                        } else {
                            b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                        }
                    } else {
                        if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                            b.value = game.value;
                        } else {
                            b.value = Integer.toString(game.Eval(turn));
                        }
                    }
                    if (b.Eq(root) == 1) {
                        b.name = "root";
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            } else {
                                b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                            }
                        }
                        traverse.add(b.name + "," + 0 + "," + b.value);
                    } else {
                        traverse.add(b.name + "," + Integer.toString(depth - 1) + "," + b.value);
                    }
                } else {
                    if (b.Eq(root) == 1) {
                        b.name = "root";
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            if((game.value != "-Infinity") && (game.value != "Infinity")){
                                b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            }
                            else
                            {
                                b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                            }
                            traverse.add("root" + "," + 0 + "," + b.value);
                            if (Integer.parseInt(b.value) > minmax_value) {
                                minmax_value = Integer.parseInt(b.value);
                                minmax_state = new Board(game);
                            }
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = game.value;
                            } else {
                                b.value = Integer.toString(game.Eval(turn));
                            }
                            traverse.add("root" + "," + 0 + "," + Integer.parseInt(b.value));
                            if (Integer.parseInt(b.value) > minmax_value) {
                                minmax_value = Integer.parseInt(b.value);
                                minmax_state = new Board(game);
                            }
                        }
                    } else {
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            } else {
                                b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                            }
                            if(b.name.contains("B"))
                                traverse.add(b.name + "," + Integer.toString(depth-1) + "," + b.value);
                            else
                                traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value);
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = game.value;
                            } else {
                                b.value = Integer.toString(game.Eval(turn));
                            }
                            if(b.name.contains("B"))
                                traverse.add(b.name + "," + Integer.toString(depth-1) + "," + b.value);
                            else
                                traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value);
                        }
                        if (b.chance == 1 && depth == 1) {
                            if (Integer.parseInt(b.value) > minmax_value) {
                                minmax_value = Integer.parseInt(b.value);
                                minmax_state = new Board(game);
                                s_flag = 1;
                            }
                        }
                    }
                }
                game = new Board(b);
                if(game.Empty() == 1)
                    break;
            }
        }
        return next_state;
    }

    public static Board MinValue2(Board b, int depth, int max_depth, int turn) {
        depth = depth + 1;
        int min_eval = Integer.MAX_VALUE;
        Board next_state = new Board(b);
        for (int k = 0; k < b.number_pits; k++) {
            Board game = new Board(b);
            game.value = "-Infinity";
            if (game.player1[k] != 0) {
                int flag = 1;
                int stones = 0;
                int zero = 0;
                stones = game.player1[k];
                game.player1[k] = 0;
                int man_flag = 1;
                int pit_flag = -1;
                game.chance = 0;
                int m = k;
                while (stones != 0) {
                    if (man_flag != 0) {
                        for (int i = 1; i <= game.number_pits - m - 1; i++) {
                            if (stones != 0) {
                                game.player1[i + m] += 1;
                                stones -= 1;
                                if ((stones == 0) && (game.player1[m + i] == 1)) {
                                    pit_flag = m + i;
                                }
                            } else {
                                flag = 0;
                                break;
                            }
                        }
                        if (stones == 0 || flag == 0) {
                            man_flag = 0;
                            break;
                        }
                        if (stones != 0) {
                            game.player1[game.number_pits] += 1;
                            stones -= 1;
                        }
                        if (stones == 0) {
                            game.chance = 1;
                            if (game.Empty() == 1)
                                break;
                            game.name = "B" + Integer.toString(k + 2);
                            String t = game.name + "," + depth + "," + "Infinity";
                            traverse.add(t);
                            MinValue2(game, depth - 1, max_depth, turn);
                            break;
                        }
                        for (int i = game.number_pits; i > 0; i--) {
                            if (stones != 0) {
                                game.player2[i] += 1;
                                stones -= 1;
                            } else {
                                flag = 0;
                                break;
                            }
                        }
                        if ((flag == 0) || (stones == 0)) {
                            man_flag = 0;
                            break;
                        }
                    }
                    if (stones != 0) {
                        m = 0;
                        stones -= 1;
                        game.player1[0] += 1;
                        if (stones == 0) {
                            if(game.player1[0]==1) {
                                pit_flag = 0;
                            }
                            flag = 0;
                            break;
                        }
                    }
                    if (flag == 0)
                        break;

                }
                if (pit_flag != -1) {
                    game.player1[pit_flag] = 0;
                    game.player1[game.number_pits] += game.player2[pit_flag + 1] + 1;
                    game.player2[pit_flag + 1] = 0;
                }
                if (game.Empty() == 1) {
                    if (depth == max_depth && game.chance == 1) {
                        game.name = "B" + Integer.toString(k + 2);
                        String t = game.name + "," + depth + "," + "Infinity";
                        traverse.add(t);
                    }
                    if (depth < max_depth && game.chance == 1) {
                        game.name = "B" + Integer.toString(k + 2);
                        String t = game.name + "," + depth + "," + "Infinity";
                        traverse.add(t);
                    }
                    if (depth < max_depth && game.chance == 0) {
                        game.name = "B" + Integer.toString(k + 2);
                        String t = game.name + "," + depth + "," + "-Infinity";
                        traverse.add(t);
                    }
                    game.chance = 1;
                    game.name = "B" + Integer.toString(k + 2);
                    game.value = Integer.toString(game.Eval(turn));
                    String t = game.name + "," + depth + "," + game.Eval(turn);
                    traverse.add(t);
                }
                if (game.chance == 0) {
                    if ((depth < max_depth)) {
                        game.name = "B" + Integer.toString(k + 2);
                        String t = game.name + "," + depth + "," + game.value;
                        traverse.add(t);
                    } else {
                        game.name = "B" + Integer.toString(k + 2);
                        String t = game.name + "," + depth + "," + game.Eval(turn);
                        traverse.add(t);
                    }
                }
                if (game.Eval(turn) < min_eval && game.chance == 0) {
                    min_eval = game.Eval(turn);
                    next_state = new Board(game);
                }
                int val = 0;
                if ((depth < max_depth) && (game.chance == 0)) {
                    Board sub = new Board(MaxValue2(game, depth, max_depth, turn));
                    val = sub.Eval(turn);
                }
                if (depth == max_depth && b.chance == 0) {
                    if (b.name.contains("A") && b.value != "Infinity" && b.value != "-Infinity") {
                        if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                            b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                        }
                        else
                        {
                            b.value = Integer.toString(Math.min(game.Eval(turn),Integer.parseInt(b.value)));
                        }
                    } else {
                        if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                            b.value = game.value;
                        }
                        else{
                            b.value = Integer.toString(game.Eval(turn));
                        }
                    }
                    traverse.add(b.name + "," + Integer.toString(depth - 1) + "," + b.value);
                } else if (b.name.contains("B")) {
                    if (b.chance == 0) {
                        if (b.value != "Infinity" && b.value != "-Infinity") {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            }
                            else
                            {
                                b.value = Integer.toString(Math.min(game.Eval(turn),Integer.parseInt(b.value)));
                            }
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = game.value;
                            }
                            else{
                                b.value = Integer.toString(game.Eval(turn));
                            }
                        }
                        if (game.chance == 1) {
                            if ((game.value != "-Infinity") && (game.value != "Infinity"))
                                b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                        }
                        traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value);
                    } else {
                        if (b.value != "Infinity" && b.value != "-Infinity") {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            }
                            else
                            {
                                b.value = Integer.toString(Math.min(game.Eval(turn),Integer.parseInt(b.value)));
                            }
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = game.value;
                            }
                            else{
                                b.value = Integer.toString(game.Eval(turn));
                            }
                        }
                        traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value);
                    }
                } else {
                    if (b.name.contains("A")) {
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            if ((game.value != "-Infinity") && (game.value != "Infinity"))
                                b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            else
                                b.value = Integer.toString(Math.min(game.Eval(turn), Integer.parseInt(b.value)));
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity"))
                                b.value = game.value;
                            else
                                b.value = Integer.toString(game.Eval(turn));
                        }
                        traverse.add(b.name + "," + Integer.toString(depth - 1) + "," + b.value);
                    } else {
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            b.value = Integer.toString(Math.min(val, Integer.parseInt(b.value)));
                        } else {
                            b.value = Integer.toString(val);
                        }
                        traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value);
                    }
                }
                game = new Board(b);
                if(game.Empty() == 1)
                    break;
            }

        }
        return next_state;

    }

    public static Board Greedy(Board b, int player) {
        int turn = 1;
        int max_eval = Integer.MIN_VALUE;
        Board next_state = new Board();
        for (int k = 0; k < b.number_pits; k++) {
            Board game = new Board(b);
            if (game.player1[k] != 0) {
                int flag = 1;
                int stones = 0;
                int zero = 0;
                stones = game.player1[k];
                game.player1[k] = 0;
                int man_flag = 1;
                int pit_flag = -1;
                game.chance = 0;
                int m = k;
                while (stones != 0) {
                    if (man_flag != 0) {
                        for (int i = 1; i <= game.number_pits - m - 1; i++) {
                            if (stones != 0) {
                                game.player1[i + m] += 1;
                                stones -= 1;
                                if ((stones == 0) && (game.player1[m + i] == 1)) {
                                    pit_flag = m + i;
                                }
                            } else {
                                flag = 0;
                                break;
                            }
                        }
                        if (stones == 0 || flag == 0) {
                            man_flag = 0;
                            break;
                        }
                        if (stones != 0) {
                            game.player1[game.number_pits] += 1;
                            stones -= 1;
                        }
                        if (stones == 0) {
                            if (game.Empty() == 1) {
                                break;
                            }
                            game.chance = 1;
                            Greedy(game, player);
                            break;
                        }
                        for (int i = game.number_pits; i > 0; i--) {
                            if (stones != 0) {
                                game.player2[i] += 1;
                                stones -= 1;
                            } else {
                                flag = 0;
                                break;
                            }
                        }
                        if ((flag == 0) || (stones == 0)) {
                            man_flag = 0;
                            break;
                        }
                    }
                    if (stones != 0) {
                        m = 0;
                        stones -= 1;
                        game.player1[0] += 1;
                        if (stones == 0) {
                            if(game.player1[0]==1) {
                                pit_flag = 0;
                            }
                            flag = 0;
                            break;
                        }
                    }
                    if (flag == 0)
                        break;

                }
                if (pit_flag != -1) {
                    game.player1[pit_flag] = 0;
                    game.player1[game.number_pits] += game.player2[pit_flag + 1] + 1;
                    game.player2[pit_flag + 1] = 0;
                }
                if (game.Empty() == 1) {
                    game.chance = 1;
                }


                if (final_state.player1 == null) {
                    final_state = new Board(game);
                }

                if (game.Eval(turn) > max_eval && game.chance == 0) {
                    max_eval = game.Eval(turn);
                    next_state = new Board(game);
                }
                if (next_state.player1 != null && next_state.Eval(turn) > maximum_value) {
                    maximum_value = next_state.Eval(turn);
                    final_state = new Board(next_state);
                }

            }
            game = new Board(b);
            if(game.Empty() == 1)
                break;
        }
        return next_state;

    }

    public static Board Greedy2(Board b, int player) {
        int turn = 2;
        int max_eval = Integer.MIN_VALUE;
        Board next_state = new Board();
        for (int k = 1; k <= b.number_pits; k++) {
            Board game = new Board(b);
            if (game.player2[k] != 0) {
                int flag = 1;
                int stones = 0;
                int zero = 0;
                stones = game.player2[k];
                game.player2[k] = 0;
                int man_flag = 1;
                int pit_flag = 0;
                game.chance = 0;
                int m = k;
                while (stones != 0) {
                    if (man_flag != 0) {

                        for (int i = m - 1; i > 0; i--) {
                            if (stones != 0) {
                                game.player2[i] += 1;
                                stones -= 1;
                                if ((stones == 0) && (game.player2[i] == 1)) {
                                    pit_flag = i;
                                }
                            } else {
                                flag = 0;
                                break;
                            }
                        }

                        if ((flag == 0) || (stones == 0)) {
                            man_flag = 0;
                            break;
                        }

                        if (stones != 0) {
                            game.player2[0] += 1;
                            stones -= 1;
                        }
                        if (stones == 0) {
                            if (game.Empty() == 1) {
                                break;
                            }
                            game.chance = 1;
                            Greedy2(game, player);
                            break;
                        }
                        for (int i = 0; i < game.number_pits; i++) {
                            if (stones != 0) {
                                game.player1[i] += 1;
                                stones -= 1;
                            } else {
                                flag = 0;
                                break;
                            }
                        }
                        if (stones == 0 || flag == 0) {
                            man_flag = 0;
                            break;
                        }
                        for (int i = game.number_pits; i >= m; i--) {
                            if (stones != 0) {
                                game.player2[i] += 1;
                                stones -= 1;
                                if ((stones == 0) && (game.player2[i] == 1)) {
                                    pit_flag = i;
                                }
                            } else {
                                flag = 0;
                                break;
                            }
                        }
                        if ((flag == 0) || (stones == 0)) {
                            man_flag = 0;
                            break;
                        }
                    }
                    m = k;
                }
                if (pit_flag != 0) {
                    game.player2[pit_flag] = 0;
                    game.player2[0] += game.player1[pit_flag - 1] + 1;
                    game.player1[pit_flag - 1] = 0;
                }
                if (game.Empty() == 1) {
                    game.chance = 1;
                }

                if (final_state.player1 == null) {
                    final_state = new Board(game);
                }
                if (game.Eval(turn) > max_eval && game.chance == 0) {
                    max_eval = game.Eval(turn);
                    next_state = new Board(game);
                }
                if (next_state.player1 != null && next_state.Eval(turn) > maximum_value) {
                    maximum_value = next_state.Eval(turn);
                    final_state = new Board(next_state);
                }
            }
            game = new Board(b);
            if(game.Empty() == 1)
                break;
        }
        return next_state;

    }

    public static Board MaxValue(Board b, int depth, int max_depth, int turn) {
        depth = depth + 1;
        int max_eval = Integer.MIN_VALUE;
        Board next_state = new Board(b);
        for (int k = 0; k < b.number_pits; k++) {
            Board game = new Board(b);
            game.value = "Infinity";
            if (game.player1[k] != 0) {
                int flag = 1;
                int stones = 0;
                int zero = 0;
                stones = game.player1[k];
                game.player1[k] = 0;
                int man_flag = 1;
                int pit_flag = -1;
                game.chance = 0;
                int m = k;
                while (stones != 0) {
                    if (man_flag != 0) {
                        for (int i = 1; i <= game.number_pits - m - 1; i++) {
                            if (stones != 0) {
                                game.player1[i + m] += 1;
                                stones -= 1;
                                if ((stones == 0) && (game.player1[m + i] == 1)) {
                                    pit_flag = m + i;
                                }
                            } else {
                                flag = 0;
                                break;
                            }
                        }
                        if (stones == 0 || flag == 0) {
                            man_flag = 0;
                            break;
                        }
                        if (stones != 0) {
                            game.player1[game.number_pits] += 1;
                            stones -= 1;
                        }
                        if (stones == 0) {
                            game.chance = 1;
                            if (game.Empty() == 1) {
                                break;
                            }
                            game.name = "B" + Integer.toString(k + 2);
                            String t = game.name + "," + depth + "," + "-Infinity";
                            traverse.add(t);
                            MaxValue(game, depth - 1, max_depth, turn);
                            break;
                        }
                        for (int i = game.number_pits; i > 0; i--) {
                            if (stones != 0) {
                                game.player2[i] += 1;
                                stones -= 1;
                            } else {
                                flag = 0;
                                break;
                            }
                        }
                        if ((flag == 0) || (stones == 0)) {
                            man_flag = 0;
                            break;
                        }
                    }
                    if (stones != 0) {
                        m = 0;
                        stones -= 1;
                        game.player1[0] += 1;
                        if (stones == 0) {
                            if(game.player1[0]==1) {
                                pit_flag = 0;
                            }
                            flag = 0;
                            break;
                        }
                    }
                    if (flag == 0)
                        break;

                }
                if (pit_flag != -1) {
                    game.player1[pit_flag] = 0;
                    game.player1[game.number_pits] += game.player2[pit_flag + 1] + 1;
                    game.player2[pit_flag + 1] = 0;
                }
                if (game.Empty() == 1) {
                    if ((depth == max_depth && game.chance == 1)) {
                        game.name = "B" + Integer.toString(k + 2);
                        String t = game.name + "," + depth + "," + "-Infinity";
                        traverse.add(t);
                    }
                    if (depth < max_depth && game.chance == 1) {
                        game.name = "B" + Integer.toString(k + 2);
                        String t = game.name + "," + depth + "," + "-Infinity";
                        traverse.add(t);
                    }
                    if (depth < max_depth && game.chance == 0) {
                        game.name = "B" + Integer.toString(k + 2);
                        String t = game.name + "," + depth + "," + "Infinity";
                        traverse.add(t);
                    }
                    game.chance = 1;
                    game.name = "B" + Integer.toString(k + 2);
                    String t = game.name + "," + depth + "," + game.Eval(turn);
                    traverse.add(t);
                }

                if (game.Eval(turn) > max_eval && game.chance == 0) {
                    max_eval = game.Eval(turn);
                    next_state = new Board(game);
                }
                if (game.chance == 0) {
                    if (depth < max_depth) {
                        game.name = "B" + Integer.toString(k + 2);
                        String t = game.name + "," + depth + "," + game.value;
                        traverse.add(t);
                    } else {
                        game.name = "B" + Integer.toString(k + 2);
                        String t = game.name + "," + depth + "," + game.Eval(turn);
                        traverse.add(t);
                    }
                }
                if (minmax_state.player1 == null) {
                    minmax_state = new Board(game);
                }
                if (b.Eq(root) == 1 && b.value != "Infinity" && game.value != "Infinity") {
                    b.name = "root";
                    if ((game.value != "-Infinity") && (game.value != "Infinity"))
                        b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                    else
                        b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                    String tt = b.name + "," + 0 + "," + b.value;
                    traverse.add(tt);
                    if (Integer.parseInt(b.value) > minmax_value) {
                        minmax_value = Integer.parseInt(b.value);
                        minmax_state = new Board(game);
                    }
                    game = new Board(b);
                    continue;
                }
                int val = 0;
                if ((depth < max_depth) && (game.chance == 0)) {
                    Board sub = new Board(MinValue(game, depth, max_depth, turn));
                    val = sub.Eval(turn);
                }
                if (depth == max_depth && b.chance == 0) {
                    if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                        if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                            b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                        } else {
                            b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                        }
                    } else {
                        if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                            b.value = game.value;
                        } else {
                            b.value = Integer.toString(game.Eval(turn));
                        }
                    }
                    if (b.Eq(root) == 1) {
                        b.name = "root";
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            } else {
                                b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                            }
                        }
                        traverse.add(b.name + "," + 0 + "," + b.value);
                    } else {
                        traverse.add(b.name + "," + Integer.toString(depth - 1) + "," + b.value);
                    }
                } else {
                    if (b.Eq(root) == 1) {
                        b.name = "root";
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            if((game.value != "-Infinity") && (game.value != "Infinity")){
                                b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            }
                            else
                            {
                                b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                            }
                            traverse.add("root" + "," + 0 + "," + b.value);
                            if (Integer.parseInt(b.value) > minmax_value) {
                                minmax_value = Integer.parseInt(b.value);
                                minmax_state = new Board(game);
                            }
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = game.value;
                            } else {
                                b.value = Integer.toString(game.Eval(turn));
                            }
                            traverse.add("root" + "," + 0 + "," + Integer.parseInt(b.value));
                            if (Integer.parseInt(b.value) > minmax_value) {
                                minmax_value = Integer.parseInt(b.value);
                                minmax_state = new Board(game);
                            }
                        }
                    } else {
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            } else {
                                b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                            }
                            if(b.name.contains("A"))
                                traverse.add(b.name + "," + Integer.toString(depth-1) + "," + b.value);
                            else
                                traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value);
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = game.value;
                            } else {
                                b.value = Integer.toString(game.Eval(turn));
                            }
                            if(b.name.contains("A"))
                                traverse.add(b.name + "," + Integer.toString(depth-1) + "," + b.value);
                            else
                                traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value);
                        }
                        if (b.chance == 1 && depth == 1) {
                            if (Integer.parseInt(b.value) > minmax_value) {
                                minmax_value = Integer.parseInt(b.value);
                                minmax_state = new Board(game);
                                s_flag = 1;
                            }
                        }
                    }
                }
                game = new Board(b);
                if(game.Empty() == 1)
                    break;
            }

        }
        return next_state;

    }

    public static Board MinValue(Board b, int depth, int max_depth, int turn) {
        depth = depth + 1;
        int min_eval = Integer.MAX_VALUE;
        Board next_state = new Board(b);
        for (int k = 1; k <= b.number_pits; k++) {
            Board game = new Board(b);
            game.value = "-Infinity";
            if (game.player2[k] != 0) {
                int flag = 1;
                int stones = 0;
                int zero = 0;
                stones = game.player2[k];
                game.player2[k] = 0;
                int man_flag = 1;
                int pit_flag = 0;
                game.chance = 0;
                int m = k;
                while (stones != 0) {
                    if (man_flag != 0) {

                        for (int i = m - 1; i > 0; i--) {
                            if (stones != 0) {
                                game.player2[i] += 1;
                                stones -= 1;
                                if ((stones == 0) && (game.player2[i] == 1)) {
                                    pit_flag = i;
                                }
                            } else {
                                flag = 0;
                                break;
                            }
                        }

                        if ((flag == 0) || (stones == 0)) {
                            man_flag = 0;
                            break;
                        }

                        if (stones != 0) {
                            game.player2[0] += 1;
                            stones -= 1;
                        }
                        if (stones == 0) {
                            game.chance = 1;
                            if (game.Empty() == 1) {
                                break;
                            }
                            game.name = "A" + Integer.toString(k + 1);
                            String t = game.name + "," + depth + "," + "Infinity";
                            traverse.add(t);
                            MinValue(game, depth - 1, max_depth, turn);
                            break;
                        }
                        for (int i = 0; i < game.number_pits; i++) {
                            if (stones != 0) {
                                game.player1[i] += 1;
                                stones -= 1;
                            } else {
                                flag = 0;
                                break;
                            }
                        }
                        if (stones == 0 || flag == 0) {
                            man_flag = 0;
                            break;
                        }
                        for (int i = game.number_pits; i >= m; i--) {
                            if (stones != 0) {
                                game.player2[i] += 1;
                                stones -= 1;
                                if ((stones == 0) && (game.player2[i] == 1)) {
                                    pit_flag = i;
                                }
                            } else {
                                flag = 0;
                                break;
                            }
                        }
                        if ((flag == 0) || (stones == 0)) {
                            man_flag = 0;
                            break;
                        }
                    }
                    m = k;
                }
                if (pit_flag != 0) {
                    game.player2[pit_flag] = 0;
                    game.player2[0] += game.player1[pit_flag - 1] + 1;
                    game.player1[pit_flag - 1] = 0;
                }
                if (game.Empty() == 1) {
                    if (depth == max_depth && game.chance == 1) {
                        game.name = "A" + Integer.toString(k + 1);
                        String t = game.name + "," + depth + "," + "Infinity";
                        traverse.add(t);
                    }
                    if (depth < max_depth && game.chance == 1) {
                        game.name = "A" + Integer.toString(k + 1);
                        String t = game.name + "," + depth + "," + "Infinity";
                        traverse.add(t);
                    }
                    if (depth < max_depth && game.chance == 0) {
                        game.name = "A" + Integer.toString(k + 1);
                        String t = game.name + "," + depth + "," + "-Infinity";
                        traverse.add(t);
                    }
                    game.chance = 1;
                    game.name = "A" + Integer.toString(k + 1);
                    game.value = Integer.toString(game.Eval(turn));
                    String t = game.name + "," + depth + "," + game.Eval(turn);
                    traverse.add(t);
                }
                if (game.chance == 0) {
                    if ((depth < max_depth)) {
                        game.name = "A" + Integer.toString(k + 1);
                        String t = game.name + "," + depth + "," + game.value;
                        traverse.add(t);
                    } else {
                        game.name = "A" + Integer.toString(k + 1);
                        String t = game.name + "," + depth + "," + game.Eval(turn);
                        traverse.add(t);
                    }
                }
                if (game.Eval(turn) < min_eval && game.chance == 0) {
                    min_eval = game.Eval(turn);
                    next_state = new Board(game);
                }
                int val = 0;
                if ((depth < max_depth) && (game.chance == 0)) {
                    Board sub = new Board(MaxValue(game, depth, max_depth, turn));
                    val = sub.Eval(turn);
                }
                if (depth == max_depth && b.chance == 0) {
                    if (b.name.contains("B") && b.value != "Infinity" && b.value != "-Infinity") {
                        if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                            b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                        }
                        else
                        {
                            b.value = Integer.toString(Math.min(game.Eval(turn),Integer.parseInt(b.value)));
                        }
                    } else {
                        if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                            b.value = game.value;
                        }
                        else{
                            b.value = Integer.toString(game.Eval(turn));
                        }
                    }
                    traverse.add(b.name + "," + Integer.toString(depth - 1) + "," + b.value);
                } else if (b.name.contains("A")) {
                    if (b.chance == 0) {
                        if (b.value != "Infinity" && b.value != "-Infinity") {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            }
                            else
                            {
                                b.value = Integer.toString(Math.min(game.Eval(turn),Integer.parseInt(b.value)));
                            }
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = game.value;
                            }
                            else{
                                b.value = Integer.toString(game.Eval(turn));
                            }
                        }
                        if (game.chance == 1) {
                            if ((game.value != "-Infinity") && (game.value != "Infinity"))
                                b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                        }
                        traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value);
                    } else {
                        if (b.value != "Infinity" && b.value != "-Infinity") {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            }
                            else
                            {
                                b.value = Integer.toString(Math.min(game.Eval(turn),Integer.parseInt(b.value)));
                            }
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = game.value;
                            }
                            else{
                                b.value = Integer.toString(game.Eval(turn));
                            }
                        }
                        traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value);
                    }
                } else {
                    if (b.name.contains("B")) {
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            if ((game.value != "-Infinity") && (game.value != "Infinity"))
                                b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            else
                                b.value = Integer.toString(Math.min(game.Eval(turn), Integer.parseInt(b.value)));
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity"))
                                b.value = game.value;
                            else
                                b.value = Integer.toString(game.Eval(turn));
                        }
                        traverse.add(b.name + "," + Integer.toString(depth - 1) + "," + b.value);
                    } else {
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            b.value = Integer.toString(Math.min(val, Integer.parseInt(b.value)));
                        } else {
                            b.value = Integer.toString(val);
                        }
                        traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value);
                    }
                }
                game = new Board(b);
                if(game.Empty() == 1)
                    break;
            }

        }
        return next_state;
    }

    public static Board AlphaMax(Board b, int depth, int max_depth, int turn) {
        depth = depth + 1;
        int max_eval = Integer.MIN_VALUE;
        Board next_state = new Board(b);
        for (int k = 0; k < b.number_pits; k++) {
            Board game = new Board(b);
            game.value = "Infinity";
            if (game.player1[k] != 0) {
                int flag = 1;
                int stones = 0;
                int zero = 0;
                stones = game.player1[k];
                game.player1[k] = 0;
                int man_flag = 1;
                int pit_flag = -1;
                game.chance = 0;
                int m = k;
                while (stones != 0) {
                    if (man_flag != 0) {
                        for (int i = 1; i <= game.number_pits - m - 1; i++) {
                            if (stones != 0) {
                                game.player1[i + m] += 1;
                                stones -= 1;
                                if ((stones == 0) && (game.player1[m + i] == 1)) {
                                    pit_flag = m + i;
                                }
                            } else {
                                flag = 0;
                                break;
                            }
                        }
                        if (stones == 0 || flag == 0) {
                            man_flag = 0;
                            break;
                        }
                        if (stones != 0) {
                            game.player1[game.number_pits] += 1;
                            stones -= 1;
                        }
                        if (stones == 0) {
                            game.chance = 1;
                            if (game.Empty() == 1) {
                                game.chance = 0;
                                break;
                            }
                            game.name = "B" + Integer.toString(k + 2);
                            String t = game.name + "," + depth + "," + "-Infinity" + "," + game.Alpha + "," + game.Beta;
                            traverse.add(t);
                            AlphaMax(game, depth - 1, max_depth, turn);
                            break;
                        }
                        for (int i = game.number_pits; i > 0; i--) {
                            if (stones != 0) {
                                game.player2[i] += 1;
                                stones -= 1;
                            } else {
                                flag = 0;
                                break;
                            }
                        }
                        if ((flag == 0) || (stones == 0)) {
                            man_flag = 0;
                            break;
                        }
                    }
                    if (stones != 0) {
                        m = 0;
                        stones -= 1;
                        game.player1[0] += 1;
                        if (stones == 0) {
                            if(game.player1[0]==1) {
                                pit_flag = 0;
                            }
                            flag = 0;
                            break;
                        }
                    }
                    if (flag == 0)
                        break;

                }
                if (pit_flag != -1) {
                    game.player1[pit_flag] = 0;
                    game.player1[game.number_pits] += game.player2[pit_flag + 1] + 1;
                    game.player2[pit_flag + 1] = 0;
                }

                if (game.Empty() == 1) {
                    if (depth == max_depth && game.chance == 1) {
                        game.name = "B" + Integer.toString(k + 2);
                        String t = game.name + "," + depth + "," + "-Infinity" + "," + game.Alpha + "," + game.Beta;
                        traverse.add(t);
                    }
                    if (depth < max_depth && game.chance == 1) {
                        game.name = "B" + Integer.toString(k + 2);
                        String t = game.name + "," + depth + "," + "-Infinity" + "," + game.Alpha + "," + game.Beta;
                        traverse.add(t);
                    }
                    if (depth < max_depth && game.chance == 0) {
                        game.name = "B" + Integer.toString(k + 2);
                        String t = game.name + "," + depth + "," + "Infinity" + "," + game.Alpha + "," + game.Beta;
                        traverse.add(t);
                    }
                    game.chance = 1;
                    game.name = "B" + Integer.toString(k + 2);
                    if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                        game.value = Integer.toString(Math.max(Integer.parseInt(game.value), game.Eval(turn)));
                    } else {
                        game.value = Integer.toString(game.Eval(turn));
                    }
                    String t = game.name + "," + depth + "," + game.Eval(turn) + "," + game.Alpha + "," + game.Beta;
                    traverse.add(t);
                }

                if (game.Eval(turn) > max_eval && game.chance == 0) {
                    max_eval = game.Eval(turn);
                    next_state = new Board(game);
                }

                if (game.chance == 0) {
                    if (depth < max_depth) {
                        game.name = "B" + Integer.toString(k + 2);
                        if (game.Alpha != "-Infinity" && game.Alpha != "Infinity" && game.value != "Infinity" && game.value != "-Infinity") {
                            game.Alpha = Integer.toString(Math.max(Integer.parseInt(game.Alpha), Integer.parseInt(game.value)));
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity"))
                                game.Alpha = game.value;
                        }
                        String t = game.name + "," + depth + "," + game.value + "," + game.Alpha + "," + game.Beta;
                        traverse.add(t);
                    } else {
                        game.name = "B" + Integer.toString(k + 2);
                        String t = game.name + "," + depth + "," + game.Eval(turn) + "," + game.Alpha + "," + game.Beta;
                        traverse.add(t);
                        game.value = Integer.toString(game.Eval(turn));
                    }
                }

                if (minmax_state.player1 == null) {
                    minmax_state = new Board(game);
                }



                if ((game.value != "-Infinity") && (game.value != "Infinity") && (b.Beta != "-Infinity") && (b.Beta != "Infinity")) {
                    if (Integer.parseInt(b.Beta) <= Integer.parseInt(game.value)) {
                        prune = 1;
                        if (b.name.contains("A"))
                            depth = depth - 1;
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            } else {
                                b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                            }
                            traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = game.value;
                            } else {
                                b.value = Integer.toString(game.Eval(turn));
                            }
                            traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                        }
                        return next_state;
                    } else {
                        prune = 0;
                    }
                }

                //update parent alpha value
                if (b.Alpha != "-Infinity" && b.Alpha != "Infinity" && game.value != "Infinity" && game.value != "-Infinity")
                    b.Alpha = Integer.toString(Math.max(Integer.parseInt(b.Alpha), Integer.parseInt(game.value)));
                else {
                    if (game.value != "-Infinity" && game.value != "Infinity")
                        b.Alpha = game.value; //otherwise infinity
                }


                int val = 0;
                tmpAlpha = b.Alpha;
                if ((depth < max_depth) && (game.chance == 0)) {
                    Board sub = new Board(BetaMin(game, depth, max_depth, turn));
                    tAlpha = b.Alpha;
                    val = sub.Eval(turn);
                    if ((b.Alpha != "-Infinity") && (b.Alpha != "Infinity") && (sub.Beta != "-Infinity") && (sub.Beta != "Infinity")) {
                        b.Alpha = Integer.toString(Math.max(Integer.parseInt(b.Alpha), Integer.parseInt(sub.Beta)));
                    } else {
                        b.Alpha = sub.Beta;
                    }
                }

                if (prune == 1) {
                    b.Alpha = tmpAlpha;
                }

                next_state.Alpha = b.Alpha;
                next_state.Beta = b.Beta;

                if (b.Eq(root) == 1 && b.value != "Infinity" && b.value != "-Infinity" && game.value != "Infinity" && game.value != "-Infinity") {
                    b.name = "root";
                    if ((game.value != "-Infinity") && (game.value != "Infinity"))
                        b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                    else
                        b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                    if (b.Alpha != "-Infinity" && b.Alpha != "Infinity" && game.value != "Infinity" && game.value != "-Infinity")
                        b.Alpha = Integer.toString(Math.max(Integer.parseInt(b.Alpha), Integer.parseInt(game.value)));
                    else
                        b.Alpha = game.value;
                    String tt = b.name + "," + 0 + "," + b.value + "," + b.Alpha + "," + b.Beta;
                    traverse.add(tt);
                    if (Integer.parseInt(b.value) > minmax_value) {
                        minmax_value = Integer.parseInt(b.value);
                        minmax_state = new Board(game);
                    }
                    game = new Board(b);
                    continue;
                }


                if (depth == max_depth && b.chance == 0) {
                    if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                        if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                            b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                        } else {
                            b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                        }
                    } else {
                        if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                            b.value = game.value;
                        } else {
                            b.value = Integer.toString(game.Eval(turn));
                        }
                    }
                    if (b.Eq(root) == 1) {
                        b.name = "root";
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            } else {
                                b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                            }
                        }
                        traverse.add(b.name + "," + 0 + "," + b.value + "," + b.Alpha + "," + b.Beta);
                    } else {
                        traverse.add(b.name + "," + Integer.toString(depth - 1) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                    }
                } else {
                    if (b.Eq(root) == 1) {
                        b.name = "root";
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            if((game.value != "-Infinity") && (game.value != "Infinity"))
                                b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            else
                                b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                            traverse.add("root" + "," + 0 + "," + b.value + "," + b.Alpha + "," + b.Beta);
                            if ((b.value != "-Infinity") && (b.value != "Infinity") && Integer.parseInt(b.value) > minmax_value && game.chance == 0) {
                                minmax_value = Integer.parseInt(b.value);
                                minmax_state = new Board(game);
                            }
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = game.value;
                            } else {
                                b.value = Integer.toString(game.Eval(turn));
                            }
                            traverse.add("root" + "," + 0 + "," + Integer.parseInt(b.value) + "," + b.Alpha + "," + b.Beta);
                            if (Integer.parseInt(b.value) > minmax_value) {
                                minmax_value = Integer.parseInt(b.value);
                                minmax_state = new Board(game);
                            }
                        }
                    } else {
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            } else {
                                b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                            }
                            if(b.name.contains("A")) {
                                if ((b.Beta != "-Infinity") && (b.Beta != "Infinity") && (game.value != "-Infinity") && (game.value != "Infinity")) {
                                    if (Integer.parseInt(b.Beta) <= Integer.parseInt(game.value)) {
                                        traverse.add(b.name + "," + Integer.toString(depth-1) + "," + b.value + "," + tAlpha + "," + b.Beta);
                                        return next_state;
                                    }
                                }
                                traverse.add(b.name + "," + Integer.toString(depth - 1) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                            }
                            else {
                                if ((b.Beta != "-Infinity") && (b.Beta != "Infinity") && (game.value != "-Infinity") && (game.value != "Infinity")) {
                                    if (Integer.parseInt(b.Beta) <= Integer.parseInt(game.value)) {
                                        traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + tAlpha + "," + b.Beta);
                                        return next_state;
                                    }
                                }
                                traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                            }
                            } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = game.value;
                            } else {
                                b.value = Integer.toString(game.Eval(turn));
                            }
                            if(b.name.contains("A")) {
                                if ((b.Beta != "-Infinity") && (b.Beta != "Infinity") && (game.value != "-Infinity") && (game.value != "Infinity")) {
                                    if (Integer.parseInt(b.Beta) <= Integer.parseInt(game.value)) {
                                        traverse.add(b.name + "," + Integer.toString(depth-1) + "," + b.value + "," + tAlpha + "," + b.Beta);
                                        return next_state;
                                    }
                                }
                                traverse.add(b.name + "," + Integer.toString(depth - 1) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                            }
                            else {
                                if ((b.Beta != "-Infinity") && (b.Beta != "Infinity") && (game.value != "-Infinity") && (game.value != "Infinity")) {
                                    if (Integer.parseInt(b.Beta) <= Integer.parseInt(game.value)) {
                                        traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + tAlpha + "," + b.Beta);
                                        return next_state;
                                    }
                                }
                                traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                            }
                        }
                        if (b.chance == 1 && depth == 1) {
                            if (Integer.parseInt(b.value) > minmax_value) {
                                minmax_value = Integer.parseInt(b.value);
                                minmax_state = new Board(game);
                                s_flag = 1;
                            }
                        }
                    }
                }
                prune = 0;
                game = new Board(b);
                if(game.Empty() == 1)
                    break;
            }

        }
        return next_state;

    }

    public static Board BetaMin(Board b, int depth, int max_depth, int turn) {
        depth = depth + 1;
        int min_eval = Integer.MAX_VALUE;
        Board next_state = new Board(b);
        for (int k = 1; k <= b.number_pits; k++) {
            Board game = new Board(b);
            game.value = "-Infinity";
            if (game.player2[k] != 0) {
                int flag = 1;
                int stones = 0;
                int zero = 0;
                stones = game.player2[k];
                game.player2[k] = 0;
                int man_flag = 1;
                int pit_flag = 0;
                game.chance = 0;
                int m = k;
                while (stones != 0) {
                    if (man_flag != 0) {

                        for (int i = m - 1; i > 0; i--) {
                            if (stones != 0) {
                                game.player2[i] += 1;
                                stones -= 1;
                                if ((stones == 0) && (game.player2[i] == 1)) {
                                    pit_flag = i;
                                }
                            } else {
                                flag = 0;
                                break;
                            }
                        }

                        if ((flag == 0) || (stones == 0)) {
                            man_flag = 0;
                            break;
                        }

                        if (stones != 0) {
                            game.player2[0] += 1;
                            stones -= 1;
                        }
                        if (stones == 0) {
                            game.chance = 1;
                            if (game.Empty() == 1) {
                                break;
                            }
                            game.name = "A" + Integer.toString(k + 1);
                            String t = game.name + "," + depth + "," + "Infinity" + "," + game.Alpha + "," + game.Beta;
                            traverse.add(t);
                            BetaMin(game, depth - 1, max_depth, turn);
                            break;
                        }
                        for (int i = 0; i < game.number_pits; i++) {
                            if (stones != 0) {
                                game.player1[i] += 1;
                                stones -= 1;
                            } else {
                                flag = 0;
                                break;
                            }
                        }
                        if (stones == 0 || flag == 0) {
                            man_flag = 0;
                            break;
                        }
                        for (int i = game.number_pits; i >= m; i--) {
                            if (stones != 0) {
                                game.player2[i] += 1;
                                stones -= 1;
                                if ((stones == 0) && (game.player2[i] == 1)) {
                                    pit_flag = i;
                                }
                            } else {
                                flag = 0;
                                break;
                            }
                        }
                        if ((flag == 0) || (stones == 0)) {
                            man_flag = 0;
                            break;
                        }
                    }
                    m = k;
                }
                if (pit_flag != 0) {
                    game.player2[pit_flag] = 0;
                    game.player2[0] += game.player1[pit_flag - 1] + 1;
                    game.player1[pit_flag - 1] = 0;
                }
                if (game.Empty() == 1) {
                    if (depth == max_depth && game.chance == 1) {
                        game.name = "A" + Integer.toString(k + 1);
                        String t = game.name + "," + depth + "," + "Infinity" + "," + game.Alpha + "," + game.Beta;
                        traverse.add(t);
                    }
                    if (depth < max_depth && game.chance == 1) {
                        game.name = "A" + Integer.toString(k + 1);
                        String t = game.name + "," + depth + "," + "Infinity" + "," + game.Alpha + "," + game.Beta;
                        traverse.add(t);
                    }
                    if (depth < max_depth && game.chance == 0) {
                        game.name = "A" + Integer.toString(k + 1);
                        String t = game.name + "," + depth + "," + "-Infinity" + "," + game.Alpha + "," + game.Beta;
                        traverse.add(t);
                    }
                    game.chance = 1;
                    game.name = "A" + Integer.toString(k + 1);
                    game.value = Integer.toString(game.Eval(turn));
                    String t = game.name + "," + depth + "," + game.Eval(turn) + "," + game.Alpha + "," + game.Beta;
                    traverse.add(t);
                }
                if (game.chance == 0) {
                    if ((depth < max_depth)) {
                        game.name = "A" + Integer.toString(k + 1);
                        if (game.Beta != "Infinity" && game.Beta != "-Infinity" && game.value != "-Infinity" && game.value != "Infinity") {
                            game.Beta = Integer.toString(Math.min(Integer.parseInt(game.Beta), Integer.parseInt(game.value)));
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity"))
                                game.Beta = game.value;
                        }
                        String t = game.name + "," + depth + "," + game.value + "," + game.Alpha + "," + game.Beta;
                        traverse.add(t);
                    } else {
                        game.name = "A" + Integer.toString(k + 1);
                        String t = game.name + "," + depth + "," + game.Eval(turn) + "," + game.Alpha + "," + game.Beta;
                        traverse.add(t);
                        game.value = Integer.toString(game.Eval(turn));

                    }
                }

                if ((b.Alpha != "-Infinity") && (b.Alpha != "Infinity") && (game.value != "-Infinity") && (game.value != "Infinity")) {
                    if (Integer.parseInt(b.Alpha) >= Integer.parseInt(game.value)) {
                        prune = 1;
                        if (b.name.contains("B"))
                            depth = depth - 1;
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            } else {
                                b.value = Integer.toString(Math.min(game.Eval(turn), Integer.parseInt(b.value)));
                            }
                            traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = game.value;
                            } else {
                                b.value = Integer.toString(game.Eval(turn));
                            }
                            traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                        }
                        return next_state;
                    } else {
                        prune = 0;
                    }
                }

                if (b.Beta != "-Infinity" && b.Beta != "Infinity") {
                    if (game.value != "Infinity" && game.value != "-Infinity") {
                        b.Beta = Integer.toString(Math.min(Integer.parseInt(b.Beta), Integer.parseInt(game.value)));
                    }
                } else {
                    if (game.value != "-Infinity" && game.value != "Infinity")
                        b.Beta = game.value;
                }

                if (game.Eval(turn) < min_eval && game.chance == 0) {
                    min_eval = game.Eval(turn);
                    next_state = new Board(game);
                }
                tmpBeta = b.Beta;
                int val = 0;
                if ((depth < max_depth) && (game.chance == 0)) {
                    Board sub = new Board(AlphaMax(game, depth, max_depth, turn));
                    tBeta = b.Beta;
                    val = sub.Eval(turn);
                    if ((b.Beta != "-Infinity") && (b.Beta != "Infinity") && (sub.Alpha != "-Infinity") && (sub.Alpha != "Infinity")) {
                        b.Beta = Integer.toString(Math.min(Integer.parseInt(b.Beta), Integer.parseInt(sub.Alpha)));
                    } else {
                        b.Beta = sub.Alpha;
                    }
                }

                if (prune == 1)
                    b.Beta = tmpBeta;

                next_state.Alpha = b.Alpha;
                next_state.Beta = b.Beta;

                if (depth == max_depth && b.chance == 0) {
                    if (b.name.contains("B") && b.value != "Infinity" && b.value != "-Infinity") {
                        if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                            b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                        }
                        else
                        {
                            b.value = Integer.toString(Math.min(game.Eval(turn), Integer.parseInt(b.value)));
                        }
                    } else {
                        if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                            b.value = game.value;
                        }
                        else{
                            b.value = Integer.toString(game.Eval(turn));
                        }
                    }
                    if ((b.Alpha != "-Infinity") && (b.Alpha != "Infinity") && (game.value != "-Infinity") && (game.value != "Infinity")) {
                        if (Integer.parseInt(b.Alpha) >= Integer.parseInt(game.value)) {
                            traverse.add(b.name + "," + Integer.toString(depth-1) + "," + b.value + "," + b.Alpha + "," + tmpBeta);
                            return next_state;
                        }
                    }
                    traverse.add(b.name + "," + Integer.toString(depth - 1) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                } else if (b.name.contains("A")) {
                    if (b.chance == 0) {
                        if (b.value != "Infinity" && b.value != "-Infinity") {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            }
                            else
                            {
                                b.value = Integer.toString(Math.min(game.Eval(turn),Integer.parseInt(b.value)));
                            }
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = game.value;
                            }
                            else{
                                b.value = Integer.toString(game.Eval(turn));
                            }
                        }
                        if (game.chance == 1) {
                            if ((game.value != "-Infinity") && (game.value != "Infinity"))
                                b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                        }
                        if ((b.Alpha != "-Infinity") && (b.Alpha != "Infinity") && (game.value != "-Infinity") && (game.value != "Infinity")) {
                            if (Integer.parseInt(b.Alpha) >= Integer.parseInt(game.value)) {
                                traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + tBeta);
                                return next_state;
                            }
                        }
                        traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                    } else {
                        if (b.value != "Infinity" && b.value != "-Infinity") {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            }
                            else
                            {
                                b.value = Integer.toString(Math.min(game.Eval(turn),Integer.parseInt(b.value)));
                            }
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = game.value;
                            }
                            else{
                                b.value = Integer.toString(game.Eval(turn));
                            }
                        }
                    }
                    if ((b.Alpha != "-Infinity") && (b.Alpha != "Infinity") && (game.value != "-Infinity") && (game.value != "Infinity")) {
                        if (Integer.parseInt(b.Alpha) >= Integer.parseInt(game.value)) {
                            traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + tBeta);
                            return next_state;
                        }
                    }
                    traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                } else {
                    if (b.name.contains("B")) {
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            if ((game.value != "-Infinity") && (game.value != "Infinity"))
                                b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            else
                                b.value = Integer.toString(Math.min(game.Eval(turn), Integer.parseInt(b.value)));
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity"))
                                b.value = game.value;
                            else
                                b.value = Integer.toString(game.Eval(turn));
                        }
                        if ((b.Alpha != "-Infinity") && (b.Alpha != "Infinity") && (game.value != "-Infinity") && (game.value != "Infinity")) {
                            if (Integer.parseInt(b.Alpha) >= Integer.parseInt(game.value)) {
                                traverse.add(b.name + "," + Integer.toString(depth-1) + "," + b.value + "," + b.Alpha + "," + tBeta);
                                return next_state;
                            }
                        }
                        traverse.add(b.name + "," + Integer.toString(depth - 1) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                    } else {
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            b.value = Integer.toString(Math.min(val, Integer.parseInt(b.value)));
                        } else {
                            b.value = Integer.toString(val);
                        }
                        if ((b.Alpha != "-Infinity") && (b.Alpha != "Infinity") && (game.value != "-Infinity") && (game.value != "Infinity")) {
                            if (Integer.parseInt(b.Alpha) >= Integer.parseInt(game.value)) {
                                traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + tBeta);
                                return next_state;
                            }
                        }
                        traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                    }
                }
                prune = 0;
                game = new Board(b);
                if(game.Empty() == 1)
                    break;
            }

        }
        return next_state;
    }

    public static Board AlphaMax2(Board b, int depth, int max_depth, int turn) {
        depth = depth + 1;
        int max_eval = Integer.MIN_VALUE;
        Board next_state = new Board(b);
        for (int k = 1; k <= b.number_pits; k++) {
            Board game = new Board(b);
            game.value = "Infinity";
            if (game.player2[k] != 0) {
                int flag = 1;
                int stones = 0;
                int zero = 0;
                stones = game.player2[k];
                game.player2[k] = 0;
                int man_flag = 1;
                int pit_flag = 0;
                game.chance = 0;
                int m = k;
                while (stones != 0) {
                    if (man_flag != 0) {

                        for (int i = m - 1; i > 0; i--) {
                            if (stones != 0) {
                                game.player2[i] += 1;
                                stones -= 1;
                                if ((stones == 0) && (game.player2[i] == 1)) {
                                    pit_flag = i;
                                }
                            } else {
                                flag = 0;
                                break;
                            }
                        }

                        if ((flag == 0) || (stones == 0)) {
                            man_flag = 0;
                            break;
                        }

                        if (stones != 0) {
                            game.player2[0] += 1;
                            stones -= 1;
                        }
                        if (stones == 0) {
                            game.chance = 1;
                            if (game.Empty() == 1) {
                                break;
                            }
                            game.name = "A" + Integer.toString(k + 1);
                            String t = game.name + "," + depth + "," + "-Infinity" + "," + game.Alpha + "," + game.Beta;
                            traverse.add(t);
                            AlphaMax2(game, depth - 1, max_depth, turn);
                            break;
                        }
                        for (int i = 0; i < game.number_pits; i++) {
                            if (stones != 0) {
                                game.player1[i] += 1;
                                stones -= 1;
                            } else {
                                flag = 0;
                                break;
                            }
                        }
                        if (stones == 0 || flag == 0) {
                            man_flag = 0;
                            break;
                        }
                        for (int i = game.number_pits; i >= m; i--) {
                            if (stones != 0) {
                                game.player2[i] += 1;
                                stones -= 1;
                                if ((stones == 0) && (game.player2[i] == 1)) {
                                    pit_flag = i;
                                }
                            } else {
                                flag = 0;
                                break;
                            }
                        }
                        if ((flag == 0) || (stones == 0)) {
                            man_flag = 0;
                            break;
                        }
                    }
                    m = k;
                }
                if (pit_flag != 0) {
                    game.player2[pit_flag] = 0;
                    game.player2[0] += game.player1[pit_flag - 1] + 1;
                    game.player1[pit_flag - 1] = 0;
                }
                if (game.Empty() == 1) {
                    if (depth == max_depth && game.chance == 1) {
                        game.name = "A" + Integer.toString(k + 1);
                        String t = game.name + "," + depth + "," + "-Infinity" + "," + game.Alpha + "," + game.Beta;
                        traverse.add(t);
                    }
                    if (depth < max_depth && game.chance == 1) {
                        game.name = "A" + Integer.toString(k + 1);
                        String t = game.name + "," + depth + "," + "-Infinity" + "," + game.Alpha + "," + game.Beta;
                        traverse.add(t);
                    }
                    if (depth < max_depth && game.chance == 0) {
                        game.name = "A" + Integer.toString(k + 1);
                        String t = game.name + "," + depth + "," + "Infinity" + "," + game.Alpha + "," + game.Beta;
                        traverse.add(t);
                    }
                    game.chance = 1;
                    game.name = "A" + Integer.toString(k + 1);
                    if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                        game.value = Integer.toString(Math.max(Integer.parseInt(game.value), game.Eval(turn)));
                    } else {
                        game.value = Integer.toString(game.Eval(turn));
                    }
                    String t = game.name + "," + depth + "," + game.Eval(turn) + "," + game.Alpha + "," + game.Beta;
                    traverse.add(t);
                }

                if (game.Eval(turn) > max_eval && game.chance == 0) {
                    max_eval = game.Eval(turn);
                    next_state = new Board(game);
                }
                if (game.chance == 0) {
                    if (depth < max_depth) {
                        game.name = "A" + Integer.toString(k + 1);
                        if (game.Alpha != "-Infinity" && game.Alpha != "Infinity" && game.value != "Infinity" && game.value != "-Infinity") {
                            game.Alpha = Integer.toString(Math.max(Integer.parseInt(game.Alpha), Integer.parseInt(game.value)));
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity"))
                                game.Alpha = game.value;
                        }
                        String t = game.name + "," + depth + "," + game.value + "," + game.Alpha + "," + game.Beta;
                        traverse.add(t);
                    } else {
                        game.name = "A" + Integer.toString(k + 1);
                        String t = game.name + "," + depth + "," + game.Eval(turn) + "," + game.Alpha + "," + game.Beta;
                        traverse.add(t);
                        game.value = Integer.toString(game.Eval(turn));
                    }
                }
                if (minmax_state.player1 == null) {
                    minmax_state = new Board(game);
                }

                if ((game.value != "-Infinity") && (game.value != "Infinity") && (b.Beta != "-Infinity") && (b.Beta != "Infinity")) {
                    if (Integer.parseInt(b.Beta) <= Integer.parseInt(game.value)) {
                        prune = 1;
                        if (b.name.contains("B"))
                            depth = depth - 1;
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            } else {
                                b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                            }
                            traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = game.value;
                            } else {
                                b.value = Integer.toString(game.Eval(turn));
                            }
                            traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                        }
                        return next_state;
                    } else {
                        prune = 0;
                    }
                }


                if (b.Alpha != "-Infinity" && b.Alpha != "Infinity" && game.value != "Infinity" && game.value != "-Infinity")
                    b.Alpha = Integer.toString(Math.max(Integer.parseInt(b.Alpha), Integer.parseInt(game.value)));
                else {
                    if (game.value != "-Infinity" && game.value != "Infinity")
                        b.Alpha = game.value; //otherwise infinity
                }


                int val = 0;
                tmpAlpha = b.Alpha;
                if ((depth < max_depth) && (game.chance == 0)) {
                    Board sub = new Board(BetaMin2(game, depth, max_depth, turn));
                    val = sub.Eval(turn);
                    tAlpha = b.Alpha;
                    if ((b.Alpha != "-Infinity") && (b.Alpha != "Infinity") && (sub.Beta != "-Infinity") && (sub.Beta != "Infinity")) {
                        b.Alpha = Integer.toString(Math.max(Integer.parseInt(b.Alpha), Integer.parseInt(sub.Beta)));
                    } else {
                        b.Alpha = sub.Beta;
                    }
                }

                if (prune == 1) {
                    b.Alpha = tmpAlpha;
                }

                next_state.Alpha = b.Alpha;
                next_state.Beta = b.Beta;

                if (b.Eq(root) == 1 && b.value != "Infinity" && b.value != "-Infinity" && game.value != "Infinity" && game.value != "-Infinity") {
                    b.name = "root";
                    if ((game.value != "-Infinity") && (game.value != "Infinity"))
                        b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                    else
                        b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                    if (b.Alpha != "-Infinity" && b.Alpha != "Infinity" && game.value != "Infinity" && game.value != "-Infinity")
                        b.Alpha = Integer.toString(Math.max(Integer.parseInt(b.Alpha), Integer.parseInt(game.value)));
                    else
                        b.Alpha = game.value;
                    String tt = b.name + "," + 0 + "," + b.value + "," + b.Alpha + "," + b.Beta;
                    traverse.add(tt);
                    if (Integer.parseInt(b.value) > minmax_value) {
                        minmax_value = Integer.parseInt(b.value);
                        minmax_state = new Board(game);
                    }
                    game = new Board(b);
                    continue;
                }

                if (depth == max_depth && b.chance == 0) {
                    if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                        if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                            b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                        } else {
                            b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                        }
                    } else {
                        if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                            b.value = game.value;
                        } else {
                            b.value = Integer.toString(game.Eval(turn));
                        }
                    }
                    if (b.Eq(root) == 1) {
                        b.name = "root";
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            } else {
                                b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                            }
                        }
                        traverse.add(b.name + "," + 0 + "," + b.value + "," + b.Alpha + "," + b.Beta);
                    } else {
                        traverse.add(b.name + "," + Integer.toString(depth - 1) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                    }
                } else {
                    if (b.Eq(root) == 1) {
                        b.name = "root";
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            if((game.value != "-Infinity") && (game.value != "Infinity"))
                                b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            else
                                b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                            traverse.add("root" + "," + 0 + "," + b.value + "," + b.Alpha + "," + b.Beta);
                            if (Integer.parseInt(b.value) > minmax_value && game.chance == 0) {
                                minmax_value = Integer.parseInt(b.value);
                                minmax_state = new Board(game);
                            }
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = game.value;
                            } else {
                                b.value = Integer.toString(game.Eval(turn));
                            }
                            traverse.add("root" + "," + 0 + "," + Integer.parseInt(b.value) + "," + b.Alpha + "," + b.Beta);
                            if (Integer.parseInt(b.value) > minmax_value) {
                                minmax_value = Integer.parseInt(b.value);
                                minmax_state = new Board(game);
                            }
                        }
                    } else {
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = Integer.toString(Math.max(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            } else {
                                b.value = Integer.toString(Math.max(game.Eval(turn), Integer.parseInt(b.value)));
                            }
                            if(b.name.contains("B")) {
                                if ((b.Beta != "-Infinity") && (b.Beta != "Infinity") && (game.value != "-Infinity") && (game.value != "Infinity")) {
                                    if (Integer.parseInt(b.Beta) <= Integer.parseInt(game.value)) {
                                        traverse.add(b.name + "," + Integer.toString(depth-1) + "," + b.value + "," + tAlpha + "," + b.Beta);
                                        return next_state;
                                    }
                                }
                                traverse.add(b.name + "," + Integer.toString(depth - 1) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                            }
                            else {
                                if ((b.Beta != "-Infinity") && (b.Beta != "Infinity") && (game.value != "-Infinity") && (game.value != "Infinity")) {
                                    if (Integer.parseInt(b.Beta) <= Integer.parseInt(game.value)) {
                                        traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + tAlpha + "," + b.Beta);
                                        return next_state;
                                    }
                                }
                                traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                            }
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = game.value;
                            } else {
                                b.value = Integer.toString(game.Eval(turn));
                            }
                            if(b.name.contains("B")) {
                                if ((b.Beta != "-Infinity") && (b.Beta != "Infinity") && (game.value != "-Infinity") && (game.value != "Infinity")) {
                                    if (Integer.parseInt(b.Beta) <= Integer.parseInt(game.value)) {
                                        traverse.add(b.name + "," + Integer.toString(depth-1) + "," + b.value + "," + tAlpha + "," + b.Beta);
                                        return next_state;
                                    }
                                }
                                traverse.add(b.name + "," + Integer.toString(depth - 1) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                            }
                            else {
                                if ((b.Beta != "-Infinity") && (b.Beta != "Infinity") && (game.value != "-Infinity") && (game.value != "Infinity")) {
                                    if (Integer.parseInt(b.Beta) <= Integer.parseInt(game.value)) {
                                        traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + tAlpha + "," + b.Beta);
                                        return next_state;
                                    }
                                }
                                traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                            }
                        }
                        if (b.chance == 1 && depth == 1) {
                            if (Integer.parseInt(b.value) > minmax_value) {
                                minmax_value = Integer.parseInt(b.value);
                                minmax_state = new Board(game);
                                s_flag = 1;
                            }
                        }
                    }
                }
                prune = 0;
                game = new Board(b);
                if(game.Empty() == 1)
                    break;
            }

        }
        return next_state;

    }

    public static Board BetaMin2(Board b, int depth, int max_depth, int turn) {
        depth = depth + 1;
        int min_eval = Integer.MAX_VALUE;
        Board next_state = new Board(b);
        for (int k = 0; k < b.number_pits; k++) {
            Board game = new Board(b);
            game.value = "-Infinity";
            if (game.player1[k] != 0) {
                int flag = 1;
                int stones = 0;
                int zero = 0;
                stones = game.player1[k];
                game.player1[k] = 0;
                int man_flag = 1;
                int pit_flag = -1;
                game.chance = 0;
                int m = k;
                while (stones != 0) {
                    if (man_flag != 0) {
                        for (int i = 1; i <= game.number_pits - m - 1; i++) {
                            if (stones != 0) {
                                game.player1[i + m] += 1;
                                stones -= 1;
                                if ((stones == 0) && (game.player1[m + i] == 1)) {
                                    pit_flag = m + i;
                                }
                            } else {
                                flag = 0;
                                break;
                            }
                        }
                        if (stones == 0 || flag == 0) {
                            man_flag = 0;
                            break;
                        }
                        if (stones != 0) {
                            game.player1[game.number_pits] += 1;
                            stones -= 1;
                        }
                        if (stones == 0) {
                            game.chance = 1;
                            if (game.Empty() == 1) {
                                break;
                            }
                            game.name = "B" + Integer.toString(k + 2);
                            String t = game.name + "," + depth + "," + "Infinity" + "," + game.Alpha + "," + game.Beta;
                            traverse.add(t);
                            BetaMin2(game, depth - 1, max_depth, turn);
                            break;
                        }
                        for (int i = game.number_pits; i > 0; i--) {
                            if (stones != 0) {
                                game.player2[i] += 1;
                                stones -= 1;
                            } else {
                                flag = 0;
                                break;
                            }
                        }
                        if ((flag == 0) || (stones == 0)) {
                            man_flag = 0;
                            break;
                        }
                    }
                    if (stones != 0) {
                        m = 0;
                        stones -= 1;
                        game.player1[0] += 1;
                        if (stones == 0) {
                            if(game.player1[0]==1) {
                                pit_flag = 0;
                            }
                            flag = 0;
                            break;
                        }
                    }
                    if (flag == 0)
                        break;

                }
                if (pit_flag != -1) {
                    game.player1[pit_flag] = 0;
                    game.player1[game.number_pits] += game.player2[pit_flag + 1] + 1;
                    game.player2[pit_flag + 1] = 0;
                }
                if (game.Empty() == 1) {
                    if (depth == max_depth && game.chance == 1) {
                        game.name = "B" + Integer.toString(k + 2);
                        String t = game.name + "," + depth + "," + "Infinity" + "," + game.Alpha + "," + game.Beta;
                        traverse.add(t);
                    }
                    if (depth < max_depth && game.chance == 1) {
                        game.name = "B" + Integer.toString(k + 2);
                        String t = game.name + "," + depth + "," + "Infinity" + "," + game.Alpha + "," + game.Beta;
                        traverse.add(t);
                    }
                    if (depth < max_depth && game.chance == 0) {
                        game.name = "B" + Integer.toString(k + 2);
                        String t = game.name + "," + depth + "," + "-Infinity" + "," + game.Alpha + "," + game.Beta;
                        traverse.add(t);
                    }
                    game.chance = 1;
                    game.name = "B" + Integer.toString(k + 2);
                    game.value = Integer.toString(game.Eval(turn));
                    String t = game.name + "," + depth + "," + game.Eval(turn);
                    traverse.add(t);
                }
                if (game.chance == 0) {
                    if ((depth < max_depth)) {
                        game.name = "B" + Integer.toString(k + 2);
                        if (game.Beta != "Infinity" && game.Beta != "-Infinity" && game.value != "-Infinity" && game.value != "Infinity") {
                            game.Beta = Integer.toString(Math.min(Integer.parseInt(game.Beta), Integer.parseInt(game.value)));
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity"))
                                game.Beta = game.value;
                        }
                        String t = game.name + "," + depth + "," + game.value + "," + game.Alpha + "," + game.Beta;
                        traverse.add(t);
                    } else {
                        game.name = "B" + Integer.toString(k + 2);
                        String t = game.name + "," + depth + "," + game.Eval(turn) + "," + game.Alpha + "," + game.Beta;
                        traverse.add(t);
                        game.value = Integer.toString(game.Eval(turn));

                    }
                }
                if ((b.Alpha != "-Infinity") && (b.Alpha != "Infinity") && (game.value != "-Infinity") && (game.value != "Infinity")) {
                    if (Integer.parseInt(b.Alpha) >= Integer.parseInt(game.value)) {
                        prune = 1;
                        if (b.name.contains("A"))
                            depth = depth - 1;
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            } else {
                                b.value = Integer.toString(Math.min(game.Eval(turn), Integer.parseInt(b.value)));
                            }
                            traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = game.value;
                            } else {
                                b.value = Integer.toString(game.Eval(turn));
                            }
                            traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                        }
                        return next_state;
                    } else {
                        prune = 0;
                    }
                }

                if (b.Beta != "-Infinity" && b.Beta != "Infinity") {
                    if (game.value != "Infinity" && game.value != "-Infinity") {
                        b.Beta = Integer.toString(Math.min(Integer.parseInt(b.Beta), Integer.parseInt(game.value)));
                    }
                } else {
                    if (game.value != "-Infinity" && game.value != "Infinity")
                        b.Beta = game.value;
                }


                if (game.Eval(turn) < min_eval && game.chance == 0) {
                    min_eval = game.Eval(turn);
                    next_state = new Board(game);
                }

                tmpBeta = b.Beta;
                int val = 0;
                if ((depth < max_depth) && (game.chance == 0)) {
                    Board sub = new Board(AlphaMax2(game, depth, max_depth, turn));
                    tBeta = b.Beta;
                    val = sub.Eval(turn);
                    if ((b.Beta != "-Infinity") && (b.Beta != "Infinity") && (sub.Alpha != "-Infinity") && (sub.Alpha != "Infinity")) {
                        b.Beta = Integer.toString(Math.min(Integer.parseInt(b.Beta), Integer.parseInt(sub.Alpha)));
                    } else {
                        b.Beta = sub.Alpha;
                    }
                }

                if (prune == 1)
                    b.Beta = tmpBeta;

                next_state.Alpha = b.Alpha;
                next_state.Beta = b.Beta;

                if (depth == max_depth && b.chance == 0) {
                    if (b.name.contains("A") && b.value != "Infinity" && b.value != "-Infinity") {
                        if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                            b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                        }
                        else
                        {
                            b.value = Integer.toString(Math.min(game.Eval(turn),Integer.parseInt(b.value)));
                        }
                    } else {
                        if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                            b.value = game.value;
                        }
                        else{
                            b.value = Integer.toString(game.Eval(turn));
                        }
                    }
                    if ((b.Alpha != "-Infinity") && (b.Alpha != "Infinity") && (game.value != "-Infinity") && (game.value != "Infinity")) {
                        if (Integer.parseInt(b.Alpha) >= Integer.parseInt(game.value)) {
                            traverse.add(b.name + "," + Integer.toString(depth-1) + "," + b.value + "," + b.Alpha + "," + tBeta);
                            return next_state;
                        }
                    }
                    traverse.add(b.name + "," + Integer.toString(depth - 1) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                } else if (b.name.contains("B")) {
                    if (b.chance == 0) {
                        if (b.value != "Infinity" && b.value != "-Infinity") {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            }
                            else
                            {
                                b.value = Integer.toString(Math.min(game.Eval(turn),Integer.parseInt(b.value)));
                            }
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = game.value;
                            }
                            else{
                                b.value = Integer.toString(game.Eval(turn));
                            }
                        }
                        if (game.chance == 1) {
                            if ((game.value != "-Infinity") && (game.value != "Infinity"))
                                b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                        }
                        if ((b.Alpha != "-Infinity") && (b.Alpha != "Infinity") && (game.value != "-Infinity") && (game.value != "Infinity")) {
                            if (Integer.parseInt(b.Alpha) >= Integer.parseInt(game.value)) {
                                traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + tBeta);
                                return next_state;
                            }
                        }
                        traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                    } else {
                        if (b.value != "Infinity" && b.value != "-Infinity") {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            }
                            else
                            {
                                b.value = Integer.toString(Math.min(game.Eval(turn),Integer.parseInt(b.value)));
                            }
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity")) {
                                b.value = game.value;
                            }
                            else{
                                b.value = Integer.toString(game.Eval(turn));
                            }
                        }
                    }
                    if ((b.Alpha != "-Infinity") && (b.Alpha != "Infinity") && (game.value != "-Infinity") && (game.value != "Infinity")) {
                        if (Integer.parseInt(b.Alpha) >= Integer.parseInt(game.value)) {
                            traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + tBeta);
                            return next_state;
                        }
                    }
                    traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                } else {
                    if (b.name.contains("A")) {
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            if ((game.value != "-Infinity") && (game.value != "Infinity"))
                                b.value = Integer.toString(Math.min(Integer.parseInt(game.value), Integer.parseInt(b.value)));
                            else
                                b.value = Integer.toString(Math.min(game.Eval(turn), Integer.parseInt(b.value)));
                        } else {
                            if ((game.value != "-Infinity") && (game.value != "Infinity"))
                                b.value = game.value;
                            else
                                b.value = Integer.toString(game.Eval(turn));
                        }
                        if ((b.Alpha != "-Infinity") && (b.Alpha != "Infinity") && (game.value != "-Infinity") && (game.value != "Infinity")) {
                            if (Integer.parseInt(b.Alpha) >= Integer.parseInt(game.value)) {
                                traverse.add(b.name + "," + Integer.toString(depth-1) + "," + b.value + "," + b.Alpha + "," + tBeta);
                                return next_state;
                            }
                        }
                        traverse.add(b.name + "," + Integer.toString(depth - 1) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                    } else {
                        if ((b.value != "-Infinity") && (b.value != "Infinity")) {
                            b.value = Integer.toString(Math.min(val, Integer.parseInt(b.value)));
                        } else {
                            b.value = Integer.toString(val);
                        }
                        if ((b.Alpha != "-Infinity") && (b.Alpha != "Infinity") && (game.value != "-Infinity") && (game.value != "Infinity")) {
                            if (Integer.parseInt(b.Alpha) >= Integer.parseInt(game.value)) {
                                traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + tBeta);
                                return next_state;
                            }
                        }
                        traverse.add(b.name + "," + Integer.toString(depth) + "," + b.value + "," + b.Alpha + "," + b.Beta);
                    }
                }
                prune = 0;
                game = new Board(b);
                if(game.Empty() == 1)
                    break;
            }

        }
        return next_state;
    }
}
