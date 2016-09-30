package ru.icc.cells.utils;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.parser.Vector;
import ru.icc.cells.common.Page;
import ru.icc.cells.common.Rectangle;
import ru.icc.cells.common.TextBlock;
import ru.icc.cells.common.TextChunk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Андрей on 23.09.2016.
 */
public class TextChunkProcessor {
    private static final double HEIGHT_MULTIPLIER      = 1.0;
    private static final double SPACE_WIDTH_MULTIPLIER = 1.1;
    private Page page;

    public TextChunkProcessor(Page page) {
        this.page = page;
    }

    public List<TextBlock> process() {
        List<TextChunk> chunks = new ArrayList<>(page.getOriginChunks());
        prepareChunks(chunks);
        return join(chunks);
    }

    private void prepareChunks(List<TextChunk> chunks) {
        for (int i = 0; i < chunks.size(); i++) {
            TextChunk chunk = chunks.get(i);
            if (chunk.getText().replaceAll("•", "").replaceAll(" ", "").length() == 0) {//\u2022
                chunks.remove(i--);
            }
        }
    }

    private List<TextBlock> join(List<TextChunk> chunks) {
        List<TextBlock> textBlocks = joinHorizontalChunks(chunks);
        int diff = textBlocks.size();
        while (diff!=0) {
            diff = textBlocks.size();
            textBlocks = joinHorizontalChunks(chunks);
            diff = diff - textBlocks.size();
        }
        textBlocks = joinVerticalLines(textBlocks);
        normalize(textBlocks);
        diff = textBlocks.size();
        while (diff!=0) {
            diff = textBlocks.size();
            textBlocks = joinVerticalLines(textBlocks);
            diff = diff - textBlocks.size();
        }

        for (int i = 0; i < textBlocks.size(); i++) {
            textBlocks.get(i).setOrder(i);
        }
        return textBlocks;
    }

    private List<TextBlock> joinHorizontalChunks(List<TextChunk> chunks) {
        List<TextBlock> result = new ArrayList<>();

        TextBlock textBlock = null;
        for (int i = 0; i < chunks.size() - 1; i++) {
            if (textBlock == null) {
                textBlock = new TextBlock();
            }
            TextChunk leftChunk  = chunks.get(i);
            TextChunk rightChunk = chunks.get(i + 1);
            textBlock.add(leftChunk);
            if (!isHorizontalPositionValid(leftChunk, rightChunk)) {
                result.add(textBlock);
                textBlock = null;
                continue;
            }
            if (!isDistanceLessEqualsSpaceLength(leftChunk, rightChunk)) {
                result.add(textBlock);
                textBlock = null;
                continue;
            }
            if (isThereLinesBetweenChunks(leftChunk, rightChunk, true)) {
                result.add(textBlock);
                textBlock = null;
                continue;
            }
        }

        if (textBlock == null) {
            textBlock = new TextBlock();
            textBlock.add(chunks.get(chunks.size() - 1));
            result.add(textBlock);
        } else {
            textBlock.add(chunks.get(chunks.size() - 1));
            result.add(textBlock);
        }
        return result;
    }

    /**
     * Checks whether firstRect is on the left of secondRect
     */
    private static <T extends Rectangle> boolean isHorizontalPositionValid(T firstRect, T secondRect) {
        float lx1 = firstRect.getLeft();
        float lx2 = secondRect.getLeft();
        float ty1 = firstRect.getTop();
        float ty2 = secondRect.getTop();
        float by1 = firstRect.getBottom();
        float by2 = secondRect.getBottom();
        return (lx1 <= lx2) && (ty1 >= by2) && (by1 <= ty2);
        //        return !(((rx1 > lx2) && (rx1 > rx2)) || (ty1 <= by2) || (by1 >= ty2));
    }

    /**
     * Checks whether distance between chunks is less or equals than space width
     */
    private boolean isDistanceLessEqualsSpaceLength(TextChunk leftChunk, TextChunk rightChunk) {
        if (leftChunk.getRight() >= rightChunk.getLeft()) return true;
        /* Creating a chunk with only " " content and taking its width */
        float spaceWidth    = new Chunk(' ', new Font(leftChunk.getChunkFont())).getWidthPoint();
        float chunkDistance = rightChunk.getLeft() - leftChunk.getRight();
        return chunkDistance <= spaceWidth * SPACE_WIDTH_MULTIPLIER;
    }

    private <T extends Rectangle> boolean isThereLinesBetweenChunks(T firstRect, T secondRect, boolean linesVertical) {
        return page.getRulings().stream().filter(line -> {
            float   rx1 = firstRect.getRight();
            float   lx2 = secondRect.getLeft();
            float   lx1 = firstRect.getLeft();
            float   rx2 = secondRect.getRight();
            float   ty1 = firstRect.getTop();
            float   ty2 = secondRect.getTop();
            float   by1 = firstRect.getBottom();
            float   by2 = secondRect.getBottom();
            double  lx  = Double.min(line.getStartLocation().getX(), line.getEndLocation().getX());
            double  rx  = Double.max(line.getStartLocation().getX(), line.getEndLocation().getX());
            double  by  = Double.min(line.getStartLocation().getY(), line.getEndLocation().getY());
            double  ty  = Double.max(line.getStartLocation().getY(), line.getEndLocation().getY());
            boolean isBetweenChunks;
            if (linesVertical) {
                boolean vertical = (lx == rx);
                isBetweenChunks = lx >= rx1 && lx <= lx2 && ty >= Float.max(by1, by2) && by >= Float.min(ty1, ty2);
                return vertical && isBetweenChunks;
            } else {
                boolean horizontal = (ty == by);
                isBetweenChunks = ty <= by1 && ty >= ty2 && lx <= Float.max(rx1, rx2) && rx >= Float.min(lx1, lx2);
                return horizontal && isBetweenChunks;
            }
        }).count() > 0;
    }

    private List<TextBlock> joinVerticalLines(List<TextBlock> textLines) {
        List<TextBlock> result    = new ArrayList<>();
        TextBlock       textBlock = null;

        for (int i = 0; i < textLines.size() - 1; i++) {
            if (textBlock == null) {
                textBlock = new TextBlock();
            }
            TextBlock firstChunk  = textLines.get(i);
            TextBlock secondChunk = textLines.get(i + 1);
            textBlock.add(firstChunk);
            if (!isVerticalPositionValid(firstChunk, secondChunk)) {
                result.add(textBlock);
                textBlock = null;
                continue;
            }
            if (!isDistanceLessEqualsHeight(firstChunk, secondChunk)) {
                result.add(textBlock);
                textBlock = null;
                continue;
            }
            if (i + 2 < textLines.size() && isHorizontalPositionValid(secondChunk, textLines.get(i + 2)) &&
                firstChunk.getRight() >= textLines.get(i + 2).getLeft()) {
                result.add(textBlock);
                textBlock = null;
                continue;
            }
            if (isThereLinesBetweenChunks(firstChunk, secondChunk, false)) {
                result.add(textBlock);
                textBlock = null;
                continue;
            }
        }
        if (textBlock == null) {
            textBlock = new TextBlock();
            textBlock.add(textLines.get(textLines.size() - 1));
            result.add(textBlock);
        } else {
            textBlock.add(textLines.get(textLines.size() - 1));
            result.add(textBlock);
        }
        return result;
    }

    /**
     * Checks whether firstRect is on the top of secondRect
     */
    private <T extends Rectangle> boolean isVerticalPositionValid(T firstRect, T secondRect) {
        float lx1 = firstRect.getLeft();
        float rx1 = firstRect.getRight();
        float lx2 = secondRect.getLeft();
        float rx2 = secondRect.getRight();
        float ty1 = firstRect.getTop();
        float ty2 = secondRect.getTop();
        float by1 = firstRect.getBottom();
        return (rx1 >= lx2) && (lx1 <= rx2) && (ty1 >= ty2);
        //        return !((rx1 <= lx2) || (lx1 >= rx2) || (ty1 < ty2));
    }

    /**
     * Checks whether distance between chunks is less or equals than height of secondRect
     */
    private <T extends Rectangle> boolean isDistanceLessEqualsHeight(T firstRect, T secondRect) {
        float height = (float) (Math.abs(secondRect.getTop() - secondRect.getBottom()) *
                                HEIGHT_MULTIPLIER);
        float distance = Math.abs(((firstRect.getTop() > secondRect.getTop()) ?
                                   firstRect.getBottom() :
                                   firstRect.getTop()) - secondRect.getTop());
        return distance <= height;
    }

    private void normalize(List<? extends Rectangle> data) {
        for (int i = 0; i < data.size() - 2; i++) {
            Rectangle left = data.get(i);
            for (int j = i + 2; j < data.size(); j++) {
                Rectangle right = data.get(j);
                if ((isHorizontalPositionValid(left, right) /*&& (isVerticalPositionValid(left, right)
                        && isDistanceLessEqualsHeight(left, right))*/) &&
                    left.getRight() >= right.getLeft()) {
                    left.setRight(right.getLeft() - 5);
                }
            }
        }
    }
}
