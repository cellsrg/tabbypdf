package ru.icc.cells.tabbypdf.utils.processing.filter.bi;

import ru.icc.cells.tabbypdf.common.Rectangle;
import ru.icc.cells.tabbypdf.common.TextBlock;
import ru.icc.cells.tabbypdf.common.TextChunk;

/**
 * Проверка на то, первый чанк находится левее второго и что их вертикальные проекции пересекаются.
 */
public class HorizontalPositionBiHeuristic extends BiHeuristic<Rectangle> {

    public HorizontalPositionBiHeuristic() {
        super(Orientation.HORIZONTAL);
    }

    @Override
    public boolean test(Rectangle first, Rectangle second) {
        double lx1, lx2, ty1, ty2, by1, by2;
        if (first.getClass().equals(TextBlock.class) && second.getClass().equals(TextBlock.class)) {
            TextChunk firstChunk = ((TextBlock) first).getChunks().get(((TextBlock) first).getChunks().size() - 1);
            TextChunk secondChunk = ((TextBlock) second).getChunks().get(0);
            lx1 = firstChunk.getLeft();
            lx2 = secondChunk.getLeft();
            ty1 = firstChunk.getTop();
            ty2 = secondChunk.getTop();
            by1 = firstChunk.getBottom();
            by2 = secondChunk.getBottom();
        } else {
            lx1 = first.getLeft();
            lx2 = second.getLeft();
            ty1 = first.getTop();
            ty2 = second.getTop();
            by1 = first.getBottom();
            by2 = second.getBottom();
        }
        return (lx1 <= lx2) && (ty1 > by2) && (by1 < ty2);
    }
}
