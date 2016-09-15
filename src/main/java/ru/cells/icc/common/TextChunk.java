package ru.cells.icc.common;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.pdf.DocumentFont;
import com.itextpdf.text.pdf.parser.Vector;

/**
 * Created by sunveil on 27/06/16.
 */
public class TextChunk extends Chunk implements Comparable<TextChunk> {
    /**
     * the text of the chunk
     */
    private       String       text;
    /**
     * the starting location of the chunk
     */
    private final Vector       startLocation;
    /**
     * the ending location of the chunk
     */
    private final Vector       endLocation;
    /**
     * unit vector in the orientation of the chunk
     */
    private final Vector       orientationVector;
    /**
     * the orientation as a scalar for quick sorting
     */
    private final int          orientationMagnitude;
    /**
     * perpendicular distance to the orientation unit vector (i.e. the Y position in an unrotated coordinate system)
     * we round to the nearest integer to handle the fuzziness of comparing floats
     */
    private final int          distPerpendicular;
    /**
     * distance of the start of the chunk parallel to the orientation unit vector (i.e. the X position in an unrotated coordinate system)
     */
    private final float        distParallelStart;
    /**
     * distance of the end of the chunk parallel to the orientation unit vector (i.e. the X position in an unrotated coordinate system)
     */
    private final float        distParallelEnd;
    /**
     * the width of a single space character in the font of the chunk
     */
    private final float        charSpaceWidth;
    private       Vector       rightTopPoint;
    private       int          order;
    private       DocumentFont font;

    public void setChunkFont(DocumentFont font) {
        this.font = font;
    }

    public DocumentFont getChunkFont() {
        return this.font;
    }


    public void setRightTopPoint(Vector rightTopPoint) {
        this.rightTopPoint = rightTopPoint;
    }

    public Vector getRightTopPoint() {
        return rightTopPoint;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public TextChunk(String string, Vector startLocation, Vector endLocation, float charSpaceWidth) {
        this.text = string;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.charSpaceWidth = charSpaceWidth;

        Vector oVector = endLocation.subtract(startLocation);
        if (oVector.length() == 0) {
            oVector = new Vector(1, 0, 0);
        }
        orientationVector = oVector.normalize();
        orientationMagnitude =
                (int) (Math.atan2(orientationVector.get(Vector.I2), orientationVector.get(Vector.I1)) * 1000);

        Vector origin = new Vector(0, 0, 1);
        distPerpendicular = (int) (startLocation.subtract(origin)).cross(orientationVector).get(Vector.I3);

        distParallelStart = orientationVector.dot(startLocation);
        distParallelEnd = orientationVector.dot(endLocation);
    }


    public Vector getStartLocation() {
        return startLocation;
    }

    public Vector getEndLocation() {
        return endLocation;
    }


    public String getText() {
        return text;
    }

    public float getCharSpaceWidth() {
        return charSpaceWidth;
    }

    public void printDiagnostics() {
        System.out.println("Text (@" + startLocation + " -> " + endLocation + "): " + text);
        System.out.println("orientationMagnitude: " + orientationMagnitude);
        System.out.println("distPerpendicular: " + distPerpendicular);
        System.out.println("distParallel: " + distParallelStart);
    }


    public boolean sameLine(TextChunk as) {
        if (orientationMagnitude != as.orientationMagnitude) return false;
        return distPerpendicular == as.distPerpendicular;
    }

    public boolean sameLine2(TextChunk as) {
        return getStartLocation().get(1) == as.getStartLocation().get(1);
    }

    public float distanceFromEndOf(TextChunk other) {
        return distParallelStart - other.distParallelEnd;
    }


    public int compareTo(TextChunk rhs) {
        if (this == rhs) return 0;

        int rslt;
        rslt = Integer.compare(orientationMagnitude, rhs.orientationMagnitude);
        if (rslt != 0) return rslt;

        rslt = Integer.compare(distPerpendicular, rhs.distPerpendicular);
        if (rslt != 0) return rslt;

        return Float.compare(distParallelStart, rhs.distParallelStart);
    }

    public void setText(String text) {
        this.text = text;
    }
}
