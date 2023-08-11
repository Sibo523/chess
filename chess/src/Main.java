import javax.swing.*;

public class Main {
    public static void main(String[] args) throws Exception {

//        System.out.println("Hello world!");
//        Screen screen = new Screen();
//        screen.createWindow();
        cord from = new cord(0,1);
        cord dest = new cord(0,3);

        cord from1 = new cord(0,0);
        cord dest1 = new cord(0,2);


        Board board = new Board();
        board.move(from,dest);
        board.move(from1,dest1);
        //board.move
        board.print();
    }

}