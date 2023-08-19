public class cord {
    private int x, y;
    public cord(int x, int y){
        this.x = x;
        this.y = y;
    }
    public cord(cord shalom){
        x = shalom.getX();
        y = shalom.getY();
    }
    public int getX() {
        return x;
    }
    public boolean equals(cord hello){
        return x == hello.getX() && y == hello.getY();
    }

    public int getY() {
        return y;
    }

    public void set(int x,int y) {
        this.x = x;
        this.y = y;
    }
    public void add(cord hey){
        this.x += hey.getX();
        this.y += hey.getY();
    }
    public void print(){
        System.out.println(x+"|"+y);
    }
}
