import java.util.*;

public class TicTacToeAlphaBeta {

    static final char EMPTY = ' ';
    static final char X = 'X'; // KI
    static final char O = 'O'; // Mensch

    public static void main(String[] args) {
        char[][] state = new char[3][3];
        for (char[] r : state) Arrays.fill(r, EMPTY);

        Scanner sc = new Scanner(System.in);
        char current = X; // X beginnt

        System.out.println("TicTacToe – Minimax mit Alpha-Beta");
        while (true) {
            print(state);

            if (TerminalTest(state)) {
                int u = Utility(state);
                if (u == 1) System.out.println(">> X (KI) gewinnt.");
                else if (u == -1) System.out.println(">> O (Du) gewinnst.");
                else System.out.println(">> Unentschieden.");
                break;
            }

            if (current == X) {
                // Beste Aktion mit Alpha-Beta-Pruning bestimmen
                int[] action = MinimaxAlphaBeta(state);
                state[action[0]][action[1]] = X;
                System.out.println("KI spielt X auf (" + (action[0] + 1) + "," + (action[1] + 1) + ")");
            } else {
                System.out.println("Du bist O. Eingabe: Zeile Spalte (1..3 1..3):");
                while (true) {
                    String line = sc.nextLine().trim();
                    try {
                        String[] p = line.split("\\s+");
                        int r = Integer.parseInt(p[0]) - 1;
                        int c = Integer.parseInt(p[1]) - 1;
                        if (r < 0 || r > 2 || c < 0 || c > 2 || state[r][c] != EMPTY) {
                            System.out.println("Ungültig. Bitte erneut:");
                            continue;
                        }
                        state[r][c] = O;
                        break;
                    } catch (Exception e) {
                        System.out.println("Bitte zwei Zahlen 1..3 eingeben:");
                    }
                }
            }
            current = (current == X) ? O : X;
        }
    }



    /* ==================== Minimax mit Alpha-Beta ==================== */

    // Wählt beste Aktion für X. Nutzt alpha/beta zum Abschneiden.
    static int[] MinimaxAlphaBeta(char[][] state) {
        int bestVal = Integer.MIN_VALUE; // -∞
        int[] bestAction = null;
        int alpha = Integer.MIN_VALUE;   // beste garantierte MAX-Untergrenze
        int beta  = Integer.MAX_VALUE;   // beste garantierte MIN-Obergrenze

        // Alle legalen Züge probieren
        for (int[] a : Successors(state)) {
            char[][] s = Result(state, a, X);
            int v = MinValueAB(s, alpha, beta); // Gegner ist dran (min)
            if (v > bestVal) {
                bestVal = v;
                bestAction = a;
            }
            // Alpha aktualisieren (MAX verbessert sich)
            alpha = Math.max(alpha, bestVal);
            // Optional: kleiner Ordnungs-Trick (frühe gute Züge zuerst),
            // aber hier reicht die einfache Reihenfolge.
        }
        return bestAction;
    }

    // MAX-Knoten mit Alpha-Beta
    static int MaxValueAB(char[][] state, int alpha, int beta) {
        if (TerminalTest(state)) return Utility(state);
        int v = Integer.MIN_VALUE; // -∞
        for (int[] a : Successors(state)) {
            char[][] s = Result(state, a, X);
            v = Math.max(v, MinValueAB(s, alpha, beta)); // Kind auswerten
            if (v >= beta) return v;     // Beta-Cut: MIN wird das nicht zulassen
            alpha = Math.max(alpha, v);  // Alpha verbessern
        }
        return v;
    }

    // MIN-Knoten mit Alpha-Beta
    static int MinValueAB(char[][] state, int alpha, int beta) {
        if (TerminalTest(state)) return Utility(state);
        int v = Integer.MAX_VALUE; // +∞
        for (int[] a : Successors(state)) {
            char[][] s = Result(state, a, O);
            v = Math.min(v, MaxValueAB(s, alpha, beta)); // Kind auswerten
            if (v <= alpha) return v;   // Alpha-Cut: MAX nimmt das nicht
            beta = Math.min(beta, v);   // Beta verbessern
        }
        return v;
    }

    /* ==================== Hilfsfunktionen (wie vorher) ==================== */

    static boolean TerminalTest(char[][] b) {
        return Winner(b) != EMPTY || Successors(b).isEmpty();
    }

    // +1: X gewinnt, -1: O gewinnt, 0: sonst
    static int Utility(char[][] b) {
        char w = Winner(b);
        if (w == X) return 1;
        if (w == O) return -1;
        return 0;
    }

    // Alle freien Felder als (r,c)
    static List<int[]> Successors(char[][] b) {
        List<int[]> list = new ArrayList<>(9);
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                if (b[r][c] == EMPTY) list.add(new int[]{r, c});
        return list;
    }

    // Kopie + Zug setzen
    static char[][] Result(char[][] state, int[] action, char player) {
        char[][] copy = new char[3][3];
        for (int i = 0; i < 3; i++) copy[i] = Arrays.copyOf(state[i], 3);
        copy[action[0]][action[1]] = player;
        return copy;
    }

    // Gewinner ermitteln oder EMPTY
    static char Winner(char[][] b) {
        for (int i = 0; i < 3; i++) {
            if (b[i][0] != EMPTY && b[i][0] == b[i][1] && b[i][1] == b[i][2]) return b[i][0];
            if (b[0][i] != EMPTY && b[0][i] == b[1][i] && b[1][i] == b[2][i]) return b[0][i];
        }
        if (b[1][1] != EMPTY) {
            if (b[0][0] == b[1][1] && b[1][1] == b[2][2]) return b[1][1];
            if (b[0][2] == b[1][1] && b[1][1] == b[2][0]) return b[1][1];
        }
        return EMPTY;
    }

    // Brett anzeigen
    static void print(char[][] b) {
        System.out.println();
        System.out.println("  1   2   3");
        for (int r = 0; r < 3; r++) {
            System.out.print((r + 1) + " ");
            for (int c = 0; c < 3; c++) {
                char ch = (b[r][c] == EMPTY) ? '.' : b[r][c];
                System.out.print(ch);
                if (c < 2) System.out.print(" | ");
            }
            System.out.println();
            if (r < 2) System.out.println("  ---------");
        }
        System.out.println();
    }
}
