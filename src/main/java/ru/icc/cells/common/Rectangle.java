package ru.icc.cells.common;

/**
 * Created by Андрей on 23.09.2016.
 */
public class Rectangle {
    private float left, bottom, right, top;

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

    protected <T extends Rectangle> void join(T other) {
        left = Float.min(this.left, other.getLeft());
        bottom = Float.min(this.bottom, other.getBottom());
        right = Float.max(this.right, other.getRight());
        top = Float.max(this.top, other.getTop());
    }

    public <T extends Rectangle> boolean contains(T other) {
        boolean left   = this.left <= other.getLeft();
        boolean right  = this.right >= other.getRight();
        boolean top    = this.top >= other.getTop();
        boolean bottom = this.bottom <= other.getBottom();

        return left && right && top && bottom;
    }
}
