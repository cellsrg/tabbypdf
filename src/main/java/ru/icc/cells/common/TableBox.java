package ru.icc.cells.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains table region that belongs to the same table
 */
public class TableBox extends Rectangle
{
    private int pageNumber;
    private List<TableRegion> tableRegions = new ArrayList<>();
    private TextBlock associatedTableKeyWordBlock;

    public TableBox()
    {
        super();
    }

    public List<TableRegion> getTableRegions()
    {
        return tableRegions;
    }

    /**
     * Add table region to this table
     * @param tableRegion
     */
    public void add(TableRegion tableRegion)
    {
        join(tableRegion);
        tableRegions.add(tableRegion);
    }

    @Override
    protected <T extends Rectangle> void join(T other)
    {
        if (tableRegions.isEmpty())
        {
            setLeft(other.getLeft());
            setBottom(other.getBottom());
            setRight(other.getRight());
            setTop(other.getTop());
        }
        else
        {
            super.join(other);
        }
    }

    public void setPageNumber(int pageNumber)
    {
        this.pageNumber = pageNumber;
    }

    public int getPageNumber()
    {
        return pageNumber;
    }

    public void setTableRegions(List<TableRegion> tableRegions)
    {
        this.tableRegions = tableRegions;
    }

    public TextBlock getAssociatedTableKeyWordBlock()
    {
        return associatedTableKeyWordBlock;
    }

    public void setAssociatedTableKeyWordBlock(TextBlock associatedTableKeyWordBlock)
    {
        this.associatedTableKeyWordBlock = associatedTableKeyWordBlock;
    }
}
