package ru.icc.cells.common;

import com.itextpdf.text.pdf.parser.Vector;

/**
 * Created by Андрей on 23.09.2016.
 */
public class Rectangle {
    protected Vector startLocation;
    protected Vector endLocation;
    protected Vector rightTopPoint;

    public Vector getStartLocation() {
        return startLocation;
    }

    public Vector getEndLocation() {
        return endLocation;
    }

    public Vector getRightTopPoint() {
        return rightTopPoint;
    }

    public void setStartLocation(Vector startLocation) {
        this.startLocation = startLocation;
    }

    public void setEndLocation(Vector endLocation) {
        this.endLocation = endLocation;
    }

    public void setRightTopPoint(Vector rightTopPoint) {
        this.rightTopPoint = rightTopPoint;
    }

    protected <T extends Rectangle> void join(T other) {
        float left   = Float.min(this.startLocation.get(0), other.startLocation.get(0));
        float right  = Float.max(this.rightTopPoint.get(0), other.rightTopPoint.get(0));
        float top    = Float.max(this.rightTopPoint.get(1), other.rightTopPoint.get(1));
        float bottom = Float.min(this.startLocation.get(1), other.startLocation.get(1));

        this.startLocation = new Vector(left, bottom, this.startLocation.get(2));
        this.endLocation = new Vector(right, bottom, this.endLocation.get(2));
        this.rightTopPoint = new Vector(right, top, this.rightTopPoint.get(2));
    }

    public <T extends Rectangle> boolean contains(T other) {
        boolean left   = this.startLocation.get(0) <= other.startLocation.get(0);
        boolean right  = this.rightTopPoint.get(0) >= other.rightTopPoint.get(0);
        boolean top    = this.rightTopPoint.get(1) >= other.rightTopPoint.get(1);
        boolean bottom = this.startLocation.get(1) <= other.startLocation.get(1);

        return left && right && top && bottom;
    }
}
