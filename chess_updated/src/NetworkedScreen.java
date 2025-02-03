import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class NetworkedScreen {
    private Board board;
    private boolean pieceSelected = false;
    private Coordinate selectedCoord;
    private List<Coordinate> highlightedMoves = new ArrayList<>();
    
    private final JPanel gui = new JPanel(new BorderLayout(3,3));
    private JButton[][] boardSquares = new JButton[Board.SIZE][Board.SIZE];
    private Image[][] pieceImages = new Image[2][6];
    private JPanel chessBoard;
    private final JLabel messageLabel = new JLabel("Waiting for game to start...");
    
    // Mapping indices for the sprite sheet.
    private static final int KING = 0, QUEEN = 1, ROOK = 2, KNIGHT = 3, BISHOP = 4, PAWN = 5;
    
    private String playerColor = "UNKNOWN";
    
    public NetworkedScreen() throws Exception {
        board = new Board(); // local copy; server messages will update this board.
        initializeGui();
        updateBoardUI();
    }
    
    private void initializeGui() throws Exception {
        createPieceImages();
        gui.setBorder(new EmptyBorder(5,5,5,5));
        
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(messageLabel);
        gui.add(topPanel, BorderLayout.PAGE_START);
        
        chessBoard = new JPanel(new GridLayout(Board.SIZE, Board.SIZE));
        chessBoard.setBorder(new CompoundBorder(new EmptyBorder(8,8,8,8), new LineBorder(Color.BLACK)));
        for (int y = Board.SIZE - 1; y >= 0; y--) {
            for (int x = 0; x < Board.SIZE; x++) {
                JButton square = new JButton();
                square.setMargin(new Insets(0,0,0,0));
                final int cx = x, cy = y;
                square.addActionListener(e -> buttonPressed(cx, cy));
                boardSquares[x][y] = square;
                if ((x+y)%2 == 0)
                    square.setBackground(Color.WHITE);
                else
                    square.setBackground(Color.GRAY);
                chessBoard.add(square);
            }
        }
        gui.add(chessBoard, BorderLayout.CENTER);
    }
    
    private void buttonPressed(int x, int y) {
        Coordinate coord = new Coordinate(x, y);
        clearHighlights();
        if (!pieceSelected) {
            Piece p = board.getPieceAt(coord);
            // Only allow selection if the piece belongs to the player.
            if (p != null && ((playerColor.equals("WHITE") && !p.isBlack()) || (playerColor.equals("BLACK") && p.isBlack()))) {
                selectedCoord = coord;
                pieceSelected = true;
                highlightSquare(coord, Color.YELLOW);
                List<Coordinate> moves = p.getValidMoves(board); // (not filtering for check in this demo)
                for (Coordinate m : moves) {
                    highlightSquare(m, Color.CYAN);
                    highlightedMoves.add(m);
                }
                setMessage("Selected piece at " + (char)('A' + x) + (y+1));
            }
        } else {
            // When a move is chosen, send it to the server.
            int fromX = selectedCoord.getX();
            int fromY = selectedCoord.getY();
            int toX = x;
            int toY = y;
            ChessClient.sendMoveStatic(fromX, fromY, toX, toY);
            pieceSelected = false;
        }
    }
    
    // Called by the client when a MOVE message is received.
    public void applyMove(int fromX, int fromY, int toX, int toY) {
        Coordinate from = new Coordinate(fromX, fromY);
        Coordinate to = new Coordinate(toX, toY);
        board.makeMove(from, to);
        updateBoardUI();
    }
    
    public void setPlayerColor(String color) {
        this.playerColor = color;
    }
    
    public void setMessage(String msg) {
        SwingUtilities.invokeLater(() -> messageLabel.setText(msg));
    }
    
    private void updateBoardUI() {
        for (int y = 0; y < Board.SIZE; y++) {
            for (int x = 0; x < Board.SIZE; x++) {
                Piece p = board.getPieceAt(new Coordinate(x, y));
                JButton square = boardSquares[x][y];
                if (p != null) {
                    int colorIndex = p.isBlack() ? 1 : 0;
                    int pieceIndex;
                    if (p instanceof King) pieceIndex = KING;
                    else if (p instanceof Queen) pieceIndex = QUEEN;
                    else if (p instanceof Rook) pieceIndex = ROOK;
                    else if (p instanceof Knight) pieceIndex = KNIGHT;
                    else if (p instanceof Bishop) pieceIndex = BISHOP;
                    else if (p instanceof Pawn) pieceIndex = PAWN;
                    else pieceIndex = PAWN;
                    square.setIcon(new ImageIcon(pieceImages[colorIndex][pieceIndex]));
                } else {
                    square.setIcon(null);
                }
            }
        }
    }
    
    private void highlightSquare(Coordinate coord, Color color) {
        boardSquares[coord.getX()][coord.getY()].setBorder(new LineBorder(color, 3));
    }
    
    private void clearHighlights() {
        for (int y = 0; y < Board.SIZE; y++) {
            for (int x = 0; x < Board.SIZE; x++) {
                boardSquares[x][y].setBorder(UIManager.getBorder("Button.border"));
            }
        }
        highlightedMoves.clear();
    }
    
    private void createPieceImages() throws Exception {
        URL url = new URL("https://i.stack.imgur.com/memI0.png");
        BufferedImage bi = ImageIO.read(url);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 6; j++) {
                pieceImages[i][j] = bi.getSubimage(j * 64, i * 64, 64, 64);
            }
        }
    }
    
    public JComponent getGui() {
        return gui;
    }
}
