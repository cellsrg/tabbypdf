package ru.icc.cells.utils.processing;

import ru.icc.cells.common.Page;
import ru.icc.cells.common.Rectangle;
import ru.icc.cells.common.TextBlock;
import ru.icc.cells.common.TextChunk;
import ru.icc.cells.utils.processing.filter.Heuristic;
import ru.icc.cells.utils.processing.filter.bi.BiHeuristic;
import ru.icc.cells.utils.processing.filter.bi.HorizontalPositionBiHeuristic;
import ru.icc.cells.utils.processing.filter.tri.TriHeuristic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Андрей on 23.09.2016.
 */
public class TextChunkProcessor {
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
        Iterator<TextChunk> chunkIterator = chunks.iterator();
        for (TextChunk chunk = chunkIterator.next(); chunkIterator.hasNext(); chunk = chunkIterator.next()) {
            String chunkText = chunk.getText();
            for (String strToReplace : configuration.getStringsToReplace()) {
                if (!strToReplace.equals(" ")) {
                    chunkText = chunkText.replaceAll(strToReplace, "");
                }
            }
            if (chunkText.isEmpty()) {
                chunkIterator.remove();
            }
        }
    }

    private void prepareBlocks(List<TextBlock> blocks) {
        Iterator<TextBlock> blockIterator = blocks.iterator();
        for (TextBlock block = blockIterator.next(); blockIterator.hasNext(); block = blockIterator.next()) {
            String chunkText = block.getText();
            for (String strToReplace : configuration.getStringsToReplace()) {
                chunkText = chunkText.replaceAll(strToReplace, "");
            }
            if (chunkText.isEmpty()) {
                blockIterator.remove();
            }
        }
    }

    private List<TextBlock> join(List<TextChunk> chunks) {
        List<TextBlock> textBlocks = joinHorizontalChunks(chunks);
        int             diff       = textBlocks.size();
        List<BiHeuristic> horizontalBiHeuristics = getBiHeuristics(
                biHeuristic -> biHeuristic.getOrientation() == Heuristic.Orientation.HORIZONTAL ||
                               biHeuristic.getOrientation() == Heuristic.Orientation.BOTH);
        List<TriHeuristic> horizontalTriHeuristics = getTriHeuristics(
                triHeuristic -> triHeuristic.getOrientation() == Heuristic.Orientation.HORIZONTAL ||
                                triHeuristic.getOrientation() == Heuristic.Orientation.BOTH);
        List<BiHeuristic> verticalBiHeuristics = getBiHeuristics(
                biHeuristic -> biHeuristic.getOrientation() == Heuristic.Orientation.VERTICAL ||
                               biHeuristic.getOrientation() == Heuristic.Orientation.BOTH);
        List<TriHeuristic> verticalTriHeuristics = getTriHeuristics(
                triHeuristic -> triHeuristic.getOrientation() == Heuristic.Orientation.VERTICAL ||
                                triHeuristic.getOrientation() == Heuristic.Orientation.BOTH);
        while (diff != 0) {
            diff = textBlocks.size();
            textBlocks = joinBlocks(textBlocks, horizontalBiHeuristics, horizontalTriHeuristics);
            diff = diff - textBlocks.size();
        }
        prepareBlocks(textBlocks);
        normalize(textBlocks);
        diff = textBlocks.size();
        while (diff != 0) {
            diff = textBlocks.size();
            textBlocks = joinBlocks(textBlocks, verticalBiHeuristics, verticalTriHeuristics);
            diff = diff - textBlocks.size();
        }
        return textBlocks;
    }


    private List<TextBlock> joinBlocks(List<TextBlock> blocks, List<BiHeuristic> biHeuristics,
                                       List<TriHeuristic> triHeuristics) {

        List<TextBlock> result    = new ArrayList<>();
        TextBlock       textBlock = null;

        join_process:
        for (int i = 0; i < blocks.size() - 1; i++) {
            if (textBlock == null) {
                textBlock = new TextBlock();
            }
            TextBlock firstBlock  = blocks.get(i);
            TextBlock secondBlock = blocks.get(i + 1);
            textBlock.add(firstBlock);

            for (BiHeuristic biHeuristic : biHeuristics) {
                boolean heuristicResult;
                try { //if i only could get generic type
                    heuristicResult = biHeuristic.test(firstBlock, secondBlock);
                } catch (ClassCastException ex) {
                    heuristicResult = biHeuristic.test(firstBlock.getChunks().get(firstBlock.getChunks().size() - 1),
                                                       secondBlock.getChunks().get(0));
                }
                if (!heuristicResult) {
                    result.add(textBlock);
                    textBlock = null;
                    continue join_process;
                }
            }
            for (TriHeuristic triHeuristic : triHeuristics) {
                if (triHeuristic.getHeuristicType() == TriHeuristic.TriHeuristicType.AFTER) {
                    if (i + 2 < blocks.size() && !triHeuristic.test(firstBlock, secondBlock, blocks.get(i + 2))) {
                        result.add(textBlock);
                        textBlock = null;
                        continue join_process;
                    }
                } else if (triHeuristic.getHeuristicType() == TriHeuristic.TriHeuristicType.BEFORE) {
                    if (i - 1 >= 0 && !triHeuristic.test(blocks.get(i - 1), firstBlock, secondBlock)) {
                        result.add(textBlock);
                        textBlock = null;
                        continue join_process;
                    }
                }
            }
        }
        if (textBlock == null) {
            textBlock = new TextBlock();
            textBlock.add(blocks.get(blocks.size() - 1));
            result.add(textBlock);
        } else {
            textBlock.add(blocks.get(blocks.size() - 1));
            result.add(textBlock);
        }
        return result;
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
            List<BiHeuristic> horizontalBiHeuristics = getBiHeuristics(
                    biHeuristic -> biHeuristic.getOrientation() == Heuristic.Orientation.HORIZONTAL ||
                                   biHeuristic.getOrientation() == Heuristic.Orientation.BOTH);
            for (BiHeuristic horizontalBiHeuristic : horizontalBiHeuristics) {
                if (!horizontalBiHeuristic.test(leftChunk, rightChunk)) {
                    result.add(textBlock);
                    textBlock = null;
                    continue join_process;
                }
            }
            List<TriHeuristic> horizontalTriHeuristics = getTriHeuristics(
                    triHeuristic -> triHeuristic.getOrientation() == Heuristic.Orientation.HORIZONTAL ||
                                    triHeuristic.getOrientation() == Heuristic.Orientation.BOTH);
            for (TriHeuristic horizontalTriHeuristic : horizontalTriHeuristics) {
                if (horizontalTriHeuristic.getHeuristicType() == TriHeuristic.TriHeuristicType.AFTER) {
                    if (i + 2 < chunks.size() &&
                        !horizontalTriHeuristic.test(leftChunk, rightChunk, chunks.get(i + 2))) {
                        result.add(textBlock);
                        textBlock = null;
                        continue join_process;
                    }
                } else if (horizontalTriHeuristic.getHeuristicType() == TriHeuristic.TriHeuristicType.BEFORE) {
                    if (i - 1 >= 0 && !horizontalTriHeuristic.test(chunks.get(i - 1), leftChunk, rightChunk)) {
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

    private List<BiHeuristic> getBiHeuristics(Predicate<BiHeuristic> predicate) {
        return configuration.getBiHeuristics().stream().filter(predicate).collect(Collectors.toList());
    }

    private List<TriHeuristic> getTriHeuristics(Predicate<TriHeuristic> predicate) {
        return configuration.getTriHeuristics().stream().filter(predicate).collect(Collectors.toList());
    }

    private void normalize(List<? extends Rectangle> data) {
        for (int i = 0; i < data.size() - 2; i++) {
            Rectangle left = data.get(i);
            for (int j = i + 2; j < data.size(); j++) {
                Rectangle right = data.get(j);
                if (((new HorizontalPositionBiHeuristic()).test(left, right) /*&& (isVerticalPositionValid(left, right)
                        && isDistanceLessEqualsHeight(left, right))*/) && left.getRight() >= right.getLeft()) {
                    left.setRight(right.getLeft() - 5);
                }
            }
        }
    }
}
