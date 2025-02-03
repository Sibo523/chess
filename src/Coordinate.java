// Coordinate.java
public class Coordinate {
    private int x, y;
    
    public Coordinate(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    // Copy constructor
    public Coordinate(Coordinate other){
        this.x = other.x;
        this.y = other.y;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Coordinate)) return false;
        Coordinate that = (Coordinate) o;
        return this.x == that.x && this.y == that.y;
    }
    
    @Override
    public int hashCode(){
        return 31 * x + y;
    }
    
    @Override
    public String toString(){
        return "(" + x + "," + y + ")";
    }
}
