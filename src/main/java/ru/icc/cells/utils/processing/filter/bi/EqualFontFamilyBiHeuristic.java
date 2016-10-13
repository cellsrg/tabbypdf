package ru.icc.cells.utils.processing.filter.bi;

import ru.icc.cells.common.TextChunk;
import ru.icc.cells.utils.content.FontUtils;

public class EqualFontFamilyBiHeuristic extends BiHeuristic<TextChunk> {

    public EqualFontFamilyBiHeuristic() {
        super(Orientation.BOTH);
    }

    public EqualFontFamilyBiHeuristic(Orientation orientation) {
        super(orientation);
    }

    @Override
    public boolean test(TextChunk first, TextChunk second) {
        String[] firstChunkFontFamilies  = FontUtils.getFontFamilies(first);
        String[] secondChunkFontFamilies = FontUtils.getFontFamilies(second);
        if (firstChunkFontFamilies.length!=secondChunkFontFamilies.length) return false;
        for (int i = 0; i < firstChunkFontFamilies.length; i++) {
            if (!firstChunkFontFamilies[i].equals(secondChunkFontFamilies[i])) return false;
        }
        return true;
    }
}
