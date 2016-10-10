package ru.icc.cells.utils.processing;

import ru.icc.cells.common.Page;
import ru.icc.cells.common.Rectangle;
import ru.icc.cells.common.TextBlock;
import ru.icc.cells.common.TextChunk;
import ru.icc.cells.utils.processing.filter.ChunkFilter;
import ru.icc.cells.utils.processing.filter.bi.BiFilter;
import ru.icc.cells.utils.processing.filter.bi.HorizontalPositionBiFilter;
import ru.icc.cells.utils.processing.filter.tri.TriFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Андрей on 23.09.2016.
 */
public class TextChunkProcessor {
    private static final double HEIGHT_MULTIPLIER      = 1.0;
    private static final double SPACE_WIDTH_MULTIPLIER = 1.0;
    private Page                            page;
    private TextChunkProcessorConfiguration configuration;

    public TextChunkProcessor(Page page, TextChunkProcessorConfiguration configuration) {
        this.page = page;
        this.configuration = configuration;
    }

    public List<TextBlock> process() {
        List<TextChunk> chunks = new ArrayList<>(page.getOriginChunks());
        prepareChunks(chunks);
        return join(chunks);
    }

    private void prepareChunks(List<TextChunk> chunks) {
        for (int i = 0; i < chunks.size(); i++) {
            TextChunk chunk = chunks.get(i);
            if (chunk.getText().replaceAll("•", "").replaceAll(" ", "").replaceAll("_", "").length() == 0) {//\u2022
                chunks.remove(i--);
            }
        }
    }

    private List<TextBlock> join(List<TextChunk> chunks) {
        List<TextBlock> textBlocks = joinHorizontalChunks(chunks);
        int             diff       = textBlocks.size();
        while (diff != 0) {
            diff = textBlocks.size();
            textBlocks = joinHorizontalChunks(chunks);
            diff = diff - textBlocks.size();
        }
        textBlocks = joinVerticalLines(textBlocks);
        normalize(textBlocks);
        diff = textBlocks.size();
        while (diff != 0) {
            diff = textBlocks.size();
            textBlocks = joinVerticalLines(textBlocks);
            diff = diff - textBlocks.size();
        }
        return textBlocks;
    }

    private List<TextBlock> joinHorizontalChunks(List<TextChunk> chunks) {
        List<TextBlock> result = new ArrayList<>();

        TextBlock textBlock = null;
        join_process:
        for (int i = 0; i < chunks.size() - 1; i++) {
            if (textBlock == null) {
                textBlock = new TextBlock();
            }
            TextChunk leftChunk  = chunks.get(i);
            TextChunk rightChunk = chunks.get(i + 1);
            textBlock.add(leftChunk);
            List<BiFilter> horizontalBiFilters = getBiFilters(
                    biFilter -> biFilter.getOrientation() == ChunkFilter.Orientation.HORIZONTAL ||
                                biFilter.getOrientation() == ChunkFilter.Orientation.BOTH);
            for (BiFilter horizontalBiFilter : horizontalBiFilters) {
                if (!horizontalBiFilter.filter(leftChunk, rightChunk)) {
                    result.add(textBlock);
                    textBlock = null;
                    continue join_process;
                }
            }
            List<TriFilter> horizontalTriFilters = getTriFilters(
                    triFilter -> triFilter.getOrientation() == ChunkFilter.Orientation.HORIZONTAL ||
                                 triFilter.getOrientation() == ChunkFilter.Orientation.BOTH);
            for (TriFilter horizontalTriFilter : horizontalTriFilters) {
                if (horizontalTriFilter.getFilterType() == TriFilter.TriFilterType.AFTER) {
                    if (i + 2 < chunks.size() &&
                        !horizontalTriFilter.filter(leftChunk, rightChunk, chunks.get(i + 2))) {
                        result.add(textBlock);
                        textBlock = null;
                        continue join_process;
                    }
                } else if (horizontalTriFilter.getFilterType() == TriFilter.TriFilterType.BEFORE) {
                    if (i - 1 >= 0 && !horizontalTriFilter.filter(chunks.get(i - 1), leftChunk, rightChunk)) {
                        result.add(textBlock);
                        textBlock = null;
                        continue join_process;
                    }
                }
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

    private List<TextBlock> joinVerticalLines(List<TextBlock> textLines) {
        List<TextBlock> result    = new ArrayList<>();
        TextBlock       textBlock = null;
        join_process:
        for (int i = 0; i < textLines.size() - 1; i++) {
            if (textBlock == null) {
                textBlock = new TextBlock();
            }
            TextBlock firstBlock  = textLines.get(i);
            TextBlock secondBlock = textLines.get(i + 1);
            textBlock.add(firstBlock);

            List<BiFilter> verticalBiFilters = getBiFilters(
                    biFilter -> biFilter.getOrientation() == ChunkFilter.Orientation.VERTICAL ||
                                biFilter.getOrientation() == ChunkFilter.Orientation.BOTH);
            for (BiFilter verticalBiFilter : verticalBiFilters) {
                boolean filterResult;
                try { //if i only could get generic type
                    filterResult = verticalBiFilter.filter(firstBlock, secondBlock);
                } catch (ClassCastException ex) {
                    filterResult =
                            verticalBiFilter.filter(firstBlock.getChunks().get(firstBlock.getChunks().size() - 1),
                                                    secondBlock.getChunks().get(0));
                }
                if (!filterResult) {
                    result.add(textBlock);
                    textBlock = null;
                    continue join_process;
                }
            }
            List<TriFilter> verticalTriFilters = getTriFilters(
                    triFilter -> triFilter.getOrientation() == ChunkFilter.Orientation.VERTICAL ||
                                 triFilter.getOrientation() == ChunkFilter.Orientation.BOTH);
            for (TriFilter horizontalTriFilter : verticalTriFilters) {
                if (horizontalTriFilter.getFilterType() == TriFilter.TriFilterType.AFTER) {
                    if (i + 2 < textLines.size() &&
                        !horizontalTriFilter.filter(firstBlock, secondBlock, textLines.get(i + 2))) {
                        result.add(textBlock);
                        textBlock = null;
                        continue join_process;
                    }
                } else if (horizontalTriFilter.getFilterType() == TriFilter.TriFilterType.BEFORE) {
                    if (i - 1 >= 0 && !horizontalTriFilter.filter(textLines.get(i - 1), firstBlock, secondBlock)) {
                        result.add(textBlock);
                        textBlock = null;
                        continue join_process;
                    }
                }
            }
            TextChunk chunk = firstBlock.getChunks().get(firstBlock.getChunks().size() - 1);
            chunk.setText(chunk.getText() + System.lineSeparator());
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

    private List<BiFilter> getBiFilters(Predicate<BiFilter> predicate) {
        return configuration.getBiFilters().stream().filter(predicate).collect(Collectors.toList());
    }

    private List<TriFilter> getTriFilters(Predicate<TriFilter> predicate) {
        return configuration.getTriFilters().stream().filter(predicate).collect(Collectors.toList());
    }

    private void normalize(List<? extends Rectangle> data) {
        for (int i = 0; i < data.size() - 2; i++) {
            Rectangle left = data.get(i);
            for (int j = i + 2; j < data.size(); j++) {
                Rectangle right = data.get(j);
                if (((new HorizontalPositionBiFilter()).filter(left, right) /*&& (isVerticalPositionValid(left, right)
                        && isDistanceLessEqualsHeight(left, right))*/) && left.getRight() >= right.getLeft()) {
                    left.setRight(right.getLeft() - 5);
                }
            }
        }
    }
}
