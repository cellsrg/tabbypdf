package ru.icc.cells.utils.processing.filter.bi;

import ru.icc.cells.common.TextChunk;

public class EqualFontSizeBiFilter extends BiFilter<TextChunk> {
    public EqualFontSizeBiFilter() {
        super(Orientation.BOTH);
    }

    @Override
    public boolean filter(TextChunk first, TextChunk second) {
        return first.getFontSize() == second.getFontSize();
    }
}
