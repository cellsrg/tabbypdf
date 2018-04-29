package ru.icc.cells.tabbypdf.utils.processing.filter.bi;

import ru.icc.cells.tabbypdf.entities.Rectangle;
import ru.icc.cells.tabbypdf.entities.TextBlock;
import ru.icc.cells.tabbypdf.entities.TextChunk;
import ru.icc.cells.tabbypdf.utils.processing.filter.Heuristic;

public class SpaceWidthBiFilter extends BiHeuristic<Rectangle> {
    private double  spaceWidthMultiplier;
    private boolean enableListCheck;

    public SpaceWidthBiFilter() {
        this(1f, false);
    }

    public SpaceWidthBiFilter(double spaceWidthMultiplier, boolean enableListCheck) {
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

        double spaceWidth = Double.max(
            fc.getFontCharacteristics().getSpaceWidth(),
            sc.getFontCharacteristics().getSpaceWidth()
        );
        return (sc.getLeft() - fc.getRight() <= spaceWidth * spaceWidthMultiplier)
            || (enableListCheck && isList);
    }
}
