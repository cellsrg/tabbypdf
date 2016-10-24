package ru.icc.cells;

import ru.icc.cells.common.Page;
import ru.icc.cells.common.TableBox;
import ru.icc.cells.common.TextBlock;
import ru.icc.cells.detectors.TableDetector;
import ru.icc.cells.utils.content.PdfContentExtractor;
import ru.icc.cells.utils.processing.TextChunkProcessor;
import ru.icc.cells.utils.processing.TextChunkProcessorConfiguration;
import ru.icc.cells.utils.processing.filter.Heuristic;
import ru.icc.cells.utils.processing.filter.bi.*;
import ru.icc.cells.utils.processing.filter.tri.CutInAfterTriHeuristic;
import ru.icc.cells.utils.processing.filter.tri.CutInBeforeTriHeuristic;
import ru.icc.cells.writers.TableBoxToXmlWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Example {
    public static final String TEST_PDF_DIR = "src/test/resources/pdf/";
    public static final String SAVE_PDF_DIR = "src/test/resources/pdf/edit/";

    public static void main(String[] args) throws IOException, TransformerException, ParserConfigurationException {
        File folder = new File(TEST_PDF_DIR);
        for (File file : folder.listFiles(File::isFile)) {
            TableBoxToXmlWriter writer = new TableBoxToXmlWriter();
            writer.write(findTables(file), file.getName(),
                         SAVE_PDF_DIR + "xml/" + file.getName().substring(0, file.getName().lastIndexOf('.')) +
                         "-reg-output.xml");
        }
        //findTables(new File("src/test/resources/pdf/Aeroflot_FS_2006.10.pdf"));
    }

    private static TextChunkProcessorConfiguration getConfiguration() {
        return new TextChunkProcessorConfiguration()
                            /*VERTICAL FILTERS*/.addFilter(new HorizontalPositionBiHeuristic())
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

    private static List<TableBox> findTables(File file) {
        System.out.println(file.getName());
        List<TableBox> tableBoxes = new ArrayList<>();
        try {
            PdfContentExtractor extractor = new PdfContentExtractor(file.getAbsolutePath());
            for (int pageNumber = 0; pageNumber < extractor.getNumberOfPages(); pageNumber++) {
                Page page = extractor.getPageContent(pageNumber);
                TextChunkProcessorConfiguration configuration =
                        getConfiguration().addFilter(new LinesBetweenChunksBiHeuristic(page.getRulings()));
                List<TextBlock> textBlocks     = new TextChunkProcessor(page, configuration).process();
                TableDetector   tableDetector  = new TableDetector(null);
                List<TableBox>  pageTableBoxes = tableDetector.detect(textBlocks);
                for (TableBox tableBox : pageTableBoxes) {
                    tableBox.setPageNumber(pageNumber + 1);
                }
                tableBoxes.addAll(pageTableBoxes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tableBoxes;
    }
}
