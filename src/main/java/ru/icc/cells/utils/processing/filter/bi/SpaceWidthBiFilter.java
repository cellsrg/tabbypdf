package ru.icc.cells.utils.processing.filter.bi;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import ru.icc.cells.common.Rectangle;
import ru.icc.cells.common.TextBlock;
import ru.icc.cells.common.TextChunk;

public class SpaceWidthBiFilter extends BiHeuristic<Rectangle>
{
    private float   spaceWidthMultiplier;
    private boolean enableListCheck;

    public SpaceWidthBiFilter()
    {
        this(1f, false);
    }

    public SpaceWidthBiFilter(float spaceWidthMultiplier, boolean enableListCheck)
    {
        super(Orientation.HORIZONTAL);
        this.spaceWidthMultiplier = spaceWidthMultiplier;
        enableListCheck = enableListCheck;
    }

    public SpaceWidthBiFilter enableListCheck(boolean value)
    {
        this.enableListCheck = value;
        return this;
    }

    @Override
    public boolean test(Rectangle first, Rectangle second)
    {
        TextChunk fc = null, sc = null;
        boolean   isList;
        if (first.getClass().equals(TextChunk.class) && second.getClass().equals(TextChunk.class))
        {
            fc = (TextChunk) first;
            sc = (TextChunk) second;
            isList = false;
        }
        else if (first.getClass().equals(TextBlock.class) && second.getClass().equals(TextBlock.class))
        {
            fc = ((TextBlock) first).getChunks().get(((TextBlock) first).getChunks().size() - 1);
            sc = ((TextBlock) second).getChunks().get(0);
            isList = ((TextBlock) first).getText().matches("\\d+\\.\\s*");// example: '1. '
        }
        else
        {
            return true;
        }
        if (fc.getRight() >= sc.getLeft()) return true;
        float spaceWidth;
        if (fc.getText().contains(" "))
        {
            spaceWidth = fc.getCharSpaceWidth();
            if (spaceWidth > 20)
            {
                spaceWidth = 20;
            }
        }
        else
        {
            spaceWidth = new Chunk(' ', new Font(fc.getChunkFont())).getWidthPoint();
            if (spaceWidth == 0)
            {
                spaceWidth = 3;
            }
        }
        float chunkDistance = sc.getLeft() - fc.getRight();
        return (chunkDistance <= spaceWidth * spaceWidthMultiplier) || (enableListCheck && isList);
    }
}
