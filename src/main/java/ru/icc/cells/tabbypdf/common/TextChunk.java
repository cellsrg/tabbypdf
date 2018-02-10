package ru.icc.cells.tabbypdf.common;

import com.itextpdf.text.pdf.DocumentFont;

/**
 * Rectangular area containing text and font characteristics
 */
public class TextChunk extends RectangularTextContainer implements Comparable<TextChunk>
{
    /**
     * the text of the chunk
     */
    private       String       text;
    /**
     * the width of a single space character in the font of the chunk
     */
    private final float        charSpaceWidth;
    /**
     * the font of the chunk text
     */
    private       DocumentFont font;
    /**
     * the size of the font of the chunk text
     */
    private float fontSize;

    private FontCharacteristics fontCharacteristics;

    public TextChunk(
            String string, float left, float bottom, float right, float top, FontCharacteristics fontCharacteristics
    ) {
        super(left, bottom, right, top);
        this.text = string;
        this.charSpaceWidth = fontCharacteristics.getSpaceWidth();
        this.fontCharacteristics = fontCharacteristics;
        this.fontSize = fontCharacteristics.getSize();
    }

    public FontCharacteristics getFontCharacteristics() {
        return fontCharacteristics;
    }

    public void setChunkFont(DocumentFont font)
    {
        this.font = font;
    }

    public DocumentFont getChunkFont()
    {
        return this.font;
    }

    public float getFontSize()
    {
        return fontSize;
    }

    public void setFontSize(float fontSize)
    {
        this.fontSize = fontSize;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    @Override
    public String getText()
    {
        return text;
    }

    public float getCharSpaceWidth()
    {
        return charSpaceWidth;
    }

    /**
     * Used for debugging
     */
    public void printDiagnostics()
    {
        System.out.println(
                "Text (@" + getLeft() + "," + getBottom() + " -> " + getRight() + "," + getBottom() + "): " + text);
    }

    /**
     * Checks whether this chunk is at the same horizontal line with other chunk
     * @param as other chunk
     */
    public boolean sameLine(TextChunk as)
    {
        return this.getTop() == as.getTop() || this.getBottom() == as.getBottom();
    }


    /**
     * @param other other chunk
     * @return distance from end of other chunk
     */
    public float distanceFromEndOf(TextChunk other)
    {
        return getLeft() - other.getRight();
    }

    @Override
    public int compareTo(TextChunk rhs)
    {
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
