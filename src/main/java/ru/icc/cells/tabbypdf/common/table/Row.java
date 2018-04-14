package ru.icc.cells.tabbypdf.common.table;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class Row {
    private final int        id;
    private final List<Cell> cells = new ArrayList<>();

    public Row(int id) {
        this.id = id;
    }

    public void addCell(Cell cell) {
        cells.add(cell);
        cells.sort(Comparator.comparingInt(Cell::getId));
    }

    public void removeCell(int cellId) {
        cells.removeIf(cell -> cell.getId() == cellId);
    }
}
