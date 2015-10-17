import java.util.*;
public abstract class BoardGame{
    public static final int ILLEGAL = 0;
    public static final int ONGOING = 1;
    public static final int TIE = 2;
    public static final int X_WON = 3;
    public static final int O_WON = 4;
    public static final int NO_MOVE = 5;
    public static final int GAME_OVER = 6;

    private int size;
    private MoveStrategy strategy;
    protected int status;
    protected char[][] grid;
    protected char turn;
    
    public BoardGame(int size) {
        this(size, new FirstAvailableMove());
    }
    
    public BoardGame(int size, MoveStrategy strategy) {
        status = ONGOING;
        this.size = size;
        this.strategy = strategy;
        grid = new char[size][size];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                grid[i][j] = '_';
            }
        }
        turn = 'x';
    }
    
    public BoardGame(BoardGame b) {
        status = b.status;
        size = b.size;
        strategy = b.strategy;
        grid = new char[size][size];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                grid[i][j] = b.grid[i][j];
            }
        }
        turn = b.turn;
    }
    
    public void setMoveStrategy(MoveStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void toggleTurn() {
        if (turn == 'x') turn = 'o';
        else turn = 'x';
    }
    
    public char getTurn() {return turn;}
    
    protected List<Move> generateMoves() {
        List<Move> moves = new ArrayList<Move>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Move m = new Move(i,j);
                if (canPlay(m)) {
                    moves.add(m);
                }
            }
        }
        return moves;
    }
    
    protected void displayStatus(int s) {
        switch (s) {
            case X_WON: System.out.println("X won!"); print(); break;
            case O_WON: System.out.println("O won!"); print(); break;
            case TIE: System.out.println("It's a tie!"); print(); break;
            case ILLEGAL: System.out.println("Illegal move!"); break;
            case ONGOING: break;
            default: break;
        }
    }
    
    protected abstract boolean canPlay(Move m);
    
    public abstract int play(Move m);
    
    protected int machinePlay() {        
        Move move = strategy.selectMove(this);
        
        if (move == null) return NO_MOVE; //game loop should prevent this from happening
        else return play(move);
    }
        
    protected Move machineFirstAvailableMove() {
        List<Move> moves = generateMoves();
        if (moves.isEmpty()) return null;
        else return moves.get(0);
    }
    
    protected Move machineRandomMove() {
        List<Move> moves = generateMoves();
        if (moves.isEmpty()) return null;
        Random r = new Random();
        int moveIndex = r.nextInt(moves.size());
        return moves.get(moveIndex);
    }
    
    protected Move getMove(String input) {
        int row;
        int col;
        String[] smove = input.split("[, ]+");
        if (smove.length != 2)  return null;
        try {
            row = Integer.parseInt(smove[0]);
            col = Integer.parseInt(smove[1]);
        }
        catch (Exception e) {return null;}
        return new Move(row,col);
    }
    
    public String toString() {
        String s = "";
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                s += grid[i][j];
            }
            s += "\n";
        }
        return s;
    }
    
    protected abstract void determineWinner();
    
    protected abstract int getMinScore();
    
    public abstract int evaluateMove(Move m);
    
    protected Move machineGreedyMove() {
        List<Move> moves = generateMoves();
        if (moves.isEmpty()) return null;
        
        int currentScore = getMinScore();
        Move candidateMove = null; 
  
        for (Move m : moves) {
            int score = evaluateMove(m);
            if (score >= currentScore) {
                candidateMove = m;
                currentScore = score;
            }
        }
        return candidateMove;
    }
           
    public void print() {
        System.out.println(this);
    }
}