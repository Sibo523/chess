import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;
import java.net.Proxy;
import java.net.URL;
import javax.imageio.ImageIO;

public class Screen {
    Board b ;
    boolean flag = false,first = false;
    cord fir;

    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private JButton[][] chessBoardSquares = new JButton[8][8];
    private Image[][] chessPieceImages = new Image[2][6];
    private JPanel chessBoard;

    private final JLabel message = new JLabel(
            "Chess sabon is ready to play!");
    private static final String COLS = "ABCDEFGH";
    public static final int QUEEN = 0, KING = 1,
            ROOK = 2, KNIGHT = 3, BISHOP = 4, PAWN = 5;
    public static final int[] STARTING_ROW = {
            ROOK, KNIGHT, BISHOP, KING, QUEEN, BISHOP, KNIGHT, ROOK
    };
    public static final int BLACK = 0, WHITE = 1;

    Screen() throws Exception {
        initializeGui();
    }


    public final void initializeGui() throws Exception {
        // create the images for the chess pieces
        createImages();

        // set up the main GUI
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);
        Action newGameAction = new AbstractAction("New") {
            @Override
            public void actionPerformed(ActionEvent e) {
                setupNewGame();
                b = new Board();
                flag = false;
              //  b.print();
            }
        };
        tools.add(new JButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        }));
        tools.add(newGameAction);
        tools.add(new JButton("Save")); // TODO - add functionality!
        tools.add(new JButton("Restore")); // TODO - add functionality!
        tools.addSeparator();
        tools.add(new JButton("Resign")); // TODO - add functionality!
        tools.addSeparator();
        tools.add(message);

        gui.add(new JLabel("?"), BorderLayout.LINE_START);

        chessBoard = new JPanel(new GridLayout(0, 9)) {

            /**
             * Override the preferred size to return the largest it can, in
             * a square shape.  Must (must, must) be added to a GridBagLayout
             * as the only component (it uses the parent as a guide to size)
             * with no GridBagConstaint (so it is centered).
             */
            @Override
            public final Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                Dimension prefSize = null;
                Component c = getParent();
                if (c == null) {
                    prefSize = new Dimension(
                            (int) d.getWidth(), (int) d.getHeight());
                } else if (c != null &&
                        c.getWidth() > d.getWidth() &&
                        c.getHeight() > d.getHeight()) {
                    prefSize = c.getSize();
                } else {
                    prefSize = d;
                }
                int w = (int) prefSize.getWidth();
                int h = (int) prefSize.getHeight();
                // the smaller of the two sizes
                int s = (w > h ? h : w);
                return new Dimension(s, s);
            }
        };
        chessBoard.setBorder(new CompoundBorder(
                new EmptyBorder(8, 8, 8, 8),
                new LineBorder(Color.BLACK)
        ));
        // Set the BG to be ochre
        Color ochre = new Color(173, 216, 230);
        chessBoard.setBackground(ochre);
        JPanel boardConstrain = new JPanel(new GridBagLayout());
        boardConstrain.setBackground(ochre);
        boardConstrain.add(chessBoard);
        gui.add(boardConstrain);

        // create the chess board squares
        Insets buttonMargin = new Insets(0, 0, 0, 0);
        for (int ii = 0; ii < chessBoardSquares.length; ii++) {
            for (int jj = 0; jj < chessBoardSquares[ii].length; jj++) {
                JButton b = new JButton();
                b.setMargin(buttonMargin);
                // our chess pieces are 64x64 px in size, so we'll
                // 'fill this in' using a transparent icon..
                final int y = ii;
                final int x = jj;
                b.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            buttonPressed(y, x);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }

                });

                ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
                b.setIcon(icon);
                if ((jj % 2 == 1 && ii % 2 == 1)
                        //) {
                        || (jj % 2 == 0 && ii % 2 == 0)) {
                    b.setBackground(Color.WHITE);
                } else {
                    b.setBackground(Color.BLACK);
                }
                chessBoardSquares[jj][ii] = b;
            }
        }

        /*
         * fill the chess board
         */
        chessBoard.add(new JLabel(""));
        // fill the top row
        for (int ii = 0; ii < 8; ii++) {
            chessBoard.add(
                    new JLabel(COLS.substring(ii, ii + 1),
                            SwingConstants.CENTER));
        }
        // fill the black non-pawn piece row
        for (int ii = 0; ii < 8; ii++) {
            for (int jj = 0; jj < 8; jj++) {
                switch (jj) {
                    case 0:
                        chessBoard.add(new JLabel("" + (9 - (ii + 1)),
                                SwingConstants.CENTER));
                    default:
                        chessBoard.add(chessBoardSquares[jj][ii]);
                }
            }
        }
    }

    private void buttonPressed(int row, int col) throws Exception {
        JButton clickedButton = chessBoardSquares[col][row];
       // System.out.println("Button pressed at row: " + row + ", col: " + col);
        // You can do additional processing or logic here based on the button press
        if (!flag) {
            // Select the source square
            if (b.extract(new cord(col,row)) != null) {
                fir = new cord(col, row);
                flag = true;
            }
        } else {
            // Move the piece and update the board
            if (b.move(fir, new cord(col, row))) {
                b.setTurn();
                // Update the graphical representation (UI)
                Piece temp = b.extract(new cord(col,row));
                int key = translator(temp.getKey());
                int black = blacky(temp.isBlack());
                ImageIcon icon = new ImageIcon(chessPieceImages[black][key]); // they don't have the same key
                chessBoardSquares[col][row].setIcon(icon);
                chessBoardSquares[fir.getX()][fir.getY()].setIcon(null);
                //b.print();
                // Clear the source selection
                flag = false;
            }
            flag = false;
        }
    }
    public int blacky(boolean black){
        if(black){
            return 1;
        }
        return 0;
    }
    public int translator(int key){
        if(key == 1 ){return 5;} // it's a pawn
        if(key == 2){ return 2;} // it's a rook
        if(key == 3){ return 3;} // it's a knight
        if(key == 4){ return 4;} //it's a bish
        if(key == 5){ return 1;} // it's a queen
        if(key == 6){ return 0;} //it's a king
        System.out.println("A problem has accured");
        return 8;
    }

    public final JComponent getGui() {
        return gui;
    }

    private final void createImages() {
        try {
            URL url = new URL("https://i.stack.imgur.com/memI0.png");
            BufferedImage bi = ImageIO.read(url);
            for (int ii = 0; ii < 2; ii++) {
                for (int jj = 0; jj < 6; jj++) {
                    chessPieceImages[ii][jj] = bi.getSubimage(
                            jj * 64, ii * 64, 64, 64);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Initializes the icons of the initial chess board piece places
     */
    private final void setupNewGame() {
        message.setText("Make your move!");
        // set up the black pieces
        for (int ii = 0; ii < STARTING_ROW.length; ii++) {
            chessBoardSquares[ii][0].setIcon(new ImageIcon(
                    chessPieceImages[BLACK][STARTING_ROW[ii]]));
        }
        for (int ii = 0; ii < STARTING_ROW.length; ii++) {
            chessBoardSquares[ii][1].setIcon(new ImageIcon(
                    chessPieceImages[BLACK][PAWN]));
        }
        // set up the white pieces
        for (int ii = 0; ii < STARTING_ROW.length; ii++) {
            chessBoardSquares[ii][6].setIcon(new ImageIcon(
                    chessPieceImages[WHITE][PAWN]));
        }
        for (int ii = 0; ii < STARTING_ROW.length; ii++) {
            chessBoardSquares[ii][7].setIcon(new ImageIcon(
                    chessPieceImages[WHITE][STARTING_ROW[ii]]));
        }

    }

    public static void main(String[] args) {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                Screen cg = null;
                try {
                    cg = new Screen();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                JFrame f = new JFrame("ChessSabon");
                f.add(cg.getGui());
                // Ensures JVM closes after frame(s) closed and
                // all non-daemon threads are finished
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                // See https://stackoverflow.com/a/7143398/418556 for demo.
                f.setLocationByPlatform(true);

                // ensures the frame is the minimum size it needs to be
                // in order display the components within it
                f.pack();
                // ensures the minimum size is enforced.
                f.setMinimumSize(f.getSize());
                f.setVisible(true);
            }
        };
        // Swing GUIs should be created and updated on the EDT
        // http://docs.oracle.com/javase/tutorial/uiswing/concurrency
        SwingUtilities.invokeLater(r);
    }
}