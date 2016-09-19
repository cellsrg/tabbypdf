package ru.icc.cells;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ru.icc.cells.utils.MikhailovTextExtractionStrategy;

import java.io.IOException;

public class AppTest extends TestCase {
    private static final String TEST_PDF_1_PATH = "src\\test\\resources\\pdf\\eu-009a.pdf";

    public AppTest(String testName) {
        super(testName);
    }

    public static junit.framework.Test suite() {
        return new TestSuite(AppTest.class);
    }

    public void testStrategyReturnsNotEmptyLocationalResultList() throws IOException {
        PdfReader                       reader       = new PdfReader(TEST_PDF_1_PATH);
        PdfReaderContentParser          parser       = new PdfReaderContentParser(reader);
        int                             pageRotation = reader.getPageRotation(1);
        float                           pageWidth    = reader.getPageSize(1).getWidth();
        MikhailovTextExtractionStrategy strategy     = new MikhailovTextExtractionStrategy(pageRotation, pageWidth);

        parser.processContent(1, strategy);
        strategy.getResultantWordLocation((MikhailovTextExtractionStrategy.TextChunkFilter) null)
                .forEach(textChunk -> System.out.println("\r\n------------------\r\n"+textChunk.getText()));
        assertFalse(strategy.getLocationalResult().isEmpty());
    }
}
