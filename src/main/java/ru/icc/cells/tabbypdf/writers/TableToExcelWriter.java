package ru.icc.cells.tabbypdf.writers;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.icc.cells.tabbypdf.common.table.Cell;
import ru.icc.cells.tabbypdf.common.table.Row;
import ru.icc.cells.tabbypdf.common.table.Table;

import java.util.List;

/**
 * @author aaltaev
 */
public class TableToExcelWriter implements Writer<Table, XSSFWorkbook> {
    @Override
    public XSSFWorkbook write(List<Table> tables) {
        XSSFWorkbook workbook = new XSSFWorkbook();

        for (Integer tableNum = 0; tableNum < tables.size(); tableNum++) {
            addSheet(tables, workbook, tableNum);
        }

        return workbook;
    }

    private void addSheet(List<Table> tables, XSSFWorkbook workbook, Integer tableNum) {
        XSSFSheet sheet = workbook.createSheet(tableNum.toString());
        List<Row> rows  = tables.get(tableNum).getRows();
        for (int rowNum = 0; rowNum < rows.size(); rowNum++) {
            addRow(sheet, rows, rowNum);
        }
    }

    private void addRow(XSSFSheet sheet, List<Row> rows, int rowNum) {
        XSSFRow excelRow = sheet.createRow(rowNum);
        List<Cell> cells = rows.get(rowNum).getCells();
        for (int cellNum = 0; cellNum < cells.size(); cellNum++) {
            addCell(excelRow, cells.get(cellNum));
        }
    }

    private XSSFCell addCell(XSSFRow excelRow, Cell cell) {
        XSSFCell excelCell = excelRow.createCell(cell.getId());
        excelCell.setCellValue(cell.getText());
        return excelCell;
    }
}
