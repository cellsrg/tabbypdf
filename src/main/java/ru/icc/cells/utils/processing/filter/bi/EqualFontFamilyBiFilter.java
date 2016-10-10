package ru.icc.cells.utils.processing.filter.bi;

import ru.icc.cells.common.TextChunk;

public class EqualFontFamilyBiFilter extends BiFilter<TextChunk> {

    public EqualFontFamilyBiFilter() {
        super(Orientation.BOTH);
    }

    @Override
    public boolean filter(TextChunk first, TextChunk second) {
        String[][] firstChunkFamilyFontName  = first.getChunkFont().getFamilyFontName();
        String[][] secondChunkFamilyFontName = second.getChunkFont().getFamilyFontName();
        if (firstChunkFamilyFontName.length != secondChunkFamilyFontName.length) return false;
        for (int i = 0; i < firstChunkFamilyFontName.length; i++) {
            for (int j = 0; j < 4; j++) {
                if (!firstChunkFamilyFontName[i][j].equals(secondChunkFamilyFontName[i][j])) return false;
            }
        }
        return true;
    }
}
