/**
 * One-player version of Othello, against terrible AI
 * 
 * @author babak
 * @version 0.0
 */

import java.util.*;

public class Othello extends BoardGame {
    public static int SIZE = 8;

    public Othello(int size) {
        this(size, new FirstAvailableMove());
    }
    
    public Othello(int size, MoveStrategy strategy) {
        super(size, strategy);
        grid[size/2 - 1][size/2 - 1] = 'o';
        grid[size/2][size/2] = 'o';
        grid[size/2][size/2 - 1] = 'x';
        grid[size/2 - 1][size/2] = 'x';
    }
    
    public Othello() {
        this(SIZE);
    }
    
    public Othello(Othello o) {
        super(o);
    }
    
    private char oppositeTurn() {
        if (turn == 'x') return 'o'; else return 'x';
    }
    
    private boolean canOutflank(Move m, int rowdir, int coldir) {
        int row = m.getRow();
        int col = m.getCol();
        int rowBound, colBound;
        
        //set limits of the directions to outflank
        //if direction is 0 (i.e. irrelevant), should just not interfere with the logic
        switch (rowdir) {
            case -1: rowBound = 0; break;
            case 1: rowBound = grid.length - 1; break;
            case 0: rowBound = grid.length + 1; break; //arbitrarily large so as not to interfere with tests
            default: rowBound = 0; break; //shouldn't get here
        }
        
        switch (coldir) {
            case -1: colBound = 0; break;
            case 1: colBound = grid.length - 1; break;
            case 0: colBound = grid.length + 1; break; //arbitrarily large so as not to interfere with tests
            default: colBound = 0; break; //shouldn't get here
        }
        
        //can't outflank if too close to limit 
        if ((Math.abs(row - rowBound) <= 1) || (Math.abs(col - colBound) <= 1)) return false;
    
        int nextRow = row + rowdir;
        int nextCol = col + coldir;
        
        if (grid[nextRow][nextCol] == turn) return false; //immediate neighbour can't be of same colour
        
        //while within boundary and landing on opposite-coloured disc, keep going.
        while ( ((rowBound - nextRow) * rowdir >= 0) && ((colBound - nextCol) * coldir >= 0) && (grid[nextRow][nextCol] == oppositeTurn()) ) {
            nextRow += rowdir;
            nextCol += coldir;
        }
        
        //if went past the boundary, can't outflank
        if (((rowBound - nextRow) * rowdir < 0) || ((colBound - nextCol) * coldir < 0)) return false;
        
        if (grid[nextRow][nextCol] == turn) return true;
        return false; //covers case where there isn't any disc at the end of sequence of opposite discs
    }
        
        
    private void flip(Move m, int rowdir, int coldir) {
        int nextRow = m.getRow() + rowdir;
        int nextCol = m.getCol() + coldir;
        while (grid[nextRow][nextCol] == oppositeTurn()) {
            grid[nextRow][nextCol] = turn;
            nextRow = nextRow + rowdir;
            nextCol = nextCol + coldir;
        }
    }
    
    
    protected boolean canPlay(Move m) {
        int row = m.getRow(); 
        int col = m.getCol();
        if ((row < 0) || (row >= SIZE) || (col < 0) || (col >= SIZE)) return false;
        if (grid[row][col] != '_') return false;
        boolean canOutflank = false;
        int [] range = {-1, 0, 1};
        for (int r : range) {
            for (int c : range) {
                if ((r == 0) && (c == 0)) continue;
                canOutflank = canOutflank || canOutflank(m, r, c);
            }
        }
        
        return canOutflank;
    }
    
    public int play(Move m) {
        if (!canPlay(m)) return ILLEGAL; 
        
        int row = m.getRow();
        int col = m.getCol();
        grid[row][col] = turn;
        int [] range = {-1, 0, 1};
        for (int r : range) {
            for (int c : range) {
                if ((r == 0) && (c == 0)) continue;
                if (canOutflank(m, r, c)) flip(m, r, c);
            }
        }
        return ONGOING;
    }
                
    public void loop() {
        //int status = ONGOING;
        Scanner sc = new Scanner(System.in);
        Move move;
        boolean hasPassed = false;
        
        System.out.println("Enter 'x' if you want to start");
        if (!(sc.nextLine().equals("x"))) {
            System.out.println("OK, I'll start then");
            status = machinePlay();
            toggleTurn();
        }
        
        while (status == ONGOING) {
           print();
           if (!(generateMoves().isEmpty())) {
               do {
                   System.out.println("Enter your move: <row, col> or quit <q>");
                   move = getMove(sc.nextLine());
                   if (move == null) {
                       System.out.println("Bye bye!");
                       return;
                   }
                }
               while (!(canPlay(move)));
               status = play(move);
               hasPassed = false;
           }
           
           else {
               if (hasPassed) status = GAME_OVER;
               else {
                   hasPassed = true;
                   System.out.println("No available move for you!");
               }
           }
           
           if (status == ONGOING) {
               print();
               System.out.println("Computer's turn: ");
               toggleTurn();
               if (!(generateMoves().isEmpty())) {
                   status = machinePlay();
                   hasPassed = false;
                }
               else {
                   if (hasPassed) status = GAME_OVER;
                   else {
                       hasPassed = true;
                       System.out.println("No available move for me!");
                   }
                }
           }           
           toggleTurn();
        }
        
        determineWinner();
        displayStatus(status);
        System.out.println("Bye bye!");
           
    }
    
    private int tally() {
        int oCount = 0;
        int xCount = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (grid[i][j] == 'x') xCount++;
                if (grid[i][j] == 'o') oCount++; 
            }
        }
        return xCount - oCount;
    }
    
    protected int getMinScore() {return 0 - (grid.length * grid.length);}
    
    public int evaluateMove(Move m) {
        Othello next = new Othello(this);
        int s = next.play(m);
        int tally = next.tally();
        if (turn == 'x') return tally;
        else return (0 - tally);
    }
     
    protected void determineWinner() {
        int tally = tally();
        if (tally > 0) status = X_WON;
        else if (tally < 0) status = O_WON;
        else status = TIE;
    }
    
    public static void main(String[] args) {
        (new Othello(SIZE, new GreedyMove())).loop();
    }
}