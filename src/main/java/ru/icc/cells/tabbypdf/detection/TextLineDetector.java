package ru.icc.cells.tabbypdf.detection;

import lombok.AllArgsConstructor;
import ru.icc.cells.tabbypdf.entities.Rectangle;
import ru.icc.cells.tabbypdf.entities.TextBlock;
import ru.icc.cells.tabbypdf.entities.TextLine;
import ru.icc.cells.tabbypdf.utils.content.PageLayoutAlgorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects text lines from text blocks
 */
@AllArgsConstructor
class TextLineDetector implements Detector<TextLine, TextBlock> {
    boolean useSortedTextBlocks;

    @Override
    public List<TextLine> detect(List<TextBlock> textBlocks) {
        if (textBlocks.size() == 0) {
            return new ArrayList<>();
        } else if (textBlocks.size() == 1) {
            TextLine temp = new TextLine();
            temp.add(textBlocks.get(0));
            return new ArrayList<TextLine>() {{
                add(temp);
            }};
        }

        List<TextBlock> sortedTextBlocks = new ArrayList<>(textBlocks);
        if (useSortedTextBlocks) {
            sortedTextBlocks.sort(PageLayoutAlgorithm.RECTANGLE_COMPARATOR);
        }

        List<TextLine> textLines = new ArrayList<>();
        TextLine textLine = null;
        TextBlock previousBlock = null;

        for (int i = 1; i < sortedTextBlocks.size(); i++) {
            if (textLine == null) {
                textLine = new TextLine();
            }
            previousBlock = sortedTextBlocks.get(i - 1);
            TextBlock currentBlock = sortedTextBlocks.get(i);
            textLine.add(previousBlock);
            if (!vProjection(textLine, currentBlock)) {
                textLine.addGaps(PageLayoutAlgorithm.getVerticalGaps(textLine.getTextBlocks()));
                textLines.add(textLine);
                textLine = null;
            }
        }
        TextBlock lastBlock = sortedTextBlocks.get(sortedTextBlocks.size() - 1);

        boolean projection;
        if (textLine != null && !textLine.getTextBlocks().isEmpty()) {
            projection = vProjection(textLine, lastBlock);
        } else {
            projection = vProjection(previousBlock, lastBlock);
        }

        if (projection) {
            textLine.add(lastBlock);
            textLine.addGaps(PageLayoutAlgorithm.getVerticalGaps(textLine.getTextBlocks()));
            textLines.add(textLine);
        } else {
            textLine = new TextLine();
            textLine.add(lastBlock);
            textLine.addGaps(PageLayoutAlgorithm.getVerticalGaps(textLine.getTextBlocks()));
            textLines.add(textLine);
        }
        return textLines;
    }

    /**
     * checks if rectangles have intersection of their Y-projections
     */
    public static boolean vProjection(Rectangle rectangle1, Rectangle rectangle2) {
        return Double.min(rectangle1.getTop(), rectangle2.getTop())
             - Double.max(rectangle1.getBottom(), rectangle2.getBottom()) >= 0;
    }
}
