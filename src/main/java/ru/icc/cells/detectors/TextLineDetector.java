package ru.icc.cells.detectors;

import ru.icc.cells.common.Rectangle;
import ru.icc.cells.common.TextBlock;
import ru.icc.cells.common.TextLine;
import ru.icc.cells.utils.content.PageLayoutAlgorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects text lines from text blocks
 */
class TextLineDetector implements Detector<TextLine, TextBlock> {
    @Override
    public List<TextLine> detect(List<TextBlock> textBlocks) {
        List<TextBlock> sortedTextBlocks = new ArrayList<>(textBlocks);
        sortedTextBlocks.sort(PageLayoutAlgorithm.RECTANGLE_COMPARATOR);
        List<TextLine> textLines     = new ArrayList<>();
        TextLine       textLine      = null;
        TextBlock      previousBlock = null;
        if (textBlocks.size() == 0) {
            return textLines;
        } else if (textBlocks.size() == 1) {
            textLine = new TextLine();
            textLine.add(textBlocks.get(0));
            textLines.add(textLine);
            return textLines;
        }
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
        if (textLine!=null&&!textLine.getTextBlocks().isEmpty()){
            projection = vProjection(textLine, lastBlock);
        }else {
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
    private boolean vProjection(Rectangle rectangle1, Rectangle rectangle2) {
        return Float.min(rectangle1.getTop(), rectangle2.getTop()) -
               Float.max(rectangle1.getBottom(), rectangle2.getBottom()) >= 0;
    }
}
