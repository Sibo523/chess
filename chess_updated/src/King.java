// King.java
import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    public King(boolean isBlack, Coordinate position) {
        super(isBlack, position);
    }
    
    @Override
    public List<Coordinate> getValidMoves(Board board) {
        List<Coordinate> moves = new ArrayList<>();
        // Standard one–square moves.
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                Coordinate c = new Coordinate(position.getX() + dx, position.getY() + dy);
                if (board.inBounds(c)) {
                    Piece p = board.getPieceAt(c);
                    if (p == null || p.isBlack() != isBlack)
                        moves.add(c);
                }
            }
        }
        // Castling moves:
        if (moveCount == 0 && !board.isInCheck(isBlack)) {
            int y = position.getY();
            // Kingside castling.
            Piece kingsideRook = board.getPieceAt(new Coordinate(7, y));
            if (kingsideRook instanceof Rook && kingsideRook.getMoveCount() == 0) {
                if (board.isEmpty(new Coordinate(5, y)) && board.isEmpty(new Coordinate(6, y))) {
                    if (!board.isSquareAttacked(new Coordinate(5, y), !isBlack) &&
                        !board.isSquareAttacked(new Coordinate(6, y), !isBlack))
                    {
                        moves.add(new Coordinate(6, y));
                    }
                }
            }
            // Queenside castling.
            Piece queensideRook = board.getPieceAt(new Coordinate(0, y));
            if (queensideRook instanceof Rook && queensideRook.getMoveCount() == 0) {
                if (board.isEmpty(new Coordinate(1, y)) &&
                    board.isEmpty(new Coordinate(2, y)) &&
                    board.isEmpty(new Coordinate(3, y)))
                {
                    if (!board.isSquareAttacked(new Coordinate(3, y), !isBlack) &&
                        !board.isSquareAttacked(new Coordinate(2, y), !isBlack))
                    {
                        moves.add(new Coordinate(2, y));
                    }
                }
            }
        }
        return moves;
    }
    
    @Override
    public String getName() {
        return "King";
    }
    
    @Override
    public Piece copy() {
        King copy = new King(isBlack, new Coordinate(position));
        copy.moveCount = this.moveCount;
        return copy;
    }
}
