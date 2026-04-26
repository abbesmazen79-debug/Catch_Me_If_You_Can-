package map;

public class Tile {

    public enum Type { FLOOR, WALL }

    private Type type;

    public Tile(Type type) { this.type = type; }

    public boolean isWall()  { return type == Type.WALL; }
    public Type    getType() { return type; }
}