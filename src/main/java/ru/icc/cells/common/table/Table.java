package ru.icc.cells.common.table;

import ru.icc.cells.common.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Table extends Rectangle {
    private final List<Row> rows = new ArrayList<>();

    public Table(float left, float bottom, float right, float top) {
        super(left, bottom, right, top);
    }

    public void addCell(Cell cell, int rowId) {
        if (rows.size() < rowId + 1) {
            for (int i = rows.size(); i < rowId + 1; i++) {
                rows.add(new Row(rowId));
            }
        }
        rows.get(rowId).addCell(cell);
    }

    public List<Row> getRows() {
        return rows;
    }
}
