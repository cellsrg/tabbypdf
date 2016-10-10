package ru.icc.cells.utils.processing.filter.bi;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import ru.icc.cells.common.TextChunk;

public class SpaceWidthBiFilter extends BiFilter<TextChunk> {
    private float spaceWidthMultiplier;

    public SpaceWidthBiFilter() {
        this(1f);
    }

    public SpaceWidthBiFilter(float spaceWidthMultiplier) {
        super(Orientation.HORIZONTAL);
        this.spaceWidthMultiplier = spaceWidthMultiplier;
    }

    @Override
    public boolean filter(TextChunk first, TextChunk second) {
        if (first.getRight() >= second.getLeft()) return true;
        /* Creating a chunk with only " " content and taking its width */
        //        float spaceWidth    = leftChunk.getCharSpaceWidth();
        float spaceWidth    = new Chunk(' ', new Font(first.getChunkFont())).getWidthPoint();
        float chunkDistance = second.getLeft() - first.getRight();
        return chunkDistance <= spaceWidth * spaceWidthMultiplier;
    }
}
