package ru.icc.cells.common;

import com.itextpdf.awt.geom.Point2D;

public class Ruling {
    private final Point2D startLocation;
    private final Point2D endLocation;

    public Ruling(Point2D startLocation, Point2D endLocation) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }

    public Ruling(float x1, float y1, float x2, float y2) {
        this(new Point2D.Float(x1, y1), new Point2D.Float(x2, y2));
    }

    public Point2D getStartLocation() {
        return startLocation;
    }

    public Point2D getEndLocation() {
        return endLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ruling ruling = (Ruling) o;

        if (startLocation != null ? !startLocation.equals(ruling.startLocation) : ruling.startLocation != null)
            return false;
        return endLocation != null ? endLocation.equals(ruling.endLocation) : ruling.endLocation == null;
    }
}
