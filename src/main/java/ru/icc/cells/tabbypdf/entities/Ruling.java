package ru.icc.cells.tabbypdf.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.awt.geom.Point2D;

/**
 * Simple ruling, represented by 2 points: start and end locations
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Ruling {
    private final Point2D startLocation;
    private final Point2D endLocation;

    public Ruling(double x1, double y1, double x2, double y2) {
        this(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2));
    }
}
