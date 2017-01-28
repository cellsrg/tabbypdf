package ru.icc.cells.tabbypdf;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import ru.icc.cells.tabbypdf.common.Page;
import ru.icc.cells.tabbypdf.common.TableBox;
import ru.icc.cells.tabbypdf.common.TextBlock;
import ru.icc.cells.tabbypdf.common.table.Table;
import ru.icc.cells.tabbypdf.detectors.TableDetector;
import ru.icc.cells.tabbypdf.detectors.TableDetectorConfiguration;
import ru.icc.cells.tabbypdf.exceptions.EmptyArgumentException;
import ru.icc.cells.tabbypdf.exceptions.TableExtractionException;
import ru.icc.cells.tabbypdf.recognizers.SimpleTableRecognizer;
import ru.icc.cells.tabbypdf.recognizers.TableOptimizer;
import ru.icc.cells.tabbypdf.utils.content.PdfContentExtractor;
import ru.icc.cells.tabbypdf.utils.processing.TextChunkProcessor;
import ru.icc.cells.tabbypdf.utils.processing.TextChunkProcessorConfiguration;
import ru.icc.cells.tabbypdf.utils.processing.filter.Heuristic;
import ru.icc.cells.tabbypdf.utils.processing.filter.bi.*;
import ru.icc.cells.tabbypdf.utils.processing.filter.tri.CutInAfterTriHeuristic;
import ru.icc.cells.tabbypdf.utils.processing.filter.tri.CutInBeforeTriHeuristic;
import ru.icc.cells.tabbypdf.writers.TableToExcelWriter;
import ru.icc.cells.tabbypdf.writers.TableToHtmlWriter;
import ru.icc.cells.tabbypdf.writers.TableToXmlWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author aaltaev
 */
public class App {
    @Option(name = "-f", usage = "File name")
    private String file;
    @Option(name="-xml", usage = "Resulting xmlFile file")
    private String xmlFile;
    @Option(name="-html", usage = "Resulting html file")
    private String htmlFile;
    @Option(name="-excel", usage = "Resulting excel file")
    private String excelFile;

    private List<Table> extractedTables;

    public static void main(String[] args) {
        new App().run(args);
    }

    public void run(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);
            checkArgThrowIfEmpty(file);
            checkArgsThrowIfAllEmpty(xmlFile, htmlFile, excelFile);

            extract();

            if (checkArgIsNotEmpty(xmlFile)) {
                TableToXmlWriter writer = new TableToXmlWriter(file);
                try (FileWriter fileWriter = new FileWriter(xmlFile)) {
                    String xmlString = writer.write(extractedTables);
                    fileWriter.write(xmlString);
                } catch (ParserConfigurationException | TransformerException e) {
                    System.err.println("Can not extract tables.");
                } catch (IOException e) {
                    System.err.println("Can not create file " + xmlFile);
                }
            }

            if (checkArgIsNotEmpty(htmlFile)) {
                TableToHtmlWriter writer = new TableToHtmlWriter();
                try (FileWriter fileWriter = new FileWriter(htmlFile)) {
                    String htmlString = writer
                            .write(extractedTables)
                            .stream()
                            .collect(Collectors.joining("<br><br><br>"));
                    fileWriter.write(htmlString);
                } catch (ParserConfigurationException | TransformerException e) {
                    System.err.println("Can not extract tables.");
                } catch (IOException e) {
                    System.err.println("Can not create file " + htmlFile);
                }
            }

            if (checkArgIsNotEmpty(excelFile)) {
                TableToExcelWriter writer = new TableToExcelWriter();

                try (FileOutputStream fileOutputStream = new FileOutputStream(excelFile)) {
                    XSSFWorkbook workbook = writer.write(extractedTables);
                    workbook.write(fileOutputStream);
                    workbook.close();
                } catch (FileNotFoundException e) {
                    System.err.println("Can not create file " + excelFile);
                } catch (IOException e) {
                    System.err.println("Can not write file " + excelFile);
                }
            }

        } catch (CmdLineException | EmptyArgumentException e) {
            parser.printUsage(System.err);
        } catch (TableExtractionException e) {
            System.err.println("Can not extract table from file " + file);
        }

    }

    private void checkArgsThrowIfAllEmpty(String... args) {
        if (!Stream.of(args).anyMatch(this::checkArgIsNotEmpty)) {
            throw new EmptyArgumentException("At least one of these options must be specified: -xml, -excel, -html.");
        }
    }

    private void checkArgThrowIfEmpty(String arg) {
        if (!checkArgIsNotEmpty(arg)) throw new EmptyArgumentException("Required option was not specified.");
    }

    private boolean checkArgIsNotEmpty(String arg) {
        return arg != null && !arg.isEmpty();
    }

    public void extract() throws TableExtractionException {
        try {
            PdfContentExtractor extractor = new PdfContentExtractor(file);
            List<TableBox> tableBoxes = new ArrayList<>();
            List<Table> tables = new ArrayList<>();
            for (int pageNumber = 0; pageNumber < extractor.getNumberOfPages(); pageNumber++) {
                Page page = extractor.getPageContent(pageNumber);
                List<TableBox> fromPage = findTables(page);

                for (TableBox tableBox : fromPage) {
                    tableBox.setPageNumber(pageNumber);
                    for (TableBox pageTableBox : fromPage)
                    {
                        Table          table     = recognizeTable(page, pageTableBox);
                        TableOptimizer optimizer = new TableOptimizer();
                        optimizer.optimize(table);
                        table.setPageNumber(pageNumber);
                        tables.add(table);
                    }
                }

                tableBoxes.addAll(fromPage);

                extractedTables = tables;
            }
        } catch (IOException e) {
            throw new TableExtractionException();
        }
    }

    public static List<TableBox> findTables(Page page)
    {
        TextChunkProcessorConfiguration configuration =
                getDetectionConfiguration().addFilter(new LinesBetweenChunksBiHeuristic(page.getRulings()));
        List<TextBlock> textBlocks = new TextChunkProcessor(page, configuration).process();

        TableDetectorConfiguration cnf = new TableDetectorConfiguration()
                .setUseSortedTextBlocks(true)
                .setMinRegionGapProjectionIntersection(1);
        TableDetector tableDetector = new TableDetector(cnf);

        List<TableBox> withSort = tableDetector.detect(textBlocks);

        //        cnf.setUseSortedTextBlocks(false);
        //        List<TableBox> noSort = tableDetector.detect(textBlocks);

        return withSort;
    }

    public static TextChunkProcessorConfiguration getDetectionConfiguration()
    {
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
        //                            .setUseCharacterChunks(true);
    }

    public static Table recognizeTable(Page page, TableBox pageTableBox)
    {
        Page region = page.getRegion(pageTableBox);
        TextChunkProcessorConfiguration recognitionConfig =
                getRecognizingConfiguration().addFilter(new LinesBetweenChunksBiHeuristic(page.getRulings()));
        SimpleTableRecognizer recognizer = new SimpleTableRecognizer(recognitionConfig);
        return recognizer.recognize(region);
    }

    public static TextChunkProcessorConfiguration getRecognizingConfiguration()
    {
        return new TextChunkProcessorConfiguration()
                            /*VERTICAL FILTERS*/.addFilter(new HorizontalPositionBiHeuristic())
                            .addFilter(new SpaceWidthBiFilter().enableListCheck(false))
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

    public static void removeFalseTableBoxes(List<TableBox> tableBoxes, List<Table> tables) {
        long tBoxesWithKeyWordsCount =
                tableBoxes.stream()
                          .filter(tb -> tb.getAssociatedTableKeyWordBlock() != null)
                          .count();
        if (tBoxesWithKeyWordsCount != 0 && tableBoxes.size() != tBoxesWithKeyWordsCount)
        {
            List<TableBox> boxesToRemove = new ArrayList<>();
            List<Table>    tblesToRemove = new ArrayList<>();
            for (int i = 0; i < tableBoxes.size(); i++)
            {
                if (tableBoxes.get(i).getAssociatedTableKeyWordBlock() == null)
                {
                    boxesToRemove.add(tableBoxes.get(i));
                    tblesToRemove.add(tables.get(i));
                }
            }
            tableBoxes.removeAll(boxesToRemove);
            tables.removeAll(tblesToRemove);
        }
    }
}
