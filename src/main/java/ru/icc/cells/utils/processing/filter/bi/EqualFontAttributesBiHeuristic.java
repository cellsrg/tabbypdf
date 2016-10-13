package ru.icc.cells.utils.processing.filter.bi;

import ru.icc.cells.common.TextChunk;
import ru.icc.cells.utils.content.FontUtils;

public class EqualFontAttributesBiHeuristic extends BiHeuristic<TextChunk> {
    public EqualFontAttributesBiHeuristic() {
        super(Orientation.BOTH);
    }

    public EqualFontAttributesBiHeuristic(Orientation orientation) {
        super(orientation);
    }

    @Override
    public boolean test(TextChunk first, TextChunk second) {
        String[] firstChunkFontAttributes  = FontUtils.getFontAttributes(first);
        String[] secondChunkFontAttributes = FontUtils.getFontAttributes(second);
        if (firstChunkFontAttributes.length != secondChunkFontAttributes.length) return false;
        for (int i = 0; i < firstChunkFontAttributes.length; i++) {
            if (!firstChunkFontAttributes[i].equals(secondChunkFontAttributes[i])) return false;
        }
        return true;
    }
}
