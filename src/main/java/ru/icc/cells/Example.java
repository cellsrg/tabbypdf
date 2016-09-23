package ru.icc.cells;

import org.apache.pdfbox.pdmodel.PDDocument;
import ru.icc.cells.common.Page;
import ru.icc.cells.common.Ruling;
import ru.icc.cells.common.TextChunk;
import ru.icc.cells.debug.visual.PdfBoxWriter;
import ru.icc.cells.utils.PdfContentExtractor;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Андрей on 23.09.2016.
 */
public class Example {
    public static void main(String[] args) {
        String              path      = "src/test/resources/pdf/eu-009a.pdf";
        try {
            PdfContentExtractor extractor = new PdfContentExtractor(path);
            PDDocument          document  = PDDocument.load(new File(path));
            PdfBoxWriter        writer    = new PdfBoxWriter(document);
            writer.setShowChunkOrder(true);

            for (int i = 0; i < extractor.getNumberOfPages(); i++) {
                Page page = extractor.getPageContent(i + 1);
                writer.setPage(i);
                writer.setColor(Color.BLUE);
                for (Ruling ruling : page.getRulings()) {
                    writer.drawRuling(ruling);
                }
                writer.setColor(Color.ORANGE);
                for (TextChunk chunk : page.getOriginChunks()) {
                    writer.drawChunk(chunk);
                }
            }
            writer.close();
            document.save(path + ".changed.pdf");
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
