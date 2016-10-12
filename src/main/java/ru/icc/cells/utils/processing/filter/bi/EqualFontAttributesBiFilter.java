package ru.icc.cells.utils.processing.filter.bi;

import ru.icc.cells.common.TextChunk;
import ru.icc.cells.utils.content.FontUtils;

public class EqualFontAttributesBiFilter extends BiFilter<TextChunk> {
    public EqualFontAttributesBiFilter() {
        super(Orientation.BOTH);
    }

    @Override
    public boolean filter(TextChunk first, TextChunk second) {
        String[] firstChunkFontAttributes  = FontUtils.getFontAttributes(first);
        String[] secondChunkFontAttributes = FontUtils.getFontAttributes(second);
        if (firstChunkFontAttributes.length != secondChunkFontAttributes.length) return false;
        for (int i = 0; i < firstChunkFontAttributes.length; i++) {
            if (!firstChunkFontAttributes[i].equals(secondChunkFontAttributes[i])) return false;
        }
        return true;
    }
}
