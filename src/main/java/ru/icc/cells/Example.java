package ru.icc.cells;

import org.apache.pdfbox.pdmodel.PDDocument;
import ru.icc.cells.common.Page;
import ru.icc.cells.common.TableRegion;
import ru.icc.cells.common.TextBlock;
import ru.icc.cells.common.TextLine;
import ru.icc.cells.debug.visual.PdfBoxWriter;
import ru.icc.cells.detectors.TableRegionDetector;
import ru.icc.cells.detectors.TextLineDetector;
import ru.icc.cells.utils.TextChunkProcessor;
import ru.icc.cells.utils.content.PageLayoutAlgorithm;
import ru.icc.cells.utils.content.PdfContentExtractor;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Андрей on 23.09.2016.
 */
public class Example {
    public static final String TEST_PDF_DIR = "src/test/resources/pdf/";
    public static final String SAVE_PDF_DIR = "src/test/resources/pdf/edit/";

    public static void main(String[] args) throws IOException {
        File folder = new File(TEST_PDF_DIR);
        for (File file : folder.listFiles(File::isFile)) {
            System.out.println(file.getName());
            String path = file.getAbsolutePath();
            try {
                PdfContentExtractor extractor = new PdfContentExtractor(path);
                PDDocument          document  = PDDocument.load(new File(path));
                PdfBoxWriter        writer    = new PdfBoxWriter(document);

                for (int pageNumber = 0; pageNumber < extractor.getNumberOfPages(); pageNumber++) {
                    Page page = extractor.getPageContent(pageNumber + 1);

                    TextChunkProcessor textChunkProcessor = new TextChunkProcessor(page);
                    List<TextBlock>    textBlocks         = textChunkProcessor.process();
                    List<TextBlock>    sortedTextBlocks   = new ArrayList<>(textBlocks);
                    sortedTextBlocks.sort(PageLayoutAlgorithm.RECTANGLE_COMPARATOR);

                    TextLineDetector    textLineDetector    = new TextLineDetector();
                    List<TextLine>      textLines           = textLineDetector.detect(textBlocks);
                    TableRegionDetector tableRegionDetector = new TableRegionDetector();
                    List<TableRegion>   tableRegions        = tableRegionDetector.detect(textLines);

                    writer.setColor(Color.RED);
                    tableRegions.forEach(writer::drawRect);
                }
                writer.close();
                document.save(SAVE_PDF_DIR + file.getName());
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
