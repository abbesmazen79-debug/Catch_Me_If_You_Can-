package data.entity;

import java.awt.*;

public abstract class Entity {

    protected float x, y;
    protected int   size;
    protected float speed;

    public Entity(float x, float y, int size, float speed) {
        this.x     = x;
        this.y     = y;
        this.size  = size;
        this.speed = speed;
    }

    public abstract void update();
    public abstract void render(Graphics2D g);

    /** Axis-aligned bounding-box for collision checks */
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, size, size);
    }

    public float getCenterX() { return x + size / 2f; }
    public float getCenterY() { return y + size / 2f; }

    public float getX()    { return x; }
    public float getY()    { return y; }
    public int   getSize() { return size; }

    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
}