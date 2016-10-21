package ru.icc.cells.common;

/**
 * Simple rectangular bounding, represented with four coordinates:
 * left, right x-coordinates and top, bottom y-coordinates
 */
public class Rectangle {
    private float left, bottom, right, top;

    /**
     * Creates rectangle with zero width and height at (0,0)
     */
    public Rectangle() {
        this(0, 0, 0, 0);
    }

    public Rectangle(float left, float bottom, float right, float top) {
        this.left = left;
        this.bottom = bottom;
        this.right = right;
        this.top = top;
    }

    public float getLeft() {
        return left;
    }

    public float getBottom() {
        return bottom;
    }

    public float getRight() {
        return right;
    }

    public float getTop() {
        return top;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public void setTop(float top) {
        this.top = top;
    }

    /**
     * Joins this rectangle with another rectangle. Result keeps in this rectangle
     */
    protected <T extends Rectangle> void join(T other) {
        left = Float.min(this.left, other.getLeft());
        bottom = Float.min(this.bottom, other.getBottom());
        right = Float.max(this.right, other.getRight());
        top = Float.max(this.top, other.getTop());
    }

    /**
     * Checks whether this rectangle contains other rectangle
     */
    public <T extends Rectangle> boolean contains(T other) {
        boolean left = this.left <= other.getRight();
        boolean right = this.right >= other.getLeft();
        boolean top = this.top >= other.getBottom();
        boolean bottom = this.bottom <= other.getTop();
        return left && right && top && bottom;
    }

    public <T extends Rectangle> boolean intersects(T other){
        boolean left = this.left <= other.getRight();
        boolean right = this.right >= other.getLeft();
        boolean top = this.top >= other.getBottom();
        boolean bottom = this.bottom <= other.getTop();
        return (left && (top || bottom)) || (right && (top || bottom));
    }
}
