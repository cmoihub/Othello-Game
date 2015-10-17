import java.util.*;

public class RandomMove implements MoveStrategy {
    public Move selectMove(BoardGame game) {
        List<Move> moves = game.generateMoves();
        if (moves.isEmpty()) return null;
        Random r = new Random();
        int moveIndex = r.nextInt(moves.size());
        return moves.get(moveIndex);
    }
}
