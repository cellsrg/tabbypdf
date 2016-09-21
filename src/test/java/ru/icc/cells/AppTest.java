package ru.icc.cells;

import com.itextpdf.text.pdf.PdfReader;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ru.icc.cells.common.TextChunk;
import ru.icc.cells.utils.PdfContentExtractor;

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
            PdfReader                       reader       = new PdfReader(testPdfPath);
            PdfContentExtractor pdfContentExtractor = new PdfContentExtractor(reader);
            List<TextChunk>                 chunks       = pdfContentExtractor.getWordChunks(1);
            chunks.forEach(textChunk -> System.out.println(textChunk.getText()));
            assertFalse(chunks.isEmpty());
        }
    }
}