package ru.icc.cells;

import org.apache.pdfbox.pdmodel.PDDocument;
import ru.icc.cells.common.Page;
import ru.icc.cells.common.TextBlock;
import ru.icc.cells.debug.visual.PdfBoxWriter;
import ru.icc.cells.utils.TextChunkProcessor;
import ru.icc.cells.utils.content.PdfContentExtractor;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Андрей on 23.09.2016.
 */
public class Example {
    private static final String[] TEST_PDF_PATHS = {
            "src/test/resources/pdf/eu-009a.pdf",
            "src/test/resources/pdf/Japan_Agricultural_HB_2007.2.pdf",
            "src/test/resources/pdf/Japan_Agricultural_HB_2007.4.pdf",
            "src/test/resources/pdf/Japan_Agricultural_HB_2007.5.pdf",
            "src/test/resources/pdf/Japan_Science_HB_2007.5.pdf",
            "src/test/resources/pdf/us-004 2.pdf",
            "src/test/resources/pdf/us-028 3.pdf",
            "src/test/resources/pdf/us-031a 2.pdf",
            "src/test/resources/pdf/us-037.pdf",
            "src/test/resources/pdf/USDA_Tobacco_2005.12.pdf"
    };

    private static final String[] SAVE_TEST_PDF_PATHS = {
            "src/test/resources/pdf/edit/eu-009a.pdf",
            "src/test/resources/pdf/edit/Japan_Agricultural_HB_2007.2.pdf",
            "src/test/resources/pdf/edit/Japan_Agricultural_HB_2007.4.pdf",
            "src/test/resources/pdf/edit/Japan_Agricultural_HB_2007.5.pdf",
            "src/test/resources/pdf/edit/Japan_Science_HB_2007.5.pdf",
            "src/test/resources/pdf/edit/us-004 2.pdf",
            "src/test/resources/pdf/edit/us-028 3.pdf",
            "src/test/resources/pdf/edit/us-031a 2.pdf",
            "src/test/resources/pdf/edit/us-037.pdf",
            "src/test/resources/pdf/edit/USDA_Tobacco_2005.12.pdf"
    };
    public static void main(String[] args) {
        for (int i = 0; i < TEST_PDF_PATHS.length; i++) {
            String path = TEST_PDF_PATHS[i];
            try {
                PdfContentExtractor extractor = new PdfContentExtractor(path);
                PDDocument          document  = PDDocument.load(new File(path));
                PdfBoxWriter        writer    = new PdfBoxWriter(document);
                writer.setShowChunkOrder(true);

                for (int pageNumber = 0; pageNumber < extractor.getNumberOfPages(); pageNumber++) {
                    Page page = extractor.getPageContent(pageNumber + 1);

                    TextChunkProcessor textChunkProcessor = new TextChunkProcessor(page);
                    java.util.List<TextBlock> textBlocks     = textChunkProcessor.process();

                    writer.setPage(pageNumber);
                    writer.setColor(Color.BLUE);
                    page.getRulings().forEach(writer::drawRuling);
                    writer.setColor(Color.ORANGE);
                    textBlocks.forEach(writer::drawRect);
                }
                writer.close();
                document.save(SAVE_TEST_PDF_PATHS[i]);
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
