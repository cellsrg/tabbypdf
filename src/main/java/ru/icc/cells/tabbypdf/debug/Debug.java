package ru.icc.cells.tabbypdf.debug;

import org.apache.pdfbox.pdmodel.PDDocument;
import ru.icc.cells.tabbypdf.common.Rectangle;
import ru.icc.cells.tabbypdf.common.Ruling;
import ru.icc.cells.tabbypdf.debug.visual.PdfBoxWriter;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Debug {

    private static PDDocument   doc;
    private static PdfBoxWriter writer;
    public  static boolean      ENABLE_DEBUG;


    public static void setPage(int pageNumber) {
        if (!ENABLE_DEBUG || writer == null) {
            return;
        }
        writer.setPage(pageNumber);
    }

    public static void handleFile(File file) {
        if (!ENABLE_DEBUG) {
            return;
        }
        try {
            doc = PDDocument.load(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writer = new PdfBoxWriter(doc);
    }

    public static void setColor(Color color) {
        if (!ENABLE_DEBUG || writer == null) {
            return;
        }
        writer.setColor(color);
    }

    public static void printText(String text, float x, float y) {
        if (!ENABLE_DEBUG || writer == null) {
            return;
        }
        writer.printText(text, x, y);
    }

    public static <T extends Rectangle> void drawRect(T rect) {
        if (!ENABLE_DEBUG || writer == null) {
            return;
        }
        writer.drawRect(rect);
    }

    public static <T extends Rectangle> void drawRects(List<T> rects) {
        if (!ENABLE_DEBUG || writer == null) {
            return;
        }
        writer.drawRects(rects);
    }

    public static void drawRuling(Ruling ruling) {
        if (!ENABLE_DEBUG || writer == null) {
            return;
        }
        writer.drawRuling(ruling);
    }

    public static void close(String savePath) {
        if (!ENABLE_DEBUG || writer == null) {
            return;
        }
        writer.close();
        try {
            doc.save(savePath);
            doc.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void print(String s) {
        if (ENABLE_DEBUG) {
            System.out.print(s);
        }
    }

    public static void println(String s) {
        if (ENABLE_DEBUG) {
            System.out.println(s);
        }
    }

    public static void println() {
        println("");
    }
}
