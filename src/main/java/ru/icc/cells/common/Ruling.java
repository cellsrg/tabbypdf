package ru.icc.cells.common;

import com.itextpdf.awt.geom.Point2D;

public class Ruling {
    private final Point2D startLocation;
    private final Point2D  endLocation;

    public Ruling(Point2D startLocation, Point2D endLocation) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }

    public Point2D getStartLocation() {
        return startLocation;
    }

    public Point2D getEndLocation() {
        return endLocation;
    }
}
