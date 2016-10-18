package ru.icc.cells;

import org.apache.pdfbox.pdmodel.PDDocument;
import ru.icc.cells.common.Page;
import ru.icc.cells.common.TableBox;
import ru.icc.cells.common.TextBlock;
import ru.icc.cells.debug.visual.PdfBoxWriter;
import ru.icc.cells.detectors.TableDetector;
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

public class Example {
    public static final String TEST_PDF_DIR = "src/test/resources/pdf/";
    public static final String SAVE_PDF_DIR = "src/test/resources/pdf/edit/";

    public static void main(String[] args) throws IOException {
        File folder = new File(TEST_PDF_DIR);
        for (File file : folder.listFiles(File::isFile)) {
            processFile(file);
        }
        //processFile(new File("src/test/resources/pdf/Aeroflot_FS_2006.10.pdf"));
    }

    private static TextChunkProcessorConfiguration getConfiguration() {
        return new TextChunkProcessorConfiguration()
                            /*VERTICAL FILTERS*/
                            .addFilter(new HorizontalPositionBiHeuristic())
                            .addFilter(new SpaceWidthBiFilter().enableListCheck(true))
                            /*HORIZONTAL FILTERS*/
                            .addFilter(new VerticalPositionBiHeuristic())
                            .addFilter(new HeightBiHeuristic())
                            .addFilter(new CutInAfterTriHeuristic())
                            .addFilter(new CutInBeforeTriHeuristic())
                            /*COMMON FILTERS*/
                            .addFilter(new EqualFontFamilyBiHeuristic(Heuristic.Orientation.VERTICAL))
                            .addFilter(new EqualFontAttributesBiHeuristic(Heuristic.Orientation.VERTICAL))
                            .addFilter(new EqualFontSizeBiHeuristic(Heuristic.Orientation.VERTICAL))
                            /*REPLACE STRINGS*/
                            .addStringsToReplace(new String[]{"•", "", " ", "_", "\u0002"/**/});
    }

    private static void processFile(File file) {
        System.out.println(file.getName());
        String path = file.getAbsolutePath();
        try {
            PdfContentExtractor extractor = new PdfContentExtractor(path);
            PDDocument          document  = PDDocument.load(new File(path));
            PdfBoxWriter        writer    = new PdfBoxWriter(document);


            for (int pageNumber = 0; pageNumber < extractor.getNumberOfPages(); pageNumber++) {
                Page page = extractor.getPageContent(pageNumber + 1);
                writer.setPage(pageNumber);
                TextChunkProcessorConfiguration configuration =
                        getConfiguration().addFilter(new LinesBetweenChunksBiHeuristic(page.getRulings()));
                List<TextBlock> textBlocks = new TextChunkProcessor(page, configuration).process();
                TableDetector  tableDetector = new TableDetector(null);
                List<TableBox> tableBoxes    = tableDetector.detect(textBlocks);

                writer.setColor(Color.PINK);
                writer.drawRects(page.getOriginChunks());

                writer.setColor(Color.ORANGE);
                writer.drawRects(textBlocks);

                writer.setColor(Color.RED);
                writer.drawRects(tableBoxes);
                for (TableBox tableBox : tableBoxes) {
                    writer.setColor(Color.BLUE);
                    writer.drawRects(tableBox.getTableRegions());
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
