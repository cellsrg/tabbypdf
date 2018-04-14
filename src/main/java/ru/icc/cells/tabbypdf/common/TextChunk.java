package ru.icc.cells.tabbypdf.common;

import lombok.Data;

/**
 * Rectangular area containing text and font characteristics
 */
@Data
public class TextChunk extends RectangularTextContainer implements Comparable<TextChunk> {
    public static final String DIAGNOSTICS_FORMAT = "Text (@%f, %f -> %f, %f): %s";
    /**
     * the text of the chunk
     */
    private String text;
    /**
     * the size of the font of the chunk text
     */
    private float fontSize;

    private FontCharacteristics fontCharacteristics;

    public TextChunk(String string, double left, double bottom, double right, double top,
                     FontCharacteristics fontCharacteristics
    ) {
        super(left, bottom, right, top);
        this.text = string;
        this.fontCharacteristics = fontCharacteristics;
        this.fontSize = fontCharacteristics.getSize();
    }

    /**
     * Used for debugging
     */
    public void printDiagnostics() {
        System.out.println(String.format(DIAGNOSTICS_FORMAT, getLeft(), getBottom(), getRight(), getBottom(), text));
    }

    /**
     * Checks whether this chunk is at the same horizontal line with other chunk
     *
     * @param as other chunk
     */
    public boolean sameLine(TextChunk as) {
        return this.getTop() == as.getTop() || this.getBottom() == as.getBottom();
    }


    /**
     * @param other other chunk
     * @return distance from end of other chunk
     */
    public double distanceFromEndOf(TextChunk other) {
        return getLeft() - other.getRight();
    }

    @Override
    public int compareTo(TextChunk rhs) {
        if (this == rhs || this.equals(rhs)) {
            return 0;
        }

        if (getBottom() == rhs.getBottom()) {
            if (getLeft() < rhs.getLeft()) {
                return -1;
            } else if (getLeft() > rhs.getLeft()) {
                return 1;
            }
            return 0;
        } else if (getBottom() < rhs.getBottom()) {
            return -1;
        }
        return 1;
    }
}
