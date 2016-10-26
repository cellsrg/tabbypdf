package ru.icc.cells.recognizers;

import ru.icc.cells.common.Page;
import ru.icc.cells.common.Rectangle;
import ru.icc.cells.common.TextBlock;
import ru.icc.cells.common.table.Cell;
import ru.icc.cells.common.table.Table;
import ru.icc.cells.debug.Debug;
import ru.icc.cells.utils.content.PageLayoutAlgorithm;
import ru.icc.cells.utils.processing.TextChunkProcessor;
import ru.icc.cells.utils.processing.TextChunkProcessorConfiguration;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Андрей on 24.10.2016.
 */
public class SimpleTableRecognizer extends AbstractTableRecognizer<Page> {

    TextChunkProcessorConfiguration cnf;
    List<Float> cols = new ArrayList<>(), rows = new ArrayList<>();

    public SimpleTableRecognizer(TextChunkProcessorConfiguration cnf) {
        this.cnf = cnf;
    }

    @Override
    public Table recognize(Page from) {
        List<TextBlock> blocks = getBlocks(from);
        List<Rectangle> vGaps  = PageLayoutAlgorithm.getVerticalGaps(blocks);
        List<Rectangle> hGaps  = PageLayoutAlgorithm.getHorizontalGaps(blocks);
        vGaps.sort((o1, o2) -> Float.compare(o1.getLeft(),o2.getLeft()));
        hGaps.sort((o1, o2) -> Float.compare(o2.getTop(),o1.getTop()));

        Rectangle first = vGaps.get(0);
        first = new Rectangle(first.getRight(), first.getBottom(), first.getRight(), first.getTop());
        Rectangle last = vGaps.get(vGaps.size() - 1);
        last = new Rectangle(last.getLeft(), last.getBottom(), last.getLeft(), last.getTop());
        vGaps = vGaps.stream()
                     .map(r -> new Rectangle((r.getLeft() + r.getRight()) / 2, r.getBottom(),
                                             (r.getLeft() + r.getRight()) / 2, r.getTop()))
                     .collect(Collectors.toList());
        vGaps.set(0, first);
        vGaps.set(vGaps.size() - 1, last);

        first = hGaps.get(0);
        first = new Rectangle(first.getLeft(), first.getBottom(), first.getRight(), first.getBottom());
        last = hGaps.get(hGaps.size() - 1);
        last = new Rectangle(last.getLeft(), last.getTop(), last.getRight(), last.getTop());
        hGaps = hGaps.stream()
                     .map(r -> new Rectangle(r.getLeft(), (r.getBottom() + r.getTop()) / 2,
                                             r.getRight() , (r.getBottom() + r.getTop()) / 2))
                     .collect(Collectors.toList());
        hGaps.set(0, first);
        hGaps.set(hGaps.size() - 1, last);

        List<Rectangle> allGaps = new ArrayList<>(vGaps);
        allGaps.addAll(hGaps);

        Debug.drawRects(allGaps);

        rows.addAll(hGaps.stream().map(Rectangle::getTop).collect(Collectors.toList()));
        cols.addAll(vGaps.stream().map(Rectangle::getLeft).collect(Collectors.toList()));
        cols.sort(Float::compareTo);
        rows.sort((o1, o2) -> o2.compareTo(o1));

        Table table = new Table(from.getLeft(), from.getBottom(), from.getRight(), from.getTop());

        for (int startRow = 0; startRow < rows.size() - 1; startRow++) {
            for (int startCol = 0; startCol < cols.size() - 1; startCol++) {
                int endRow = startRow, endCol = startCol;
                float top = rows.get(startRow), left = cols.get(startCol), bottom = rows.get(endRow + 1), right =
                        cols.get(endCol + 1);
                if (isPointsOnOneLine(left, top, right, top, allGaps) &&
                    isPointsOnOneLine(left, top, left, bottom, allGaps)) {
                    while (!isPointsOnOneLine(right, top, right, bottom, allGaps) && endCol < cols.size() - 2) {
                        right = cols.get(++endCol + 1);
                    }
                    while (!isPointsOnOneLine(left, bottom, right, bottom, allGaps) && endRow < rows.size() - 2) {
                        bottom = rows.get(++endRow + 1);
                    }
                    float finalBottom = bottom;
                    float finalRight  = right;
                    List<TextBlock> content = blocks.stream()
                                                    .filter(tb -> new Rectangle(left, finalBottom, finalRight,
                                                                                top).intersects(tb))
                                                    .collect(Collectors.toList());
                    Cell cell =
                            new Cell(startCol, left, bottom, right, top, endRow - startRow + 1, endCol - startCol + 1,
                                     content);
                    table.addCell(cell, startRow);
                }
            }
        }

        return table;
    }

    private List<TextBlock> getBlocks(Page page) {
        return new TextChunkProcessor(page, cnf).process();
    }

    private boolean isPointsOnOneLine(float x1, float y1, float x2, float y2, List<Rectangle> lines) {
        for (Rectangle line : lines) {
            double firstPoint =
                    Line2D.ptSegDist(line.getLeft(), line.getBottom(), line.getRight(), line.getTop(), x1, y1);
            double scondPoint =
                    Line2D.ptSegDist(line.getLeft(), line.getBottom(), line.getRight(), line.getTop(), x2, y2);
            if (Math.abs(firstPoint) < 1 && Math.abs(scondPoint) < 1) return true;
        }
        return false;
    }

}
