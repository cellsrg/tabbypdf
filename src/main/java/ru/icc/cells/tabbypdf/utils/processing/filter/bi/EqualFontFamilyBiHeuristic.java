package ru.icc.cells.tabbypdf.utils.processing.filter.bi;

import ru.icc.cells.tabbypdf.common.FontCharacteristics;
import ru.icc.cells.tabbypdf.common.TextBlock;
import ru.icc.cells.tabbypdf.common.TextChunk;

public class EqualFontFamilyBiHeuristic extends BiHeuristic<TextBlock> {

    public EqualFontFamilyBiHeuristic() {
        super(Orientation.BOTH);
    }

    public EqualFontFamilyBiHeuristic(Orientation orientation) {
        super(orientation);
    }

    @Override
    public boolean test(TextBlock first, TextBlock second) {
        return first.getChunks().stream()
            .map(TextChunk::getFontCharacteristics)
            .map(FontCharacteristics::getFontFamily)
            .anyMatch(firstFamily ->
                second.getChunks().stream()
                    .map(TextChunk::getFontCharacteristics)
                    .map(FontCharacteristics::getFontFamily)
                    .anyMatch(firstFamily::equals)
            );
    }
}
