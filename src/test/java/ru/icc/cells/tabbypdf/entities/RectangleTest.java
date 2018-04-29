package ru.icc.cells.tabbypdf.entities;

import org.junit.Test;

import static org.junit.Assert.*;

public class RectangleTest {

    @Test
    public void join() {
        double firstLeft   = 10;
        double firstBottom = 10;
        double firstRight  = 20;
        double firstTop    = 20;

        double secondLeft   = 15;
        double secondBottom = 15;
        double secondRight  = 25;
        double secondTop    = 25;

        Rectangle first  = new Rectangle(firstLeft, firstBottom, firstRight, firstTop);
        Rectangle second = new Rectangle(secondLeft, secondBottom, secondRight, secondTop);

        first.join(second);

        assertEquals(first.getLeft(), firstLeft, 0.0);
        assertEquals(first.getBottom(), firstBottom, 0.0);
        assertEquals(first.getRight(), secondRight, 0.0);
        assertEquals(first.getTop(), secondTop, 0.0);
    }

    @Test
    public void contains() {
        double firstLeft   = 10;
        double firstBottom = 10;
        double firstRight  = 30;
        double firstTop    = 30;

        double secondLeft   = 15;
        double secondBottom = 15;
        double secondRight  = 25;
        double secondTop    = 25;

        Rectangle first  = new Rectangle(firstLeft, firstBottom, firstRight, firstTop);
        Rectangle second = new Rectangle(secondLeft, secondBottom, secondRight, secondTop);

        assertTrue(first.contains(second));
        assertFalse(second.contains(first));
    }

    @Test
    public void intersects() {
        double firstLeft   = 10;
        double firstBottom = 10;
        double firstRight  = 20;
        double firstTop    = 20;

        double secondLeft   = 15;
        double secondBottom = 15;
        double secondRight  = 25;
        double secondTop    = 25;

        double thirdLeft   = 30;
        double thirdBottom = 10;
        double thirdRight  = 50;
        double thirdTop    = 20;

        Rectangle first  = new Rectangle(firstLeft, firstBottom, firstRight, firstTop);
        Rectangle second = new Rectangle(secondLeft, secondBottom, secondRight, secondTop);
        Rectangle third  = new Rectangle(thirdLeft, thirdBottom, thirdRight, thirdTop);

        assertTrue(first.intersects(second));
        assertFalse(first.intersects(third));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rotate() {
        double left            = 10;
        double bottom          = 10;
        double right           = 20;
        double top             = 20;
        double pageWidthHeight = 100;

        Rectangle rectangle = new Rectangle(left, bottom, right, top);

        rectangle.rotate(0, pageWidthHeight, pageWidthHeight);

        assertEquals(rectangle.getLeft(), left, 0.0);
        assertEquals(rectangle.getRight(), right, 0.0);
        assertEquals(rectangle.getBottom(), bottom, 0.0);
        assertEquals(rectangle.getTop(), top, 0.0);

        rectangle.rotate(90, pageWidthHeight, pageWidthHeight);
        assertEquals(rectangle.getLeft(), pageWidthHeight - bottom, 0.0);
        assertEquals(rectangle.getBottom(), left, 0.0);
        assertEquals(rectangle.getRight(), pageWidthHeight - top, 0.0);
        assertEquals(rectangle.getTop(), right, 0.0);

        // throws an exception
        rectangle.rotate(360, pageWidthHeight, pageWidthHeight);
    }

    @Test
    public void fromRuling() {
        Ruling    ruling    = new Ruling(30, 30, 20, 20);
        Rectangle rectangle = Rectangle.fromRuling(ruling);

        assertEquals(rectangle.getLeft(), ruling.getEndLocation().getX(), 0.0);
        assertEquals(rectangle.getBottom(), ruling.getEndLocation().getY(), 0.0);
        assertEquals(rectangle.getRight(), ruling.getStartLocation().getX(), 0.0);
        assertEquals(rectangle.getTop(), ruling.getStartLocation().getY(), 0.0);
    }
}