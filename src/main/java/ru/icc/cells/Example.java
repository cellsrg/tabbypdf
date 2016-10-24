package ru.icc.cells;

import ru.icc.cells.common.Page;
import ru.icc.cells.common.TableBox;
import ru.icc.cells.common.TextBlock;
import ru.icc.cells.common.table.Cell;
import ru.icc.cells.common.table.Row;
import ru.icc.cells.common.table.Table;
import ru.icc.cells.debug.Debug;
import ru.icc.cells.detectors.TableDetector;
import ru.icc.cells.recognizers.SimpleTableRecognizer;
import ru.icc.cells.utils.content.PdfContentExtractor;
import ru.icc.cells.utils.processing.TextChunkProcessor;
import ru.icc.cells.utils.processing.TextChunkProcessorConfiguration;
import ru.icc.cells.utils.processing.filter.Heuristic;
import ru.icc.cells.utils.processing.filter.bi.*;
import ru.icc.cells.utils.processing.filter.tri.CutInAfterTriHeuristic;
import ru.icc.cells.utils.processing.filter.tri.CutInBeforeTriHeuristic;
import ru.icc.cells.writers.TableToHtmlWriter;

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
                    process(file);
//                            TableBoxToXmlWriter writer = new TableBoxToXmlWriter();
//                            writer.write(findTables(file), file.getName(),
//                                         SAVE_PDF_DIR + "xml/" + file.getName().substring(0, file.getName().lastIndexOf('.')) +
//                                         "-reg-output.xml");
                }
//        process(new File("src/test/resources/pdf/Aeroflot_FS_2006.10.pdf"));
    }

    private static TextChunkProcessorConfiguration getDetectionConfiguration() {
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
                            .addStringsToReplace(new String[]{"•", "", " ", "_", "\u0002"/**/})
                            .setRemoveColons(true);
    }

    private static TextChunkProcessorConfiguration getRecognizingConfiguration() {
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
                            .addStringsToReplace(new String[]{"•", "", " ", "_", "\u0002"/**/})
                            .setRemoveColons(false);
    }

    private static List<TableBox> process(File file) {
        try {
            Debug.handleFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Debug.println(file.getName());
        List<TableBox> tableBoxes = new ArrayList<>();
        try {
            PdfContentExtractor extractor = new PdfContentExtractor(file.getAbsolutePath());
            for (int pageNumber = 0; pageNumber < extractor.getNumberOfPages(); pageNumber++) {
                Page page = extractor.getPageContent(pageNumber);

                List<TableBox> pageTableBoxes = findTables(page);
                for (TableBox tableBox : pageTableBoxes) {
                    tableBox.setPageNumber(pageNumber + 1);
                }
                tableBoxes.addAll(pageTableBoxes);

                Debug.drawRects(pageTableBoxes);

                for (TableBox pageTableBox : pageTableBoxes) {
                    Table             table  = recognizeTable(page, pageTableBox);
                    TableToHtmlWriter writer = new TableToHtmlWriter();
                    writer.write(table, SAVE_PDF_DIR + "html/" + file.getName() + ".html");
                    for (Row row : table.getRows()) {
                        for (Cell cell : row.getCells()) {
                            Debug.print(" | " + cell.getText());
                        }
                        Debug.println(" | ");
                    }
                    Debug.println();
                }
            }
        } catch (IOException | ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }

        try {
            Debug.close(SAVE_PDF_DIR + file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tableBoxes;
    }

    private static Table recognizeTable(Page page, TableBox pageTableBox) {
        Page                            region            = page.getRegion(pageTableBox);
        TextChunkProcessorConfiguration recognitionConfig = getRecognizingConfiguration();
        SimpleTableRecognizer           recognizer        = new SimpleTableRecognizer(recognitionConfig);
        return recognizer.recognize(region);
    }

    private static List<TableBox> findTables(Page page) {
        TextChunkProcessorConfiguration configuration =
                getDetectionConfiguration().addFilter(new LinesBetweenChunksBiHeuristic(page.getRulings()));
        List<TextBlock> textBlocks    = new TextChunkProcessor(page, configuration).process();
        TableDetector   tableDetector = new TableDetector(null);
        return tableDetector.detect(textBlocks);
    }
}
