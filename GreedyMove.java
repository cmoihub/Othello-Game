import java.util.*;

public class GreedyMove implements MoveStrategy {
    public Move selectMove(BoardGame game) {
        List<Move> moves = game.generateMoves();
        if (moves.isEmpty()) return null;
        
        int currentScore = game.getMinScore();
        Move candidateMove = null; 
  
        for (Move m : moves) {
            int score = game.evaluateMove(m);
            if (score >= currentScore) {
                candidateMove = m;
                currentScore = score;
            }
        }
        return candidateMove;
    }
}
