package ru.icc.cells.tabbypdf.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains table region that belongs to the same table
 */
@Data
@NoArgsConstructor
public class TableBox extends Rectangle {
    private int pageNumber;
    private List<TableRegion> tableRegions = new ArrayList<>();
    private TextBlock associatedTableKeyWordBlock;

    /**
     * Add table region to this table
     *
     * @param tableRegion
     */
    public void add(TableRegion tableRegion) {
        join(tableRegion);
        tableRegions.add(tableRegion);
    }

    @Override
    protected <T extends Rectangle> void join(T other) {
        if (tableRegions.isEmpty()) {
            setLeft(other.getLeft());
            setBottom(other.getBottom());
            setRight(other.getRight());
            setTop(other.getTop());
        } else {
            super.join(other);
        }
    }
}
