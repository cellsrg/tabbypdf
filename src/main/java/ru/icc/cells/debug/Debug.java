package ru.icc.cells.debug;

import org.apache.pdfbox.pdmodel.PDDocument;
import ru.icc.cells.common.Rectangle;
import ru.icc.cells.common.Ruling;
import ru.icc.cells.debug.visual.PdfBoxWriter;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Debug
{

    private static PDDocument   doc;
    private static PdfBoxWriter writer;
    public static boolean ENABLE_DEBUG;

    private Debug()
    {
    }


    public static void setPage(int pageNumber) throws IOException
    {
        if (ENABLE_DEBUG)
        {
            if (writer!=null)
            {
                writer.setPage(pageNumber);
            }
        }
    }

    public static void handleFile(File file) throws IOException
    {
        if (ENABLE_DEBUG)
        {
            doc = PDDocument.load(file);
            writer = new PdfBoxWriter(doc);
        }
    }

    public static void setColor(Color color)
    {
        if (ENABLE_DEBUG)
        {
            if (writer != null)
            {
                writer.setColor(color);
            }
        }
    }

    public static void printText(String text, float x, float y)
    {
        if (ENABLE_DEBUG)
        {
            if (writer != null)
            {
                writer.printText(text, x, y);
            }
        }
    }

    public static <T extends Rectangle> void drawRect(T rect)
    {
        if (ENABLE_DEBUG)
        {
            if (writer != null)
            {
                writer.drawRect(rect);
            }
        }
    }

    public static <T extends Rectangle> void drawRects(List<T> rects)
    {
        if (ENABLE_DEBUG)
        {
            if (writer != null)
            {
                writer.drawRects(rects);
            }
        }
    }

    public static void drawRuling(Ruling ruling)
    {
        if (ENABLE_DEBUG)
        {
            if (writer != null)
            {
                writer.drawRuling(ruling);
            }
        }
    }

    public static void close(String savePath) throws IOException
    {
        if (ENABLE_DEBUG)
        {
            if (writer != null)
            {
                writer.close();
                doc.save(savePath);
                doc.close();
            }
        }
    }

    public static void print(String s)
    {
        if (ENABLE_DEBUG)
        {
            System.out.print(s);
        }
    }

    public static void println(String s)
    {
        if (ENABLE_DEBUG)
        {
            System.out.println(s);
        }
    }

    public static void println()
    {
        println("");
    }
}
