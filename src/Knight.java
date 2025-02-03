// Knight.java
import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    public Knight(boolean isBlack, Coordinate position) {
        super(isBlack, position);
    }
    
    @Override
    public List<Coordinate> getValidMoves(Board board) {
        List<Coordinate> moves = new ArrayList<>();
        int[][] offsets = {
            {2, 1}, {2, -1}, {1, 2}, {1, -2},
            {-1, 2}, {-1, -2}, {-2, 1}, {-2, -1}
        };
        for (int[] off : offsets) {
            Coordinate c = new Coordinate(position.getX() + off[0], position.getY() + off[1]);
            if (board.inBounds(c)) {
                if (board.getPieceAt(c) == null || board.getPieceAt(c).isBlack() != isBlack)
                    moves.add(c);
            }
        }
        return moves;
    }
    
    @Override
    public String getName() {
        return "Knight";
    }
    
    @Override
    public Piece copy() {
        Knight copy = new Knight(isBlack, new Coordinate(position));
        copy.moveCount = this.moveCount;
        return copy;
    }
}
