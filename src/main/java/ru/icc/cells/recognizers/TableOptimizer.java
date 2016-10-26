package ru.icc.cells.recognizers;

import ru.icc.cells.common.table.Cell;
import ru.icc.cells.common.table.Row;
import ru.icc.cells.common.table.Table;

import java.util.List;
import java.util.stream.Collectors;

public class TableOptimizer {
    public void optimize(Table table) {
        for (int i = 0; i < table.getRows().size() - 1; i++) {
            List<Cell> rowCells = table.getRows().get(i).getCells();
            for (Cell cell : rowCells) {
                if (cell.getColumnWidth() > 1) {
                    for (int k = i + 1; k < table.getRows().size(); k++) {
                        span(table.getRows().get(k), cell);
                    }
                }
            }
        }
    }

    private void span(Row row, Cell curr) {
        List<Cell> collect = getCellsToSpan(row, curr);
        if (collect.size() > 1 && collect.stream().filter(c -> !c.getText().isEmpty()).count() <= 1) {
            Cell cell = collect.stream().reduce((c1, c2) -> {
                c1.join(c2);
                return c1;
            }).orElse(collect.get(0));
            int cellIndex = row.getCells().indexOf(collect.get(0));
            row.getCells().removeAll(collect);
            row.getCells().add(cellIndex, cell);
        }
    }

    private List<Cell> getCellsToSpan(Row row, Cell baseCell) {
        return row.getCells()
                  .stream()
                  .filter(cell -> cell.getId() >= baseCell.getId() && cell.getId() + cell.getColumnWidth() <=
                                                                      baseCell.getId() + baseCell.getColumnWidth() &&
                                  cell.getColumnWidth() != baseCell.getColumnWidth())
                  .collect(Collectors.toList());
    }
}
