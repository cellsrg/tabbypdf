package ru.icc.cells.common.table;

import ru.icc.cells.common.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Table extends Rectangle
{
    private final List<Row> rows = new ArrayList<>();

    private int rowsSize = 0, columnsSize = 0;

    public Table(float left, float bottom, float right, float top)
    {
        super(left, bottom, right, top);
    }

    public void addCell(Cell cell, int rowId)
    {
        if (rows.size() < rowId + 1)
        {
            for (int i = rows.size(); i < rowId + 1; i++)
            {
                rows.add(new Row(rowId));
                rowsSize++;
            }
        }
        rows.get(rowId).addCell(cell);

        columnsSize = rows.stream()
                          .map(row -> row.getCells()
                                         .stream()
                                         .map(Cell::getColumnWidth)
                                         .reduce(Integer::sum)
                                         .orElse(0))
                          .max(Integer::compare)
                          .orElse(0);
    }

    public int getRowsSize()
    {
        return rowsSize;
    }

    public int getColumnsSize()
    {
        return columnsSize;
    }

    public Row getRow(int rowNumber)
    {
        return rows.get(rowNumber);
    }

    public List<Cell> getColumn(int columnNumber)
    {
        List<Cell> columnCells = new ArrayList<>();
        for (Row row : rows)
        {
            columnCells.addAll(row.getCells()
                                  .stream()
                                  .filter(cell -> columnNumber >= cell.getId() &&
                                                  columnNumber < cell.getId() + cell.getColumnWidth())
                                  .collect(Collectors.toList()));
        }
        return columnCells;
    }

}
