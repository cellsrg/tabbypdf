package ru.cells.icc;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ru.cells.icc.utils.MikhailovTextExtractionStrategy;

import java.io.IOException;

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
            PdfReaderContentParser          parser       = new PdfReaderContentParser(reader);
            int                             pageRotation = reader.getPageRotation(1);
            float                           pageWidth    = reader.getPageSize(1).getWidth();
            MikhailovTextExtractionStrategy strategy     = new MikhailovTextExtractionStrategy(pageRotation, pageWidth);

            parser.processContent(1, strategy);
            strategy.getResultantWordLocation((MikhailovTextExtractionStrategy.TextChunkFilter) null)
                    .forEach(textChunk -> System.out.println(textChunk.getText()));
            assertFalse(strategy.getLocationalResult().isEmpty());
        }
    }
}
