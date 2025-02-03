// Pawn.java
import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(boolean isBlack, Coordinate position) {
        super(isBlack, position);
    }
    
    @Override
    public List<Coordinate> getValidMoves(Board board) {
        List<Coordinate> moves = new ArrayList<>();
        int direction = isBlack ? -1 : 1;  // white moves upward (increasing y), black downward
        
        // One square forward.
        Coordinate forward = new Coordinate(position.getX(), position.getY() + direction);
        if (board.inBounds(forward) && board.isEmpty(forward)) {
            moves.add(forward);
            // Two squares forward if first move.
            Coordinate twoForward = new Coordinate(position.getX(), position.getY() + 2 * direction);
            if (moveCount == 0 && board.inBounds(twoForward) && board.isEmpty(twoForward)) {
                moves.add(twoForward);
            }
        }
        
        // Diagonal captures.
        Coordinate diagLeft = new Coordinate(position.getX() - 1, position.getY() + direction);
        if (board.inBounds(diagLeft)) {
            Piece p = board.getPieceAt(diagLeft);
            if (p != null && p.isBlack() != isBlack)
                moves.add(diagLeft);
            // En passant check.
            else if (board.getEnPassantTarget() != null && diagLeft.equals(board.getEnPassantTarget()))
                moves.add(diagLeft);
        }
        Coordinate diagRight = new Coordinate(position.getX() + 1, position.getY() + direction);
        if (board.inBounds(diagRight)) {
            Piece p = board.getPieceAt(diagRight);
            if (p != null && p.isBlack() != isBlack)
                moves.add(diagRight);
            else if (board.getEnPassantTarget() != null && diagRight.equals(board.getEnPassantTarget()))
                moves.add(diagRight);
        }
        return moves;
    }
    
    @Override
    public String getName() {
        return "Pawn";
    }
    
    @Override
    public Piece copy() {
        Pawn copy = new Pawn(isBlack, new Coordinate(position));
        copy.moveCount = this.moveCount;
        return copy;
    }
}
