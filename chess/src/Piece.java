import java.util.Scanner;

public class Piece {
    private int key; // they stay the same key and the same color
    private cord cor;
    private int worth;
    private  boolean black;
    private int w_move = 0;
    private int counter = 0;
    static Scanner s = new Scanner(System.in);
    public Piece(int key, boolean black){
        this.key = key;
        this.black = black;

        switch (key) {
            case 1 -> worth = 1;
            case 2 -> worth = 5;
            case 3, 4 -> worth = 3;
            case 5 -> worth = 9;
            case 6 -> worth = 100; // fucking king
        }
    }
    public Piece(int key, boolean black,cord cor){
        this.key = key;
        this.black = black;
        this.cor = new cord(cor);
        switch (key) {
            case 1 -> worth = 1;
            case 2 -> worth = 5;
            case 3, 4 -> worth = 3;
            case 5 -> worth = 9;
            case 6 -> worth = 100; // fucking king
        }
    }
    public Piece(Piece pie){ // make a new piece for existing piece that is exactly the same
        this.key = pie.getKey();
        this.black = pie.isBlack();
        this.cor = new cord(pie.cor);
        this.worth = pie.get_worth();
        this.w_move = pie.when();
        this.counter = pie.getCounter();

    }
    public String name(){
        if(key == 1 ){return "Pawn";}
        if(key == 2){ return "Rook";}
        if(key == 3){ return "Knigh";}
        if(key == 4){ return "Bish";}
        if(key == 5){ return "Queen";}
        if(key == 6){ return "King";}
        return null;
    }
    public void setCor(cord cor){
        this.cor = new cord(cor);
    }
    public void setCor(int x, int y){
        this.cor = new cord(x,y);
    }
    public cord getCord(){
        return cor;
    }

    public int getKey() {
        return key;
    }

    public boolean isBlack() {
        return black;
    }
    public void promote(){
        if(key == 1 ){
            while(key < 2 || key >6) {
                System.out.println("what do you want to promote to, type an int 2 to 6");
                System.out.println("2 = rook, 3 = knight, 4 = bishop, 5 = queen, 6 = king");
                key = s.nextInt(); // input for the promotion
            } // if he thinks he is funny or something and types something else then fuck him we keep getting in
        }
    }
    public void setmove(int num){
        this.w_move = num;
    }
    public void count(){
        counter+=1;
    }
    public int getCounter(){
        return counter;
    }
    public int when(){
        return w_move;
    }

    public int get_worth(){
        return worth;
    }
}
