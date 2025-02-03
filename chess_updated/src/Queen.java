// Queen.java
import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {

    public Queen(boolean isBlack, Coordinate position) {
        super(isBlack, position);
    }
    
    @Override
    public List<Coordinate> getValidMoves(Board board) {
        // Queen moves as both Rook and Bishop.
        List<Coordinate> moves = new ArrayList<>();
        int[][] directions = {
            {0,1}, {0,-1}, {1,0}, {-1,0},
            {1,1}, {1,-1}, {-1,1}, {-1,-1}
        };
        for (int[] d : directions) {
            int x = position.getX();
            int y = position.getY();
            while (true) {
                x += d[0];
                y += d[1];
                Coordinate c = new Coordinate(x, y);
                if (!board.inBounds(c))
                    break;
                if (board.getPieceAt(c) == null) {
                    moves.add(c);
                } else {
                    if (board.getPieceAt(c).isBlack() != isBlack)
                        moves.add(c);
                    break;
                }
            }
        }
        return moves;
    }
    
    @Override
    public String getName() {
        return "Queen";
    }
    
    @Override
    public Piece copy() {
        Queen copy = new Queen(isBlack, new Coordinate(position));
        copy.moveCount = this.moveCount;
        return copy;
    }
}
