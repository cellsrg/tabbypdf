package ru.icc.cells.tabbypdf;

import ru.icc.cells.tabbypdf.common.Page;
import ru.icc.cells.tabbypdf.common.TableBox;
import ru.icc.cells.tabbypdf.common.TableRegion;
import ru.icc.cells.tabbypdf.common.TextLine;
import ru.icc.cells.tabbypdf.common.table.Table;
import ru.icc.cells.tabbypdf.debug.Debug;
import ru.icc.cells.tabbypdf.pdfbox.PdfBoxDataExtractor;
import ru.icc.cells.tabbypdf.recognizers.TableOptimizer;
import ru.icc.cells.tabbypdf.writers.TableBoxToXmlWriter;
import ru.icc.cells.tabbypdf.writers.TableToHtmlWriter;
import ru.icc.cells.tabbypdf.writers.TableToXmlWriter;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Example {
    public static final String TEST_PDF_DIR = "src/test/resources/pdf/";
    public static final String SAVE_PDF_DIR = "src/test/resources/pdf/edit/";

    public static void main(String[] args) {
        Debug.ENABLE_DEBUG = true;
        File folder = new File(TEST_PDF_DIR);
        for (File file : folder.listFiles(File::isFile)) {
            if (file.getName().lastIndexOf(".pdf") == file.getName().length() - 4) {
                process(file);
            }
        }
        //        process(new File("src/test/resources/pdf/us-007.pdf"));
    }

    private static List<TableBox> process(File file) {
        Debug.println(file.getName());
        Debug.handleFile(file);


        List<TableBox> tableBoxes = new ArrayList<>();
        List<Table> tables = new ArrayList<>();

        try {
            List<Page> pages = new PdfBoxDataExtractor.Factory().getPdfBoxTextExtractor(file).getPageContent();

            for (int pageNumber = 0; pageNumber < pages.size(); pageNumber++) {
                Debug.setPage(pageNumber);
                Page page = pages.get(pageNumber);

                List<TableBox> pageTableBoxes = App.findTables(page);
                for (TableBox tableBox : pageTableBoxes) {
                    tableBox.setPageNumber(pageNumber);
                }
                tableBoxes.addAll(pageTableBoxes);

                for (TableBox pageTableBox : pageTableBoxes) {
                    Table table = App.recognizeTable(page, pageTableBox);
                    TableOptimizer optimizer = new TableOptimizer();
                    optimizer.optimize(table);
                    table.setPageNumber(pageNumber);
                    tables.add(table);
                }
            }

            App.removeFalseTableBoxes(tableBoxes, tables);

            drawTBoxes(tableBoxes);


            writeTableBoxes(tableBoxes, file.getName());
            writeTables(tables, file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Debug.close(SAVE_PDF_DIR + file.getName());

        return tableBoxes;
    }

    private static void drawTBoxes(List<TableBox> tableBoxes) throws IOException {
        for (TableBox tableBox : tableBoxes) {
            Debug.setPage(tableBox.getPageNumber());
            if (tableBox.getAssociatedTableKeyWordBlock() != null) {
                Debug.setColor(Color.MAGENTA);
                Debug.drawRect(tableBox.getAssociatedTableKeyWordBlock());
            }
            Debug.setColor(Color.RED);
            Debug.drawRect(tableBox);
            Debug.setColor(Color.GREEN);
            Debug.drawRects(tableBox.getTableRegions());
            for (TableRegion tableRegion : tableBox.getTableRegions()) {
                Debug.setColor(Color.BLUE);
                Debug.drawRects(tableRegion.getTextLines());
                for (TextLine textLine : tableRegion.getTextLines()) {
                    Debug.setColor(Color.CYAN);
                    Debug.drawRects(textLine.getTextBlocks());
                }
            }
        }
    }

    private static void writeTableBoxes(List<TableBox> tableBoxes, String fileName) {
        TableBoxToXmlWriter writer = new TableBoxToXmlWriter(fileName);
        try {
            FileWriter fileWriter = new FileWriter(
                SAVE_PDF_DIR + "xml/" + fileName.substring(0, fileName.lastIndexOf('.')) + "-reg-output.xml");
            fileWriter.write(writer.write(tableBoxes));
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeTables(List<Table> tables, String fileName) {
        for (int i = 0; i < tables.size(); i++) {
            Table table = tables.get(i);
            TableToHtmlWriter writer = new TableToHtmlWriter();
            try {
                FileWriter fileWriter = new FileWriter(SAVE_PDF_DIR + "html/" + fileName + "." + i + ".html");
                fileWriter.write(writer.write(table));
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        TableToXmlWriter tableToXmlWriter = new TableToXmlWriter(fileName);
        try {
            FileWriter fileWriter = new FileWriter(SAVE_PDF_DIR + "xml/" + fileName.substring(0, fileName.lastIndexOf('.')) + "-str-output.xml");
            fileWriter.write(tableToXmlWriter.write(tables));
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
