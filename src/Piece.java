// Piece.java
import java.util.List;

public abstract class Piece {
    protected boolean isBlack;
    protected Coordinate position;
    protected int moveCount = 0;  // Used for pawn double–move and castling
    
    public Piece(boolean isBlack, Coordinate position) {
        this.isBlack = isBlack;
        this.position = new Coordinate(position);
    }
    
    public boolean isBlack() { return isBlack; }
    public Coordinate getPosition() { return position; }
    public void setPosition(Coordinate pos) { this.position = new Coordinate(pos); }
    public void incrementMoveCount() { moveCount++; }
    public int getMoveCount() { return moveCount; }
    
    // Return a list of candidate destination coordinates.
    // (The board later tests that a move does not leave the king in check.)
    public abstract List<Coordinate> getValidMoves(Board board);
    
    // A short name for this piece (for debugging or image lookup).
    public abstract String getName();
    
    // IMPORTANT: Return a deep–copy of this piece (including its move count).
    public abstract Piece copy();
}
