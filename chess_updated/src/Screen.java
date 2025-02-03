// Screen.java
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class Screen {
    private Board board;
    private boolean pieceSelected = false;
    private Coordinate selectedCoord;
    private List<Coordinate> highlightedMoves = new ArrayList<>();
    
    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private JButton[][] boardSquares = new JButton[Board.SIZE][Board.SIZE];
    private Image[][] pieceImages = new Image[2][6];
    private JPanel chessBoard;
    private final JLabel message = new JLabel("Welcome! White's turn.");
    
    // Mapping indices for the sprite sheet.
    private static final int KING = 0, QUEEN = 1, ROOK = 2, KNIGHT = 3, BISHOP = 4, PAWN = 5;
    
    public Screen() throws Exception {
        initializeGui();
        board = new Board();
        updateBoardUI();
    }
    
    private void initializeGui() throws Exception {
        createPieceImages();
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        gui.add(toolBar, BorderLayout.PAGE_START);
        
        Action newGameAction = new AbstractAction("New") {
            @Override
            public void actionPerformed(ActionEvent e) {
                board = new Board();
                updateBoardUI();
                message.setText("New game started! White's turn.");
            }
        };
        toolBar.add(newGameAction);
        toolBar.add(new JButton("Save"));      // (Optional: add functionality)
        toolBar.add(new JButton("Restore"));   // (Optional: add functionality)
        toolBar.addSeparator();
        toolBar.add(new JButton("Resign"));    // (Optional: add functionality)
        toolBar.addSeparator();
        toolBar.add(message);
        
        chessBoard = new JPanel(new GridLayout(Board.SIZE, Board.SIZE));
        chessBoard.setBorder(new CompoundBorder(
                new EmptyBorder(8, 8, 8, 8),
                new LineBorder(Color.BLACK)
        ));
        
        // Create board squares.
        for (int y = Board.SIZE - 1; y >= 0; y--) {
            for (int x = 0; x < Board.SIZE; x++) {
                JButton square = new JButton();
                square.setMargin(new Insets(0, 0, 0, 0));
                final int cx = x, cy = y;
                square.addActionListener(e -> buttonPressed(cx, cy));
                boardSquares[x][y] = square;
                // Color alternating squares.
                if ((x + y) % 2 == 0)
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
        // Clear previous highlights.
        clearHighlights();
        
        if (!pieceSelected) {
            Piece p = board.getPieceAt(coord);
            // Only select a piece if it belongs to the side whose turn it is.
            if (p != null && p.isBlack() == board.getTurnString().equals("Black")) {
                selectedCoord = coord;
                pieceSelected = true;
                highlightSquare(coord, Color.YELLOW);
                // Highlight legal moves.
                List<Coordinate> moves = p.getValidMoves(board);
                // (Optional: you may filter out moves that leave the king in check.)
                for (Coordinate m : moves) {
                    highlightSquare(m, Color.CYAN);
                    highlightedMoves.add(m);
                }
                message.setText(board.getTurnString() + " selected piece at " +
                        (char)('A' + x) + (y + 1) + ".");
            }
        } else {
            // Attempt to move the selected piece.
            if (board.movePiece(selectedCoord, coord)) {
                updateBoardUI();
                message.setText(board.getTurnString() + "'s turn.");
            } else {
                message.setText("Illegal move. " + board.getTurnString() + "'s turn.");
            }
            pieceSelected = false;
        }
    }
    
    // Highlight a square with a colored border.
    private void highlightSquare(Coordinate coord, Color color) {
        JButton button = boardSquares[coord.getX()][coord.getY()];
        button.setBorder(new LineBorder(color, 3));
    }
    
    // Clear all square highlights.
    private void clearHighlights() {
        for (int y = 0; y < Board.SIZE; y++) {
            for (int x = 0; x < Board.SIZE; x++) {
                boardSquares[x][y].setBorder(UIManager.getBorder("Button.border"));
            }
        }
        highlightedMoves.clear();
    }
    
    private void updateBoardUI() {
        clearHighlights();
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
    
    private void createPieceImages() throws Exception {
        // Load the sprite sheet from a URL (adjust as needed).
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
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                JFrame frame = new JFrame("Chess");
                Screen screen = new Screen();
                frame.setContentPane(screen.getGui());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
