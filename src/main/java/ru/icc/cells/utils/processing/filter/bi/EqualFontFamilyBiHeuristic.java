package ru.icc.cells.utils.processing.filter.bi;

import ru.icc.cells.common.TextBlock;
import ru.icc.cells.common.TextChunk;
import ru.icc.cells.utils.content.FontUtils;

public class EqualFontFamilyBiHeuristic extends BiHeuristic<TextBlock> {

    public EqualFontFamilyBiHeuristic() {
        super(Orientation.BOTH);
    }

    public EqualFontFamilyBiHeuristic(Orientation orientation) {
        super(orientation);
    }

    @Override
    public boolean test(TextBlock first, TextBlock second) {
        boolean result = true;
        for (TextChunk chunk : first.getChunks()) {
            for (TextChunk textChunk : second.getChunks()) {
                String[] firstChunkFontFamilies  = FontUtils.getFontFamilies(chunk);
                String[] secondChunkFontFamilies = FontUtils.getFontFamilies(textChunk);
                if (firstChunkFontFamilies.length != secondChunkFontFamilies.length) result = false;
                for (int i = 0; i < firstChunkFontFamilies.length; i++) {
                    if (!firstChunkFontFamilies[i].equals(secondChunkFontFamilies[i])) result = false;
                }
                if (result) return true;
            }
        }
        return result;
    }
}
