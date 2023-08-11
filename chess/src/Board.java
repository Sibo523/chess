import javax.swing.plaf.DimensionUIResource;
import java.util.LinkedList;

public class Board {
    private int num = 1; // move counter
    public final int pawn = 1;
    public final int rook = 2;
    public final int knight = 3;
    public final int bish = 4;
    public final int queen = 5;
    public final int king = 6;
    int black_count = 0;
    int white_count = 0;
    boolean promote = false, castle = false;
    private boolean w_check = false, b_check = false;
    public Piece[][] board = new Piece[8][8];

    public Board() {
        board = new Piece[8][8];
        init();
        black_count = 39;
        white_count = 39;
    }

    public Board(Piece[][] board, Piece[] black, Piece[] white) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == null) continue;
                this.board[i][j] = new Piece(board[i][j]);              // make a new array full of pieces after the did
            }
        }
        for (int i = 0; i < black.length; i++) {
            this.black[i] = new Piece(black[i]);
            this.white[i] = new Piece(white[i]);
        }
    }

    public Piece getBlack(int i) {
        return black[i];
    }

    public Piece getWhite(int i) {
        return white[i];
    }


    private Piece[] black = new Piece[16];
    private Piece[] white = new Piece[16];

    public void init() {  // resets the board! and the above arrays
        arr_reset();
        board_reset();
    }

    public void print() {
        System.out.println("  _______________________________________________________");
        for (int i = 7; i >= 0; i--) { // goes a row down
            System.out.print(i + 1 + " |"); // Row label
            for (int j = 0; j < 8; j++) { // print a row
                if (getBoard(j, i) == null) {
                    System.out.print("     |"); // Empty cell
                } else {
                    String pieceName = getBoard(j, i).name();
                    int spacesToAdd = 5 - pieceName.length();
                    System.out.print(" " + pieceName);
                    for (int k = 0; k < spacesToAdd; k++) {
                        System.out.print(" ");
                    }
                    System.out.print("|"); // Piece label
                }
            }
            System.out.println();
            System.out.println("  ________________________________________________________");
        }
        System.out.println("    a   |   b    | c   |  d   |  e    | f    |  g   |  h"); // Column labels
    }


    /*
    keep in mind that you need to have the counter for the position for the bot and you didn't do that
    when you remove something you need to sub it from the total sum
    you can also just go over the whole map and look what is the position (easier doesn't take too much time)
     */
    public boolean move(cord cur, cord dest) throws Exception { // if we move return true and moves else return false and does nothing
        Piece start = extract(cur);
        Piece end = extract(dest);
        if (!inBoard(dest)) {
            return false;
        }
        if(end != null) {
            // if they are the same color and one of them are rook and the other one is king then what we do is castle
            castle = ((start.getKey() == king && end.getKey() == rook) || (start.getKey() == rook && end.getKey() == king)) && same_color(cur, dest);
        }
        boolean flag = could(start, end, cur, dest);

        if (flag || castle) { // now we need to see if the king is attacked
            if (extract(cur).isBlack()) { // black to move
                Board temp = new Board(this.board, this.black, this.white); // we got temporary board now
                if (castle) {
                    if (!isClear(cur, dest)) return false;
                    temp.castle(cur, dest);
                } else {
                    temp.remove(cur, dest, start);
                }        //move the piece and now check if our king is attacked
                if (!temp.Wcheck()) {
                    // if it's pawn, and we are going to the last raw we want to promote
                    promote = start.getKey() == pawn && (dest.getY() == 7 || dest.getY() == 0);
                    if (castle) {
                        castle(cur, dest);
                    } else {
                        remove(cur, dest, start);
                    }           //sets the start in the new cord and moves everything
                    if (promote) {
                        start.promote();
                    }
                    w_check = Wcheck();
                    return true;
                }
            } else {                                   //white to move
                Board temp = new Board(this.board, this.black, this.white); // we got temporary board now
                if (castle) {
                    if (!isClear(cur, dest)) return false;
                    temp.castle(cur, dest);
                } else {
                    temp.remove(cur, dest, start);
                }        //move the piece and now check if our king is attacked
                if (!temp.Bcheck()) {
                    // if it's pawn, and we are going to the last raw we want to promote
                    promote = start.getKey() == pawn && (dest.getY() == 7 || dest.getY() == 0);
                    if (castle) {
                        castle(cur, dest);
                    } else {
                        remove(cur, dest, start);
                    } //sets the start in the new cord, eats if needs to eat
                    if (promote) { // if we established that it's a promoting situation then we promote the pawn to whatever
                        start.promote();
                    }
                    b_check = Wcheck();
                    return true;
                }
            }
        }
        return false;               //then we couldn't do that move for various reasons! the king is now attacked and so forth
    }

    private boolean could(Piece start, Piece end, cord cur, cord dest) throws Exception {
        LinkedList<cord> here = new LinkedList<>();
        // find the right piece and import all the valid moves it can do
        if (start.getKey() == pawn) {
            here.addAll(pawn(cur));
        } else if (start.getKey() == knight) {
            here.addAll(knight(cur));
        } else if (start.getKey() == bish) {
            here.addAll(bishof(cur));
        } else if (start.getKey() == rook) {
            here.addAll(rock(cur));
        } else if (start.getKey() == queen) {
            here.addAll(queer(cur));
        } else if (start.getKey() == king) {
            here.addAll(King(cur));
        }
        for (cord cord : here) {
            if (cord.equals(dest)) {
                return true;
            }
        }
        return false;
    } // this function tells me if the move could be ok

    ////////////////////////////////// need to do defence!!!!!!!!!!/////////////////////////////////////////
    // instead of defence we can do did it defend

    public boolean Bcheck() throws Exception { // we need to know who we are even checking
        LinkedList<cord> blacky = new LinkedList<>();
        for (Piece piece : black) { //white being checked
            if (piece.getKey() == bish) {
                blacky.addAll(bishof(piece.getCord()));
            } else if (piece.getKey() == rook) {
                blacky.addAll(rock(piece.getCord()));
            } else if (piece.getKey() == queen) {
                blacky.addAll(queer(piece.getCord()));
            } else if (piece.getKey() == pawn) {
                blacky.addAll(pawn(piece.getCord()));
            } else if (piece.getKey() == knight) {
                blacky.addAll(knight(piece.getCord()));
            } else if (piece.getKey() == king) {
                blacky.addAll(King(piece.getCord()));
            }

        }
        for (int i = 0; i < blacky.size(); i++) {
            if (white[15].getCord().equals(blacky.get(i))) { // then it's a check from black to white
                return true;
            }
        }
        return false;
    }

    private boolean Wcheck() throws Exception { // black being checked
        LinkedList<cord> whitey = new LinkedList<>();
        for (Piece piece : white) {
            if (piece.getKey() == bish) {
                whitey.addAll(bishof(piece.getCord()));
            } else if (piece.getKey() == rook) {
                whitey.addAll(rock(piece.getCord()));
            } else if (piece.getKey() == queen) {
                whitey.addAll(queer(piece.getCord()));
            } else if (piece.getKey() == pawn) {
                whitey.addAll(pawn(piece.getCord()));
            } else if (piece.getKey() == knight) {
                whitey.addAll(knight(piece.getCord()));
            } else if (piece.getKey() == king) {
                whitey.addAll(King(piece.getCord()));
            }
        }
        for (int i = 0; i < whitey.size(); i++) {
            if (black[15].getCord().equals(whitey.get(i))) { // then it's a check from white to black
                return true;
            }
        }
        return false;
    }


    private void remove(cord cur, cord dest, Piece attacker) {
        attacker.setmove(num);
        attacker.setCor(dest);
        board[cur.getX()][cur.getY()] = null;
        board[dest.getX()][dest.getY()] = attacker;

    }

    /**
     * this function castles I need to check outside if it's legal, but it does basic switch
     *
     * @param king
     * @param rook
     */
    private void castle(cord king, cord rook) {

        cord kinger;
        cord rooker; // the new cords that we need to go, in case of a castle

        if (king.getX() < rook.getX()) { // this is how the castle should look like
            kinger = new cord(6, king.getY());
            rooker = new cord(5, rook.getY());
        } else {                           // else this is how the castle should look like
            kinger = new cord(2, king.getY());
            rooker = new cord(3, rook.getY());
        }
        remove(king, kinger, extract(king)); // switch to the places that I want
        remove(rook, rooker, extract(rook)); // canal
    }

    /**
     * from here I am starting to return a linked list of the possible lemurs
     */
    LinkedList<cord> rock(cord cur) throws Exception {
        LinkedList<cord> rocker = new LinkedList<cord>();
        cord[] arr = {new cord(0, 1), new cord(0, -1), new cord(1, 0), new cord(-1, 0)};

        for (int i = 0; i < arr.length; i++) {
            cord here = new cord(cur);
            here.add(arr[i]);
            while (inBoard(here) && (extract(here) == null)) {
                rocker.add(new cord(here));
                here.add(arr[i]);
            }
            if(inBoard(here)) {
                if(extract(here)==null){
                    rocker.add(here);
                }
                else if (!same_color(cur,here)) {
                    rocker.add(here);
                }

            }
        }
        return rocker;
    }

    LinkedList<cord> bishof(cord cur) throws Exception {
        LinkedList<cord> bisher = new LinkedList<cord>();
        cord[] arr = {new cord(1, 1), new cord(1, -1), new cord(-1, 1), new cord(-1, -1)};
        for (int i = 0; i < arr.length; i++) {
            cord here = new cord(cur);
            here.add(arr[i]);
            while (inBoard(here) && (extract(here) == null)) {
                bisher.add(here);
                here.add(arr[i]);
            }
            if(inBoard(here)) {
                if (!same_color(here, cur)) { // I did here try and catch for some reason, not sure why
                    bisher.add(here);
                }
            }
        }
        return bisher;
    }

    LinkedList<cord> queer(cord cur) throws Exception {
        LinkedList<cord> gay = new LinkedList<>(bishof(cur)); // inteliJ told me to do that like that
        gay.addAll(rock(cur));
        return gay;
    }

    LinkedList<cord> pawn(cord cur) {
        LinkedList<cord> places = new LinkedList<>();
        cord temp = new cord(cur);
        if(!inBoard(cur))return places;
        if(extract(cur)==null)return places;
        if (!extract(cur).isBlack()) {// then it's white we look from downwards up!
            cord Uright = new cord(cur.getX() + 1, cur.getY() + 1);
            cord Uleft = new cord(cur.getX() - 1, cur.getY() + 1);
            cord up = new cord(cur.getX(), cur.getY() + 1);
            cord right = new cord(cur.getX() + 1, cur.getY());
            cord left = new cord(cur.getX() - 1, cur.getY());
            if (extract(cur).when() == 0) {
                cord dU = new cord(cur.getX(), cur.getY() + 2);
                if (inBoard(dU) && extract(dU) == null) {
                    places.add(dU);
                }
            }
            if (inBoard(Uright)) {
                if (extract(up) == null) {
                    places.add(up);
                }
            }
            if (inBoard(Uright)) {
                if (extract(Uright) != null) {
                    places.add(Uright);
                }
            } else if (inBoard(right)) {
                if ((extract(right).getKey() == pawn && extract(right).when() == num - 1 && extract(right).getCounter() == 1)) { // en pasant
                    places.add(right);
                }
            }
            if (inBoard(Uleft)) {
                if (extract(Uleft) != null) {
                    places.add(Uright);
                }
            } else if (inBoard(left)) {
                if ((extract(left).getKey() == pawn && extract(left).when() == num - 1 && extract(left).getCounter() == 1)) { // en pasant
                    places.add(left);
                }
            }
        } else { // then we go downwards we are black
            cord Dright = new cord(cur.getX() + 1, cur.getY() - 1);
            cord Dleft = new cord(cur.getX() - 1, cur.getY() - 1);
            cord down = new cord(cur.getX(), cur.getY() - 1);
            cord right = new cord(cur.getX() + 1, cur.getY());
            cord left = new cord(cur.getX() - 1, cur.getY());
            if (extract(cur).when() == 0) { // the option to move twice in one turn
                cord dd = new cord(cur.getX(), cur.getY() - 2);
                if (inBoard(dd) && extract(dd) == null) {
                    places.add(dd);
                }
            }
            if (inBoard(down)) {
                if (extract(down) == null) {
                    // the promotion is irrelevnt here I just wnat to know where I can move to
                    places.add(down);
                }
            }
            if (inBoard(Dright)) {
                if (extract(Dright) != null) {
                    places.add(Dright);
                }
            } else if (inBoard(right)) {
                if (extract(right).getKey() == pawn && extract(right).when() == num - 1 && extract(right).getCounter() == 1) { // en pasant
                    places.add(right);
                }
            }
            if (inBoard(Dleft)) {
                if (extract(Dleft) != null) {
                    places.add(Dright);
                }
            } else if (inBoard(left)) {
                if (extract(left).getKey() == pawn && extract(left).when() == num - 1 && extract(left).getCounter() == 1) { // en pasant
                    places.add(left);
                }
            }
        }
        return places;
    }

    LinkedList<cord> knight(cord cur) {
        LinkedList<cord> places = new LinkedList<>();
        cord[] arr =
                {new cord(2, 1), new cord(2, -1), new cord(1, -2), new cord(-1, -2),
                        new cord(-2, -1), new cord(-2, 1),
                        new cord(-1, 2), new cord(1, 2)};

        for (cord cord : arr) {
            cord here = new cord(cur);
            here.add(cord);
            if(inBoard(here)){
                if (extract(here) == null ) { // empty or the other side
                    places.add(here);
                }

                else {
                    try {
                        if (extract(here).isBlack() != extract(cur).isBlack()) {
                            places.add(here);
                        }
                    }
                    catch (NullPointerException e){
                        continue;
                    }
                }
            }
        }
        return places;
    }

    /*
    didn't do yet is checked ///////////////////////////////////////ing ** done!

     */

    LinkedList<cord> King(cord cur) {
        LinkedList<cord> lesbian = new LinkedList<>();
        Piece here = extract(cur);
        Piece wRr = extract(new cord(7, 0));
        Piece wRl = extract(new cord(0, 0));
        Piece bRr = extract(new cord(7, 7));
        Piece nRl = extract(new cord(0, 7));
        if (here.when() == 0 && wRr != null && wRr.when() == 0 && wRr.isBlack() == here.isBlack() && isClear(here.getCord(), wRr.getCord())) { // right rook didn't move
            //lesbian.
            // I need to check that cords
        }

        return lesbian;
    }

    //////////////////////////////////////////HELPING HAVERIKO/////////////////////////////////////////////

    private void board_reset() {
        for (int i = 0; i < 8; i++) { // unleash all the pawn
            black[i].setCor(i, 6);
            board[i][6] = black[i];
            white[i].setCor(i, 1);
            board[i][1] = white[i];
        }
        /////////////////////////////BLACKED/////////////////////////////
        {
            //knights
            black[8].setCor(1, 7);
            setBoard(1, 7, black[8]);
            black[9].setCor(6, 7);
            setBoard(6, 7, black[9]);
            //bishops
            black[10].setCor(2, 7);
            setBoard(2, 7, black[10]);
            black[11].setCor(5, 7);
            setBoard(5, 7, black[11]);
            //rooks
            black[12].setCor(0, 7);
            setBoard(0, 7, black[12]);
            black[13].setCor(7, 7);
            setBoard(7, 7, black[13]);
            //queen
            black[14].setCor(3, 7);
            setBoard(3, 7, black[14]);

            //king
            black[15].setCor(4, 7);
            setBoard(4, 7, black[15]);
        }
        //////////////////////////////WHITE!////////////////////////////////
        {
            //knights
            white[8].setCor(1, 0);
            setBoard(1, 0, white[8]);
            white[9].setCor(6, 0);
            setBoard(6, 0, white[9]);

            //bishops
            white[10].setCor(2, 0);
            setBoard(2, 0, white[10]);
            white[11].setCor(5, 0);
            setBoard(5, 0, white[11]);

            //rooks
            white[12].setCor(0, 0);
            setBoard(0, 0, white[12]);
            white[13].setCor(7, 0);
            setBoard(7, 0, white[13]);

            //queen
            white[14].setCor(3, 0);
            setBoard(3, 0, white[14]);

            //king
            white[15].setCor(4, 0);
            setBoard(4, 0, white[15]);
        }
    }

    private void arr_reset() {
        int i = 0;
        for (; i < 8; i++) {                    // fill with pawns
            black[i] = new Piece(pawn, true);
            white[i] = new Piece(pawn, false);
        }
        for (; i < 10; i++) {                   //knight 8 9
            black[i] = new Piece(knight, true);
            white[i] = new Piece(knight, false);
        }
        for (; i < 12; i++) {                   //bish 10 11
            black[i] = new Piece(bish, true);
            white[i] = new Piece(bish, false);
        }
        for (; i < 14; i++) {                   // rook 12 13
            black[i] = new Piece(rook, true);
            white[i] = new Piece(rook, false);
        }
        // king queen
        black[14] = new Piece(queen, true);
        black[15] = new Piece(king, true);

        white[14] = new Piece(queen, false);
        white[15] = new Piece(king, false);
    }

    private boolean isClear(cord king, cord rook) { //it's for the kings moves
        int to = 1;
        if (rook.getX() < king.getX()) {
            to = -1;
        }
        for (int i = king.getX() + to; i < rook.getX(); i += to) {
            if (extract(new cord(i, king.getY())) == null) {
                continue;
            }
            return false;
        }
        return true;
    } //it's for the king moves

    private boolean inBoard(cord dest) { // hope it works
        return dest.getX() <= 7 && dest.getX() >= 0 && dest.getY() <= 7 && dest.getY() >= 0;
    }

    private void setBoard(int x, int y, Piece I) {
        board[x][y] = I;
    }

    private Piece getBoard(int x, int y) {
        return board[x][y];
    }

    private boolean same_color(cord cur, cord dest) throws Exception {  //returns if the cur and dest have the same color
        if (!inBoard(cur) || !inBoard(dest)) {
            throw new Exception("out of bounds");
        }
        Piece cury = extract(cur);
        Piece desty = extract(dest);
        if(cury == null){
            System.out.println("your mom is gay");
        }
        return cury.isBlack() == desty.isBlack();
    }

//    private boolean is_there(cord dest) {               // check if the place is nullnot sure why it helps
//        return board[dest.getX()][dest.getY()] != null;
//    }
//
//    private boolean isdiag(cord cur, cord dest) {
//        return Math.abs(cur.getX() - dest.getX()) == Math.abs(cur.getY() - dest.getY());
//    }

    private Piece extract(cord here) {
        if (!inBoard(here)) {
            return null;
        }
        return board[here.getX()][here.getY()];
    } // gets the Piece in this cord


    private int deltaX(cord cur, cord dest) {
        return Math.abs(cur.getX() - dest.getX());
    } // I don't really need this
}
