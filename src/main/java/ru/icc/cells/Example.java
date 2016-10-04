package ru.icc.cells;

import org.apache.pdfbox.pdmodel.PDDocument;
import ru.icc.cells.common.*;
import ru.icc.cells.debug.visual.PdfBoxWriter;
import ru.icc.cells.detectors.TableBoundingDetector;
import ru.icc.cells.detectors.TableRegionDetector;
import ru.icc.cells.detectors.TextLineDetector;
import ru.icc.cells.utils.TextChunkProcessor;
import ru.icc.cells.utils.content.PdfContentExtractor;

import java.awt.*;
import java.io.File;
import java.io.IOException;
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
                    writer.setPage(pageNumber);
                    List<TextBlock>     textBlocks     = new TextChunkProcessor(page).process();
                    List<TextLine>      textLines      = new TextLineDetector().detect(textBlocks);
                    List<TableRegion>   tableRegions   = new TableRegionDetector().detect(textLines);
                    List<TableBounding> tableBoundings = new TableBoundingDetector(textLines).detect(tableRegions);

                    for (TableBounding tableBounding : tableBoundings) {
                        writer.setColor(Color.RED);
                        writer.drawRect(tableBounding);
                        for (TableRegion tableRegion : tableBounding.getTableRegions()) {
                            writer.setColor(Color.CYAN);
                            tableRegion.getGaps().forEach(writer::drawRect);
                            writer.setColor(Color.BLUE);
                            writer.drawRect(tableRegion);
                            for (TextLine textLine : tableRegion.getTextLines()) {
                                writer.setColor(Color.GREEN);
                                writer.drawRect(textLine);
                                writer.setColor(Color.ORANGE);
                                textLine.getGaps().forEach(writer::drawRect);
                                writer.setColor(Color.PINK);
                                textLine.getTextBlocks().forEach(writer::drawRect);
                            }
                        }
                    }
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
