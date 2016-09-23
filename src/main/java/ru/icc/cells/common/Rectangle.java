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
}
