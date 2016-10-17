package ru.icc.cells.utils.processing.filter.bi;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import ru.icc.cells.common.Rectangle;
import ru.icc.cells.common.TextBlock;
import ru.icc.cells.common.TextChunk;

public class SpaceWidthBiFilter extends BiHeuristic<Rectangle> {
    private float spaceWidthMultiplier;

    public SpaceWidthBiFilter() {
        this(1f);
    }

    public SpaceWidthBiFilter(float spaceWidthMultiplier) {
        super(Orientation.HORIZONTAL);
        this.spaceWidthMultiplier = spaceWidthMultiplier;
    }

    @Override
    public boolean test(Rectangle first, Rectangle second) {
        if (first.getClass().equals(TextChunk.class) && second.getClass().equals(TextChunk.class)) {
            TextChunk firstChunk  = ((TextChunk) first);
            TextChunk secondChunk = ((TextChunk) second);
            if (firstChunk.getRight() >= secondChunk.getLeft()) return true;
            float spaceWidth;
            if (firstChunk.getText().contains(" ")) {
                spaceWidth = firstChunk.getCharSpaceWidth();
            } else {
                spaceWidth = new Chunk(' ', new Font(firstChunk.getChunkFont())).getWidthPoint();
                if (spaceWidth == 0) {
                    spaceWidth = 3;
                }
            }
            float chunkDistance = secondChunk.getLeft() - firstChunk.getRight();
            return chunkDistance <= spaceWidth * spaceWidthMultiplier;
        } else if (first.getClass().equals(TextBlock.class) && second.getClass().equals(TextBlock.class)) {
            TextBlock firstBlock  = ((TextBlock) first);
            TextBlock secondBlock = ((TextBlock) second);
            if (firstBlock.getRight() >= secondBlock.getLeft()) return true;
            float spaceWidth;
            if (firstBlock.getText().contains(" ")) {
                spaceWidth = firstBlock.getChunks().get(firstBlock.getChunks().size() - 1).getCharSpaceWidth();
            } else {
                spaceWidth = new Chunk(' ', new Font(
                        firstBlock.getChunks().get(firstBlock.getChunks().size() - 1).getChunkFont())).getWidthPoint();
                if (spaceWidth == 0) {
                    spaceWidth = 3;
                }
            }
            float chunkDistance = secondBlock.getLeft() - firstBlock.getRight();
            return chunkDistance <= spaceWidth * spaceWidthMultiplier || (firstBlock.getText().matches("\\d+\\.\\s*"));
        }
        return true;
    }
}
