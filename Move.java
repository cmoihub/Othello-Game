/**
 * A move is just coordinates of where the player wants to play.
 * 
 * @author babak 
 * @version assign2
 */
public class Move {
    private final int row, col;
    public Move() {
        this(0,0);
    }

    public Move(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
}
