package ru.icc.cells;

import org.apache.pdfbox.pdmodel.PDDocument;
import ru.icc.cells.common.*;
import ru.icc.cells.debug.visual.PdfBoxWriter;
import ru.icc.cells.detectors.TableBoxDetector;
import ru.icc.cells.detectors.TableRegionDetector;
import ru.icc.cells.detectors.TextLineDetector;
import ru.icc.cells.utils.content.PdfContentExtractor;
import ru.icc.cells.utils.processing.TextChunkProcessor;
import ru.icc.cells.utils.processing.TextChunkProcessorConfiguration;
import ru.icc.cells.utils.processing.filter.Heuristic;
import ru.icc.cells.utils.processing.filter.bi.*;
import ru.icc.cells.utils.processing.filter.tri.CutInAfterTriHeuristic;
import ru.icc.cells.utils.processing.filter.tri.CutInBeforeTriHeuristic;

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
            processFile(file);
        }
        //        processFile(new File("src/test/resources/pdf/Aeroflot_FS_2006.10.pdf"));
    }

    private static TextChunkProcessorConfiguration getConfiguration() {
        return new TextChunkProcessorConfiguration()
                            /*VERTICAL FILTERS*/.addFilter(new HorizontalPositionBiHeuristic())
                            .addFilter(new SpaceWidthBiFilter())
                            /*HORIZONTAL FILTERS*/
                            .addFilter(new VerticalPositionBiHeuristic())
                            .addFilter(new HeightBiHeuristic())
                            .addFilter(new CutInAfterTriHeuristic())
                            .addFilter(new CutInBeforeTriHeuristic())
                            /*COMMON FILTERS*/
                            //.addFilter(new LinesBetweenChunksBiHeuristic(page.getRulings()))
                            .addFilter(new EqualFontFamilyBiHeuristic(Heuristic.Orientation.VERTICAL))
                            .addFilter(new EqualFontAttributesBiHeuristic(Heuristic.Orientation.VERTICAL))
                            .addFilter(new EqualFontSizeBiHeuristic(Heuristic.Orientation.VERTICAL))
                            /*REPLACE STRINGS*/
                            .addStringsToReplace(new String[]{"•", "", " ", "_"});
    }

    public static void processFile(File file) {
        System.out.println(file.getName());
        String path = file.getAbsolutePath();
        try {
            PdfContentExtractor extractor = new PdfContentExtractor(path);
            PDDocument          document  = PDDocument.load(new File(path));
            PdfBoxWriter        writer    = new PdfBoxWriter(document);


            for (int pageNumber = 0; pageNumber < extractor.getNumberOfPages(); pageNumber++) {
                Page page = extractor.getPageContent(pageNumber + 1);
                writer.setPage(pageNumber);
                TextChunkProcessorConfiguration configuration = getConfiguration();

                List<TextBlock>   textBlocks   = new TextChunkProcessor(page, configuration).process();
                List<TextLine>    textLines    = new TextLineDetector().detect(textBlocks);
                List<TableRegion> tableRegions = new TableRegionDetector().detect(textLines);
                List<TableBox>    tableBoxes   = new TableBoxDetector(textLines).detect(tableRegions);

                //writer.setColor(Color.BLACK);
                //page.getOriginChunks().forEach(writer::drawRect);
                writer.setColor(Color.ORANGE);
                textBlocks.forEach(writer::drawRect);

                for (TableBox tableBox : tableBoxes) {
                    writer.setColor(Color.RED);
                    writer.drawRect(tableBox);
                    for (TableRegion tableRegion : tableBox.getTableRegions()) {
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
                        writer.setColor(Color.CYAN);
                        tableRegion.getGaps().forEach(writer::drawRect);
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
