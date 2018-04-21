package ru.icc.cells.tabbypdf.utils.processing;

import ru.icc.cells.tabbypdf.common.Page;
import ru.icc.cells.tabbypdf.common.Rectangle;
import ru.icc.cells.tabbypdf.common.TextBlock;
import ru.icc.cells.tabbypdf.common.TextChunk;
import ru.icc.cells.tabbypdf.utils.processing.filter.Heuristic;
import ru.icc.cells.tabbypdf.utils.processing.filter.bi.BiHeuristic;
import ru.icc.cells.tabbypdf.utils.processing.filter.bi.HorizontalPositionBiHeuristic;
import ru.icc.cells.tabbypdf.utils.processing.filter.tri.TriHeuristic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.Double.min;

public class TextChunkProcessor {
    private Page                            page;
    private TextChunkProcessorConfiguration cnf;
    private List<BiHeuristic>               horizontalBiHeuristics;
    private List<TriHeuristic>              horizontalTriHeuristics;
    private List<BiHeuristic>               verticalBiHeuristics;
    private List<TriHeuristic>              verticalTriHeuristics;

    public TextChunkProcessor(Page page, TextChunkProcessorConfiguration cnf) {
        this.page = page;
        this.cnf = cnf;
        horizontalBiHeuristics = getBiHeuristics(
            biHeuristic -> biHeuristic.getOrientation() == Heuristic.Orientation.HORIZONTAL
                || biHeuristic.getOrientation() == Heuristic.Orientation.BOTH
        );
        horizontalTriHeuristics = getTriHeuristics(
            triHeuristic -> triHeuristic.getOrientation() == Heuristic.Orientation.HORIZONTAL
                || triHeuristic.getOrientation() == Heuristic.Orientation.BOTH
        );
        verticalBiHeuristics = getBiHeuristics(
            biHeuristic -> biHeuristic.getOrientation() == Heuristic.Orientation.VERTICAL
                || biHeuristic.getOrientation() == Heuristic.Orientation.BOTH
        );
        verticalTriHeuristics = getTriHeuristics(
            triHeuristic -> triHeuristic.getOrientation() == Heuristic.Orientation.VERTICAL
                || triHeuristic.getOrientation() == Heuristic.Orientation.BOTH
        );
    }

    public List<TextBlock> process() {
        List<TextBlock> chunks = page
            .getWordChunks()
            .stream()
            .map(chunk -> {
                TextBlock block = new TextBlock();
                block.add(chunk);
                return block;
            })
            .collect(Collectors.toList());
        prepareChunks(chunks);
        List<TextBlock> blocks = join(chunks);
        if (cnf.isRemoveColons()) {
            removeColons(blocks);
        }

        List<TextBlock> iterateBlocks = new ArrayList<>(blocks);
        for (int i = 0; i < iterateBlocks.size(); i++) {
            for (int j = i + 1; j < iterateBlocks.size(); j++) {
                if (iterateBlocks.get(i).intersects(iterateBlocks.get(j))) {
                    iterateBlocks.get(i).add(iterateBlocks.get(j));
                    blocks.remove(iterateBlocks.get(j));
                }
            }
        }

        return blocks;
    }

    private void prepareChunks(List<TextBlock> chunks) {
        Iterator<TextBlock> chunkIterator = chunks.iterator();
        for (TextBlock chunk = chunkIterator.next(); chunkIterator.hasNext(); chunk = chunkIterator.next()) {
            String chunkText = chunk.getText();
            for (String strToReplace : cnf.getStringsToReplace()) {
                if (!strToReplace.equals(" ") && !chunkText.equals(" ")) {
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
            for (String strToReplace : cnf.getStringsToReplace()) {
                chunkText = chunkText.replaceAll(strToReplace, "");
            }
            if (chunkText.isEmpty()) {
                blockIterator.remove();
            }
        }
    }

    private List<TextBlock> join(List<TextBlock> chunks) {
        List<TextBlock> textBlocks = joinBlocks(chunks, horizontalBiHeuristics, horizontalTriHeuristics, false);
        int diff = textBlocks.size();
        while (diff != 0) {
            diff = textBlocks.size();
            textBlocks = joinBlocks(textBlocks, horizontalBiHeuristics, horizontalTriHeuristics, false);
            diff = diff - textBlocks.size();
        }
        prepareBlocks(textBlocks);
        normalize(textBlocks);
        diff = textBlocks.size();
        while (diff != 0) {
            diff = textBlocks.size();
            textBlocks = joinBlocks(textBlocks, verticalBiHeuristics, verticalTriHeuristics, true);
            diff = diff - textBlocks.size();
        }
        return textBlocks;
    }


    private List<TextBlock> joinBlocks(List<TextBlock> blocks, List<BiHeuristic> biHeuristics,
                                       List<TriHeuristic> triHeuristics, boolean isVertical) {

        List<TextBlock> result = new ArrayList<>();
        TextBlock textBlock = null;

        join_process:
        for (int i = 0; i < blocks.size() - 1; i++) {
            if (textBlock == null) {
                textBlock = new TextBlock();
            }
            TextBlock firstBlock = blocks.get(i);
            TextBlock secondBlock = blocks.get(i + 1);
            String text = firstBlock.getChunks().get(0).getText();
            text = (isVertical ? "\n" : "") + text;
            firstBlock.getChunks().get(0).setText(text);
            textBlock.add(firstBlock);

            for (BiHeuristic biHeuristic : biHeuristics) {
                boolean heuristicResult = true;
                if (biHeuristic.getTargetClass().equals(Rectangle.class) ||
                    biHeuristic.getTargetClass().equals(TextBlock.class)) {
                    heuristicResult = biHeuristic.test(firstBlock, secondBlock);
                } else if (biHeuristic.getTargetClass().equals(TextChunk.class)) {
                    heuristicResult = biHeuristic.test(firstBlock.getChunks().get(firstBlock.getChunks().size() - 1),
                        secondBlock.getChunks().get(0));
                } else {
                    System.out.println(biHeuristic.getTargetClass());
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

    private List<BiHeuristic> getBiHeuristics(Predicate<BiHeuristic> predicate) {
        return cnf.getBiHeuristics().stream().filter(predicate).collect(Collectors.toList());
    }

    private List<TriHeuristic> getTriHeuristics(Predicate<TriHeuristic> predicate) {
        return cnf.getTriHeuristics().stream().filter(predicate).collect(Collectors.toList());
    }

    private void normalize(List<? extends Rectangle> data) {
        for (int i = 0; i < data.size() - 2; i++) {
            Rectangle left = data.get(i);
            for (int j = i + 2; j < data.size(); j++) {
                Rectangle right = data.get(j);
                if (new HorizontalPositionBiHeuristic().test(left, right) && left.getRight() >= right.getLeft()) {
                    left.setRight(right.getLeft() - 5);
                }
            }
        }
    }

    private void removeColons(List<TextBlock> blocks) {
        TextBlock lowestBlock  = Collections.min(blocks, Comparator.comparingDouble(Rectangle::getBottom));
        TextBlock highestBlock = Collections.max(blocks, Comparator.comparingDouble(Rectangle::getBottom));
        List<TextBlock> colonBlocks = getBlocksOnSameLine(blocks, lowestBlock);
        // colonBlocks.addAll(getBlocksOnSameLine(blocks, highestBlock));
        colonBlocks.add(lowestBlock);
        // colonBlocks.add(highestBlock);
        blocks.removeAll(colonBlocks);
    }

    public static List<TextBlock> getBlocksOnSameLine(List<TextBlock> blocks, TextBlock as) {
        return blocks.stream()
            .filter(block -> min(block.getTop(), as.getTop()) - Double.max(block.getBottom(), as.getBottom()) > 0)
            .collect(Collectors.toList());
    }
}
