package ru.icc.cells.tabbypdf.recognizers;

import ru.icc.cells.tabbypdf.common.Page;
import ru.icc.cells.tabbypdf.common.Rectangle;
import ru.icc.cells.tabbypdf.common.TextBlock;
import ru.icc.cells.tabbypdf.common.table.Cell;
import ru.icc.cells.tabbypdf.common.table.Table;
import ru.icc.cells.tabbypdf.utils.content.PageLayoutAlgorithm;
import ru.icc.cells.tabbypdf.utils.processing.TextChunkProcessor;
import ru.icc.cells.tabbypdf.utils.processing.TextChunkProcessorConfiguration;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.awt.geom.Line2D.ptSegDist;

/**
 * Created by Андрей on 24.10.2016.
 */
public class SimpleTableRecognizer extends AbstractTableRecognizer<Page> {

    private TextChunkProcessorConfiguration cnf;
    private List<Double>                    cols = new ArrayList<>();
    private List<Double>                    rows = new ArrayList<>();

    public SimpleTableRecognizer(TextChunkProcessorConfiguration cnf) {
        this.cnf = cnf;
    }

    @Override
    public Table recognize(Page from) {
        List<TextBlock> blocks = getBlocks(from);
        List<Rectangle> vGaps = PageLayoutAlgorithm.getVerticalGaps(blocks);
        List<Rectangle> hGaps = PageLayoutAlgorithm.getHorizontalGaps(blocks);
        vGaps.sort(Comparator.comparingDouble(Rectangle::getLeft));
        hGaps.sort(Comparator.comparingDouble(Rectangle::getTop).reversed());

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
            .map(r -> new Rectangle(r.getLeft(), (r.getBottom() + r.getTop()) / 2, r.getRight(),
                (r.getBottom() + r.getTop()) / 2))
            .collect(Collectors.toList());
        hGaps.set(0, first);
        hGaps.set(hGaps.size() - 1, last);

        List<Rectangle> allGaps = new ArrayList<>(vGaps);
        allGaps.addAll(hGaps);

        rows.addAll(hGaps.stream().map(Rectangle::getTop).collect(Collectors.toList()));
        cols.addAll(vGaps.stream().map(Rectangle::getLeft).collect(Collectors.toList()));
        cols.sort(Double::compareTo);
        rows.sort(Comparator.reverseOrder());

        Table table = new Table(from.getLeft(), from.getBottom(), from.getRight(), from.getTop());

        for (int startRow = 0; startRow < rows.size() - 1; startRow++) {
            for (int startCol = 0; startCol < cols.size() - 1; startCol++) {
                int endRow = startRow, endCol = startCol;
                double top = rows.get(startRow), left = cols.get(startCol), bottom = rows.get(endRow + 1), right =
                    cols.get(endCol + 1);
                if (isPointsOnOneLine(left, top, right, top, allGaps) &&
                    isPointsOnOneLine(left, top, left, bottom, allGaps)) {
                    while (!isPointsOnOneLine(right, top, right, bottom, allGaps) && endCol < cols.size() - 2) {
                        right = cols.get(++endCol + 1);
                    }
                    while (!isPointsOnOneLine(left, bottom, right, bottom, allGaps) && endRow < rows.size() - 2) {
                        bottom = rows.get(++endRow + 1);
                    }
                    double finalBottom = bottom;
                    double finalRight = right;
                    List<TextBlock> content = blocks.stream()
                        .filter(tb -> new Rectangle(left, finalBottom, finalRight, top).intersects(tb))
                        .collect(Collectors.toList());

                    Cell cell = new Cell(
                        startCol,
                        left,
                        bottom,
                        right,
                        top,
                        endRow - startRow + 1,
                        endCol - startCol + 1,
                        content
                    );
                    table.addCell(cell, startRow);
                }
            }
        }
        return table;
    }

    private List<TextBlock> getBlocks(Page page) {
        return new TextChunkProcessor(page, cnf).process();
    }

    private static boolean isPointsOnOneLine(double x1, double y1, double x2, double y2, List<Rectangle> lines) {
        return lines.stream().anyMatch(line -> {
            double firstPoint  = ptSegDist(line.getLeft(), line.getBottom(), line.getRight(), line.getTop(), x1, y1);
            double secondPoint = ptSegDist(line.getLeft(), line.getBottom(), line.getRight(), line.getTop(), x2, y2);
            return Math.abs(firstPoint) < 1 && Math.abs(secondPoint) < 1;
        });
    }

}
