// Rook.java
import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {

    public Rook(boolean isBlack, Coordinate position) {
        super(isBlack, position);
    }
    
    @Override
    public List<Coordinate> getValidMoves(Board board) {
        List<Coordinate> moves = new ArrayList<>();
        int[][] directions = { {0,1}, {0,-1}, {1,0}, {-1,0} };
        
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
        return "Rook";
    }
    
    @Override
    public Piece copy() {
        Rook copy = new Rook(isBlack, new Coordinate(position));
        copy.moveCount = this.moveCount;
        return copy;
    }
}
