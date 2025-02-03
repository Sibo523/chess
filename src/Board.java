// Board.java
import java.util.List;

public class Board implements Cloneable {
    public static final int SIZE = 8;
    private Piece[][] board;
    private boolean blackToMove;  // false = White’s turn, true = Black’s turn.
    private int moveCount;
    private Coordinate enPassantTarget;  // null if no en passant is possible.
    
    public Board() {
        board = new Piece[SIZE][SIZE];
        blackToMove = false; // White starts.
        moveCount = 1;
        enPassantTarget = null;
        setupPieces();
    }
    
    private void setupPieces() {
        // Pawns:
        for (int x = 0; x < SIZE; x++) {
            board[x][1] = new Pawn(false, new Coordinate(x, 1)); // white
            board[x][6] = new Pawn(true, new Coordinate(x, 6));  // black
        }
        // White major pieces (back rank at y = 0):
        board[0][0] = new Rook(false, new Coordinate(0, 0));
        board[7][0] = new Rook(false, new Coordinate(7, 0));
        board[1][0] = new Knight(false, new Coordinate(1, 0));
        board[6][0] = new Knight(false, new Coordinate(6, 0));
        board[2][0] = new Bishop(false, new Coordinate(2, 0));
        board[5][0] = new Bishop(false, new Coordinate(5, 0));
        board[3][0] = new Queen(false, new Coordinate(3, 0));
        board[4][0] = new King(false, new Coordinate(4, 0));
        // Black major pieces (back rank at y = 7):
        board[0][7] = new Rook(true, new Coordinate(0, 7));
        board[7][7] = new Rook(true, new Coordinate(7, 7));
        board[1][7] = new Knight(true, new Coordinate(1, 7));
        board[6][7] = new Knight(true, new Coordinate(6, 7));
        board[2][7] = new Bishop(true, new Coordinate(2, 7));
        board[5][7] = new Bishop(true, new Coordinate(5, 7));
        board[3][7] = new Queen(true, new Coordinate(3, 7));
        board[4][7] = new King(true, new Coordinate(4, 7));
    }
    
    public boolean inBounds(Coordinate c) {
        return c.getX() >= 0 && c.getX() < SIZE && c.getY() >= 0 && c.getY() < SIZE;
    }
    
    public boolean isEmpty(Coordinate c) {
        return inBounds(c) && getPieceAt(c) == null;
    }
    
    public Piece getPieceAt(Coordinate c) {
        if (!inBounds(c)) return null;
        return board[c.getX()][c.getY()];
    }
    
    public void setPieceAt(Coordinate c, Piece piece) {
        if (inBounds(c)) {
            board[c.getX()][c.getY()] = piece;
            if (piece != null)
                piece.setPosition(c);
        }
    }
    
    public Coordinate getEnPassantTarget() {
        return enPassantTarget;
    }
    
    // Returns a string indicating whose turn it is.
    public String getTurnString() {
        return blackToMove ? "Black" : "White";
    }
    
    /**
     * Attempts to move a piece from “from” to “to.”
     * Returns true if the move is legal and executed.
     */
    public boolean movePiece(Coordinate from, Coordinate to) {
        Piece piece = getPieceAt(from);
        if (piece == null) return false;
        if (piece.isBlack() != blackToMove) return false;
        
        List<Coordinate> validMoves = piece.getValidMoves(this);
        boolean found = false;
        for (Coordinate move : validMoves) {
            if (move.equals(to)) {
                found = true;
                break;
            }
        }
        if (!found) return false;
        
        // Clone board and test that our move does not leave our king in check.
        Board testBoard = this.clone();
        testBoard.makeMove(from, to);
        if (testBoard.isInCheck(piece.isBlack()))
            return false;
        
        // The move is legal.
        makeMove(from, to);
        blackToMove = !blackToMove;
        moveCount++;
        return true;
    }
    
    /**
     * Executes the move (assumes it is legal) and handles pawn promotion,
     * en passant capture, and castling.
     */
    public void makeMove(Coordinate from, Coordinate to) {
        Piece piece = getPieceAt(from);
        if (piece == null) return;
        
        // Save current en passant target.
        Coordinate currentEPT = enPassantTarget;
        enPassantTarget = null;  // reset
        
        // En passant capture.
        if (piece instanceof Pawn && currentEPT != null && to.equals(currentEPT)) {
            int direction = piece.isBlack() ? -1 : 1;
            Coordinate capturedPawn = new Coordinate(to.getX(), to.getY() - direction);
            setPieceAt(capturedPawn, null);
        }
        
        // Clear destination (normal capture if any).
        setPieceAt(to, null);
        setPieceAt(from, null);
        
        // Pawn double–move: set en passant target.
        if (piece instanceof Pawn && Math.abs(to.getY() - from.getY()) == 2) {
            int direction = piece.isBlack() ? -1 : 1;
            enPassantTarget = new Coordinate(from.getX(), from.getY() + direction);
        }
        
        // Pawn promotion: auto-promote to Queen if reaching the last rank.
        if (piece instanceof Pawn && (to.getY() == 0 || to.getY() == SIZE - 1)) {
            piece = new Queen(piece.isBlack(), to);
        } else {
            piece.setPosition(to);
        }
        piece.incrementMoveCount();
        setPieceAt(to, piece);
        
        // Handle castling:
        // If the king moved two squares horizontally, then also move the rook.
        if (piece instanceof King) {
            int deltaX = to.getX() - from.getX();
            if (Math.abs(deltaX) == 2) { // castling move
                int y = to.getY();
                if (deltaX > 0) { // kingside
                    Piece rook = getPieceAt(new Coordinate(7, y));
                    setPieceAt(new Coordinate(7, y), null);
                    setPieceAt(new Coordinate(5, y), rook);
                    if (rook != null) rook.incrementMoveCount();
                } else { // queenside
                    Piece rook = getPieceAt(new Coordinate(0, y));
                    setPieceAt(new Coordinate(0, y), null);
                    setPieceAt(new Coordinate(3, y), rook);
                    if (rook != null) rook.incrementMoveCount();
                }
            }
        }
    }
    
    /**
     * Checks whether the king of the given color is in check.
     */
    public boolean isInCheck(boolean black) {
        Coordinate kingPos = findKing(black);
        if (kingPos == null) return false;
        // Check whether any enemy piece attacks the king.
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                Piece p = board[x][y];
                if (p != null && p.isBlack() != black) {
                    // For pawns and kings, use special attack patterns.
                    if (p instanceof Pawn) {
                        int dir = p.isBlack() ? -1 : 1;
                        Coordinate leftAttack = new Coordinate(x - 1, y + dir);
                        Coordinate rightAttack = new Coordinate(x + 1, y + dir);
                        if (kingPos.equals(leftAttack) || kingPos.equals(rightAttack))
                            return true;
                    } else if (p instanceof King) {
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                if (dx == 0 && dy == 0) continue;
                                Coordinate adj = new Coordinate(x + dx, y + dy);
                                if (kingPos.equals(adj))
                                    return true;
                            }
                        }
                    } else {
                        List<Coordinate> moves = p.getValidMoves(this);
                        if (moves.contains(kingPos))
                            return true;
                    }
                }
            }
        }
        return false;
    }
    
    private Coordinate findKing(boolean black) {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                Piece p = board[x][y];
                if (p != null && p.isBlack() == black && p instanceof King)
                    return new Coordinate(x, y);
            }
        }
        return null;
    }
    
    /**
     * Returns whether a given square is attacked by pieces of color attackerIsBlack.
     * (For pawns and kings we check only their immediate attack squares.)
     */
    public boolean isSquareAttacked(Coordinate square, boolean attackerIsBlack) {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                Piece p = board[x][y];
                if (p != null && p.isBlack() == attackerIsBlack) {
                    if (p instanceof Pawn) {
                        int dir = attackerIsBlack ? -1 : 1;
                        Coordinate leftAttack = new Coordinate(x - 1, y + dir);
                        Coordinate rightAttack = new Coordinate(x + 1, y + dir);
                        if (square.equals(leftAttack) || square.equals(rightAttack))
                            return true;
                    } else if (p instanceof King) {
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                if (dx == 0 && dy == 0) continue;
                                Coordinate adj = new Coordinate(x + dx, y + dy);
                                if (square.equals(adj))
                                    return true;
                            }
                        }
                    } else {
                        List<Coordinate> moves = p.getValidMoves(this);
                        if (moves.contains(square))
                            return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public Board clone() {
        Board copy = new Board();
        copy.blackToMove = this.blackToMove;
        copy.moveCount = this.moveCount;
        copy.enPassantTarget = (this.enPassantTarget == null) ? null : new Coordinate(this.enPassantTarget);
        copy.board = new Piece[SIZE][SIZE];
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                Piece p = this.board[x][y];
                if (p != null)
                    copy.board[x][y] = p.copy();
            }
        }
        return copy;
    }
    
    // For debugging: print a simple text version of the board.
    public void printBoard() {
        for (int y = SIZE - 1; y >= 0; y--) {
            System.out.print((y + 1) + " ");
            for (int x = 0; x < SIZE; x++) {
                Piece p = board[x][y];
                if (p == null)
                    System.out.print(". ");
                else
                    System.out.print(p.getName().charAt(0) + " ");
            }
            System.out.println();
        }
        System.out.println("  A B C D E F G H");
    }
}
