package ru.icc.cells.tabbypdf.common;

import java.awt.geom.Rectangle2D;

/**
 * Simple rectangular bounding, represented with four coordinates:
 * left, right x-coordinates and top, bottom y-coordinates
 */
public class Rectangle
{
    private float left, bottom, right, top;

    /**
     * Creates rectangle with zero width and height at (0,0)
     */
    public Rectangle()
    {
        this(0, 0, 0, 0);
    }

    public Rectangle(float left, float bottom, float right, float top)
    {
        this.left = left;
        this.bottom = bottom;
        this.right = right;
        this.top = top;
    }

    public float getLeft()
    {
        return left;
    }

    public float getBottom()
    {
        return bottom;
    }

    public float getRight()
    {
        return right;
    }

    public float getTop()
    {
        return top;
    }

    public void setLeft(float left)
    {
        this.left = left;
    }

    public void setBottom(float bottom)
    {
        this.bottom = bottom;
    }

    public void setRight(float right)
    {
        this.right = right;
    }

    public void setTop(float top)
    {
        this.top = top;
    }

    /**
     * Joins this rectangle with another rectangle. Result keeps in this rectangle
     */
    protected <T extends Rectangle> void join(T other)
    {
        left = Float.min(this.left, other.getLeft());
        bottom = Float.min(this.bottom, other.getBottom());
        right = Float.max(this.right, other.getRight());
        top = Float.max(this.top, other.getTop());
    }

    /**
     * Checks whether this rectangle contains other rectangle
     */
    public <T extends Rectangle> boolean contains(T other)
    {
        Rectangle2D.Float thisRect = new Rectangle2D.Float(left, bottom, right - left, top - bottom);
        Rectangle2D.Float otherRect =
                new Rectangle2D.Float(other.getLeft(), other.getBottom(), other.getRight() - other.getLeft(),
                                      other.getTop() - other.getBottom());
        return thisRect.contains(otherRect);
    }

    public <T extends Rectangle> boolean intersects(T other)
    {
        Rectangle2D.Float thisRect = new Rectangle2D.Float(left, bottom, right - left, top - bottom);
        Rectangle2D.Float otherRect =
                new Rectangle2D.Float(other.getLeft(), other.getBottom(),
                                      other.getRight() - other.getLeft(), other.getTop() - other.getBottom());
        return thisRect.intersects(otherRect);
    }

    public void rotate(int rotation, float pageHeight, float pageWidth) {
        switch (rotation) {
            case 270:
                rotate(pageHeight);
                // fall through
            case 180:
                rotate(pageWidth);
                // fall through
            case 90:
                rotate(pageHeight);
                // fall through
            case 0:
                break;
            default:
                throw new IllegalArgumentException("Rotation should be 0, 90, 180 or 270");
        }
    }

    private void rotate(float height) {
        float old = bottom;
        bottom = left;
        left = height - old;

        old = right;
        right = height - top;
        top = old;

    }
}
