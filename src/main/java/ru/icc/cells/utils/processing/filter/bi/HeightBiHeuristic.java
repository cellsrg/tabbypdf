package ru.icc.cells.utils.processing.filter.bi;

import ru.icc.cells.common.Rectangle;
import ru.icc.cells.common.TextBlock;

public class HeightBiHeuristic extends BiHeuristic<Rectangle>
{
    private float heightMultiplier;

    public HeightBiHeuristic()
    {
        this(1f);
    }

    public HeightBiHeuristic(float heightMultiplier)
    {
        super(Orientation.VERTICAL);
        this.heightMultiplier = heightMultiplier;
    }

    @Override
    public boolean test(Rectangle first, Rectangle second)
    {
        float height;
        if (second.getClass().equals(TextBlock.class))
        {
            height = (second.getTop() - ((TextBlock) second).getChunks().get(0).getBottom()) * heightMultiplier;
        }
        else
        {
            height = (Math.abs(second.getTop() - second.getBottom()) * heightMultiplier);
        }
        float distance =
                Math.abs(((first.getTop() > second.getTop()) ? first.getBottom() : first.getTop()) - second.getTop());
        return distance <= height;
    }
}
