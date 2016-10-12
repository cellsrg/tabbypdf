package ru.icc.cells.utils.processing.filter.bi;

import ru.icc.cells.common.TextChunk;
import ru.icc.cells.utils.content.FontUtils;

public class EqualFontFamilyBiFilter extends BiFilter<TextChunk> {

    public EqualFontFamilyBiFilter() {
        super(Orientation.BOTH);
    }

    @Override
    public boolean filter(TextChunk first, TextChunk second) {
        String[] firstChunkFontFamilies  = FontUtils.getFontFamilies(first);
        String[] secondChunkFontFamilies = FontUtils.getFontFamilies(second);
        if (firstChunkFontFamilies.length!=secondChunkFontFamilies.length) return false;
        for (int i = 0; i < firstChunkFontFamilies.length; i++) {
            if (!firstChunkFontFamilies[i].equals(secondChunkFontFamilies[i])) return false;
        }
        return true;
    }
}
