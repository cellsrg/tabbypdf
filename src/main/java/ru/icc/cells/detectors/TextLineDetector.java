package ru.icc.cells.detectors;

import ru.icc.cells.common.Rectangle;
import ru.icc.cells.common.TextBlock;
import ru.icc.cells.common.TextLine;
import ru.icc.cells.utils.content.PageLayoutAlgorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Андрей on 03.10.2016.
 */
public class TextLineDetector implements Detector<TextLine> {
    @Override
    public List<TextLine> detect(List<? extends Rectangle> textBlocks) {
        List<TextLine> textLines     = new ArrayList<>();
        TextLine       textLine      = null;
        TextBlock      previousBlock = null;
        for (int i = 1; i < textBlocks.size(); i++) {
            if (textLine == null) {
                textLine = new TextLine();
            }
            previousBlock = (TextBlock) textBlocks.get(i - 1);
            TextBlock currentBlock = (TextBlock) textBlocks.get(i);
            textLine.add(previousBlock);
            if (!vProjection(previousBlock, currentBlock)) {
                textLine.addGaps(PageLayoutAlgorithm.getVerticalGaps(textLine.getTextBlocks()));
                textLines.add(textLine);
                textLine = null;
            }
        }
        TextBlock lastBlock = (TextBlock) textBlocks.get(textBlocks.size() - 1);
        if (vProjection(previousBlock, lastBlock)) {
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

    private boolean vProjection(Rectangle rectangle1, Rectangle rectangle2){
        return Float.min(rectangle1.getTop(), rectangle2.getTop()) -
               Float.max(rectangle1.getBottom(), rectangle2.getBottom()) > 0;
    }
}
