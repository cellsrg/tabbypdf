package ru.icc.cells.tabbypdf.recognizers;

import ru.icc.cells.tabbypdf.common.table.Cell;
import ru.icc.cells.tabbypdf.common.table.Row;
import ru.icc.cells.tabbypdf.common.table.Table;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TableOptimizer
{
    public void optimize(Table table)
    {
        for (int rowNumber = 0; rowNumber < table.getRowsSize(); rowNumber++)
        {
            List<Cell> rowCells = table.getRow(rowNumber).getCells();
            for (Cell cell : rowCells)
            {
                if (cell.getColumnWidth() > 1)
                {
                    for (int i = rowNumber + 1; i < table.getRowsSize(); i++)
                    {
                        span(table.getRow(i), cell);
                    }
                }
            }
        }
    }

    private boolean isColumnStraight(List<Cell> column)
    {
        int  maxColWidth      = getMaxColWidth(column);
        long countOfMaxWidths = column.stream().filter(cell -> cell.getColumnWidth() == maxColWidth).count();
        return column.size() == countOfMaxWidths;
    }

    private int getMaxColWidth(List<Cell> column)
    {
        return Collections.max(column, (c1, c2) -> Integer.compare(c1.getColumnWidth(), c2.getColumnWidth())).getColumnWidth();
    }

    private void span(Row row, Cell curr)
    {
        List<Cell> collect = getCellsToSpan(row, curr);
        if (collect.size() > 1 && collect.stream().filter(c -> !c.getText().isEmpty()).count() <= 1)
        {
            Cell cell = collect
                    .stream()
                    .reduce((c1, c2) ->
                            {
                                c1.join(c2);
                                return c1;
                            })
                    .orElse(collect.get(0));
            int cellIndex = row.getCells().indexOf(collect.get(0));
            row.getCells().removeAll(collect);
            row.getCells().add(cellIndex, cell);
        }
    }

    private List<Cell> getCellsToSpan(Row row, Cell baseCell)
    {
        return row.getCells()
                  .stream()
                  .filter(cell -> cell.getId() >= baseCell.getId() && cell.getId() + cell.getColumnWidth() <=
                                                                      baseCell.getId() + baseCell.getColumnWidth() &&
                                  cell.getColumnWidth() != baseCell.getColumnWidth())
                  .collect(Collectors.toList());
    }
}
