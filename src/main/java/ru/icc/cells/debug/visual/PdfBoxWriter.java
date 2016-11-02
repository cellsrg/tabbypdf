package ru.icc.cells.debug.visual;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import ru.icc.cells.common.Rectangle;
import ru.icc.cells.common.Ruling;

import java.awt.Color;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * A PdfBox implementation of PdfWriter
 */
public class PdfBoxWriter implements PdfWriter, Closeable
{

    private PDDocument          document;
    private PDPageContentStream contentStream;
    private int                 pageNumber;
    private Color color = Color.RED;

    /**
     * Creates pdf writer. Writer must be closed after usage
     */
    public PdfBoxWriter(PDDocument document) throws IOException
    {
        this.document = document;
        this.contentStream =
                new PDPageContentStream(document, document.getPage(pageNumber),
                                        PDPageContentStream.AppendMode.APPEND,
                                        true, true);
        contentStream.setStrokingColor(color);
    }

    /**
     * Set page for graphic output
     *
     * @param pageNumber 0-based page number
     * @throws IOException
     */
    public void setPage(int pageNumber) throws IOException
    {
        if (this.pageNumber != pageNumber)
        {
            this.pageNumber = pageNumber;
            contentStream.closeAndStroke();
            contentStream.close();
            contentStream = new PDPageContentStream(document, document.getPage(pageNumber),
                                                    PDPageContentStream.AppendMode.APPEND,
                                                    true, true);
            contentStream.setStrokingColor(color);
        }
    }

    @Override
    public void close()
    {
        try
        {
            contentStream.closeAndStroke();
            contentStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void setColor(Color color)
    {
        try
        {
            this.color = color;
            contentStream.setStrokingColor(color);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void printText(String text, float x, float y)
    {
        try
        {
            contentStream.beginText();
            contentStream.newLineAtOffset(x, y);
            contentStream.setFont(PDType1Font.HELVETICA, 8);
            contentStream.showText(text);
            contentStream.endText();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public <T extends Rectangle> void drawRect(T rect)
    {
        try
        {
            contentStream.addRect(rect.getLeft(), rect.getBottom(), Math.abs(rect.getRight() - rect.getLeft()),
                                  Math.abs(rect.getTop() - rect.getBottom()));
            contentStream.stroke();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public <T extends Rectangle> void drawRects(List<T> rects)
    {
        try
        {
            for (T rect : rects)
            {
                contentStream.addRect(rect.getLeft(), rect.getBottom(), Math.abs(rect.getRight() - rect.getLeft()),
                                      Math.abs(rect.getTop() - rect.getBottom()));
                contentStream.stroke();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void drawRuling(Ruling ruling)
    {
        try
        {
            contentStream.moveTo((float) ruling.getStartLocation().getX(), (float) ruling.getStartLocation().getY());
            contentStream.lineTo((float) ruling.getEndLocation().getX(), (float) ruling.getEndLocation().getY());
            contentStream.stroke();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
