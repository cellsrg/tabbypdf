package ru.icc.cells.utils.processing.filter.bi;

import ru.icc.cells.common.TextBlock;
import ru.icc.cells.common.TextChunk;
import ru.icc.cells.utils.content.FontUtils;

public class EqualFontAttributesBiHeuristic extends BiHeuristic<TextBlock>
{
    public EqualFontAttributesBiHeuristic()
    {
        super(Orientation.BOTH);
    }

    public EqualFontAttributesBiHeuristic(Orientation orientation)
    {
        super(orientation);
    }

    @Override
    public boolean test(TextBlock first, TextBlock second)
    {
        boolean result = true;
        for (TextChunk chunk : first.getChunks())
        {
            for (TextChunk textChunk : second.getChunks())
            {
                String[] firstChunkFontAttributes  = FontUtils.getFontAttributes(chunk);
                String[] secondChunkFontAttributes = FontUtils.getFontAttributes(textChunk);
                if (firstChunkFontAttributes.length != secondChunkFontAttributes.length) result = false;
                for (int i = 0; i < firstChunkFontAttributes.length; i++)
                {
                    if (!firstChunkFontAttributes[i].equals(secondChunkFontAttributes[i])) result = false;
                }
                if (result) return true;
            }
        }
        return result;
    }
}
