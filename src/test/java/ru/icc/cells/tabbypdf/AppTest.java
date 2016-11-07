package ru.icc.cells.tabbypdf;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.pdfbox.pdmodel.PDDocument;
import ru.icc.cells.tabbypdf.common.Page;
import ru.icc.cells.tabbypdf.common.TextChunk;
import ru.icc.cells.tabbypdf.debug.visual.PdfBoxWriter;
import ru.icc.cells.tabbypdf.utils.content.PdfContentExtractor;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class AppTest extends TestCase {
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

    public AppTest(String testName) {
        super(testName);
    }

    public static junit.framework.Test suite() {
        return new TestSuite(AppTest.class);
    }

    public void testStrategyReturnsNotEmptyLocationalResultList() throws IOException {
        for (int i = 0; i < TEST_PDF_PATHS.length; i++) {
            String testPdfPath = TEST_PDF_PATHS[i];
            System.out.println(i + ") -------------------------------------------------------------------------------");
            PdfContentExtractor pdfContentExtractor = new PdfContentExtractor(testPdfPath);
            List<TextChunk>     chunks              = pdfContentExtractor.getWordChunks(1);
            chunks.forEach(textChunk -> System.out.println(textChunk.getText()));
            assertFalse(chunks.isEmpty());
        }
    }

    public void testPdfBoxWriterWorksCorrectly() throws IOException {
        for (int pdfNumber = 0; pdfNumber < TEST_PDF_PATHS.length; pdfNumber++) {
            PDDocument          document  = PDDocument.load(new File(TEST_PDF_PATHS[pdfNumber]));
            PdfBoxWriter        writer    = new PdfBoxWriter(document);
            PdfContentExtractor extractor = new PdfContentExtractor(TEST_PDF_PATHS[pdfNumber]);

            for (int pageNumber = 0; pageNumber < document.getNumberOfPages(); pageNumber++) {
                Page page = extractor.getPageContent(pageNumber + 1);
                writer.setPage(pageNumber);
                writer.setColor(Color.BLUE);
                page.getRulings().forEach(writer::drawRuling);
                writer.setColor(Color.ORANGE);
                page.getOriginChunks().forEach(writer::drawRect);
                writer.close();
            }

            document.save(SAVE_TEST_PDF_PATHS[pdfNumber]);
            document.close();
        }
    }
}
