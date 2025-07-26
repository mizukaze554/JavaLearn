import java.util.*;

public class TicTacToe {
    static Scanner sc = new Scanner(System.in);
    static int playerX = 0, playerO = 0;
    static boolean xTurn = true;
    static String gameMode;
    static String aiDifficulty;
    static final int[] WIN_PATTERNS = {
        0b111000000, 0b000111000, 0b000000111, // Rows
        0b100100100, 0b010010010, 0b001001001, // Columns
        0b100010001, 0b001010100              // Diagonals
    };

    public static void main(String[] args) {
        while (true) {
            setupGame();
            playGame();
            System.out.println("Play again? (y/n)");
            if (!sc.next().equalsIgnoreCase("y")) break;
        }
        System.out.println("Thanks for playing!");
    }

    static void setupGame() {
        playerX = 0;
        playerO = 0;
        xTurn = true;

        System.out.println("Clear the console screen? (y/n)");
        if (sc.next().equalsIgnoreCase("y")) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }

        System.out.println("Select game mode:");
        System.out.println("1. Human vs Human");
        System.out.println("2. Human vs AI");
        int mode = sc.nextInt();
        gameMode = (mode == 1) ? "human" : "ai";

        if (gameMode.equals("ai")) {
            System.out.println("Choose AI difficulty:");
            System.out.println("1. Easy");
            System.out.println("2. Normal");
            System.out.println("3. Hard");
            int diff = sc.nextInt();
            aiDifficulty = switch (diff) {
                case 1 -> "easy";
                case 2 -> "normal";
                default -> "hard";
            };
        }

        printBoard();
    }

    static void playGame() {
        while (true) {
            int move;
            if (gameMode.equals("human") || xTurn) {
                System.out.println("Player " + (xTurn ? "X" : "O") + ", choose position (0-8):");
                move = sc.nextInt();
            } else {
                System.out.println("AI (" + aiDifficulty + ") is thinking...");
                move = switch (aiDifficulty) {
                    case "easy" -> getRandomMove();
                    case "normal" -> getNormalAIMove();
                    default -> getBestMove();
                };
                System.out.println("AI placed O at position " + move);
            }

            if (move < 0 || move > 8 || isOccupied(move)) {
                System.out.println("Invalid move. Try again.");
                continue;
            }

            placeMove(move);
            printBoard();

            int currentPlayerBoard = xTurn ? playerX : playerO;
            if (isWin(currentPlayerBoard)) {
                System.out.println("Player " + (xTurn ? "X" : "O") + " wins!");
                break;
            }

            if (isDraw()) {
                System.out.println("It's a draw!");
                break;
            }

            xTurn = !xTurn;
        }
    }

    static void placeMove(int pos) {
        if (xTurn)
            playerX |= (1 << pos);
        else
            playerO |= (1 << pos);
    }

    static boolean isOccupied(int pos) {
        return ((playerX | playerO) & (1 << pos)) != 0;
    }

    static boolean isWin(int board) {
        for (int pattern : WIN_PATTERNS)
            if ((board & pattern) == pattern)
                return true;
        return false;
    }

    static boolean isDraw() {
        return (playerX | playerO) == 0b111111111;
    }

    static void printBoard() {
        System.out.println("Current board:");
        for (int i = 0; i < 9; i++) {
            char c = '.';
            if ((playerX & (1 << i)) != 0) c = 'X';
            else if ((playerO & (1 << i)) != 0) c = 'O';
            else c = (char) ('0' + i);

            System.out.print(c);
            if (i % 3 != 2) System.out.print(" | ");
            else if (i != 8) System.out.println("\n--+---+--");
        }
        System.out.println();
    }

    static int getRandomMove() {
        List<Integer> free = getFreePositions();
        return free.get(new Random().nextInt(free.size()));
    }

    static int getNormalAIMove() {
        for (int pos : getFreePositions()) {
            int test = playerO | (1 << pos);
            if (isWin(test)) return pos;
        }
        for (int pos : getFreePositions()) {
            int test = playerX | (1 << pos);
            if (isWin(test)) return pos;
        }
        return getRandomMove();
    }

    static int getBestMove() {
        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        for (int pos : getFreePositions()) {
            playerO |= (1 << pos);
            int score = minimax(playerX, playerO, false);
            playerO &= ~(1 << pos);

            if (score > bestScore) {
                bestScore = score;
                bestMove = pos;
            }
        }

        return bestMove;
    }

    static int minimax(int xBoard, int oBoard, boolean maximizing) {
        if (isWin(oBoard)) return 1;
        if (isWin(xBoard)) return -1;
        if ((xBoard | oBoard) == 0b111111111) return 0;

        int bestScore = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (int pos = 0; pos < 9; pos++) {
            if (((xBoard | oBoard) & (1 << pos)) != 0) continue;

            if (maximizing) {
                oBoard |= (1 << pos);
                bestScore = Math.max(bestScore, minimax(xBoard, oBoard, false));
                oBoard &= ~(1 << pos);
            } else {
                xBoard |= (1 << pos);
                bestScore = Math.min(bestScore, minimax(xBoard, oBoard, true));
                xBoard &= ~(1 << pos);
            }
        }
        return bestScore;
    }

    static List<Integer> getFreePositions() {
        List<Integer> free = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (!isOccupied(i)) free.add(i);
        }
        return free;
    }
}
