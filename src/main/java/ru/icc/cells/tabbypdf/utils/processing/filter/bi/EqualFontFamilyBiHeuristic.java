package ru.icc.cells.tabbypdf.utils.processing.filter.bi;

import ru.icc.cells.tabbypdf.entities.FontCharacteristics;
import ru.icc.cells.tabbypdf.entities.TextBlock;
import ru.icc.cells.tabbypdf.entities.TextChunk;

import java.util.Objects;

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
            .filter(Objects::nonNull)
            .map(TextChunk::getFontCharacteristics)
            .filter(Objects::nonNull)
            .map(FontCharacteristics::getFontFamily)
            .filter(Objects::nonNull)
            .anyMatch(firstFamily ->
                second.getChunks().stream()
                    .map(TextChunk::getFontCharacteristics)
                    .filter(Objects::nonNull)
                    .map(FontCharacteristics::getFontFamily)
                    .filter(Objects::nonNull)
                    .anyMatch(firstFamily::equals)
            );
    }
}
