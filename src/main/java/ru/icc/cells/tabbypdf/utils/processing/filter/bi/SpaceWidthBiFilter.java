package ru.icc.cells.tabbypdf.utils.processing.filter.bi;

import ru.icc.cells.tabbypdf.common.Rectangle;
import ru.icc.cells.tabbypdf.common.TextBlock;
import ru.icc.cells.tabbypdf.common.TextChunk;
import ru.icc.cells.tabbypdf.utils.processing.filter.Heuristic;

public class SpaceWidthBiFilter extends BiHeuristic<Rectangle> {
    private float spaceWidthMultiplier;
    private boolean enableListCheck;

    public SpaceWidthBiFilter() {
        this(1f, false);
    }

    public SpaceWidthBiFilter(float spaceWidthMultiplier, boolean enableListCheck) {
        super(Heuristic.Orientation.HORIZONTAL);
        this.spaceWidthMultiplier = spaceWidthMultiplier;
        this.enableListCheck = enableListCheck;
    }

    public SpaceWidthBiFilter enableListCheck(boolean value) {
        this.enableListCheck = value;
        return this;
    }

    @Override
    public boolean test(Rectangle first, Rectangle second) {
        TextChunk fc = null, sc = null;
        boolean isList;
        if (first.getClass().equals(TextChunk.class) && second.getClass().equals(TextChunk.class)) {
            fc = (TextChunk) first;
            sc = (TextChunk) second;
            isList = false;
        } else if (first.getClass().equals(TextBlock.class) && second.getClass().equals(TextBlock.class)) {
            fc = ((TextBlock) first).getChunks().get(((TextBlock) first).getChunks().size() - 1);
            sc = ((TextBlock) second).getChunks().get(0);
            isList = ((TextBlock) first).getText().matches("\\d+\\.\\s*");// example: '1. '
        } else {
            return true;
        }

        if (fc.getRight() >= sc.getLeft()) {
            return true;
        }

        return (sc.getLeft() - fc.getRight() <= fc.getFontCharacteristics().getSpaceWidth() * spaceWidthMultiplier)
            || (enableListCheck && isList);
    }
}
