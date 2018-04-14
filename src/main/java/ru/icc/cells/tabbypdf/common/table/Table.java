package ru.icc.cells.tabbypdf.common.table;

import lombok.Getter;
import lombok.Setter;
import ru.icc.cells.tabbypdf.common.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Table extends Rectangle {
    @Setter
    private int pageNumber;
    private int rowsSize;
    private int columnsSize;

    private final List<Row> rows = new ArrayList<>();

    public Table(double left, double bottom, double right, double top) {
        super(left, bottom, right, top);
    }

    public void addCell(Cell cell, int rowId) {
        if (rows.size() < rowId + 1) {
            for (int i = rows.size(); i < rowId + 1; i++) {
                rows.add(new Row(rowId));
                rowsSize++;
            }
        }
        rows.get(rowId).addCell(cell);

        columnsSize = rows.stream()
            .mapToInt(row -> row.getCells().stream().mapToInt(Cell::getColumnWidth).sum())
            .max()
            .orElse(0);
    }

    public Row getRow(int rowNumber) {
        return rows.get(rowNumber);
    }

    public List<Cell> getColumn(int columnNumber) {
        List<Cell> columnCells = new ArrayList<>();
        for (Row row : rows) {
            columnCells.addAll(row.getCells()
                .stream()
                .filter(cell -> columnNumber >= cell.getId() && columnNumber < cell.getId() + cell.getColumnWidth())
                .collect(Collectors.toList()));
        }
        return columnCells;
    }
}
