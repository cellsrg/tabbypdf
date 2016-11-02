package ru.icc.cells.common.table;

import java.util.ArrayList;
import java.util.List;

public class Row
{
    private final int id;
    private final List<Cell> cells = new ArrayList<>();

    public Row(int id)
    {
        this.id = id;
    }

    public List<Cell> getCells()
    {
        return cells;
    }

    public int getId()
    {
        return id;
    }

    public void addCell(Cell cell)
    {
        cells.add(cell);
        cells.sort((c1, c2) -> Integer.compare(c1.getId(), c2.getId()));
    }

    public void removeCell(int cellId)
    {
        cells.removeIf(cell -> cell.getId() == cellId);
    }
}
