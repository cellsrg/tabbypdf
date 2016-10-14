package ru.icc.cells.utils.processing.filter.bi;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import ru.icc.cells.common.TextChunk;

public class SpaceWidthBiFilter extends BiHeuristic<TextChunk> {
    private float spaceWidthMultiplier;

    public SpaceWidthBiFilter() {
        this(1f);
    }

    public SpaceWidthBiFilter(float spaceWidthMultiplier) {
        super(Orientation.HORIZONTAL);
        this.spaceWidthMultiplier = spaceWidthMultiplier;
    }

    @Override
    public boolean test(TextChunk first, TextChunk second) {
        if (first.getRight() >= second.getLeft()) return true;
        float spaceWidth;
        if (first.getText().contains(" ")) {
            spaceWidth = first.getCharSpaceWidth();
        } else {
            spaceWidth = new Chunk(' ', new Font(first.getChunkFont())).getWidthPoint();
            if (spaceWidth == 0) {
                spaceWidth = 3;
            }
        }
        /* Creating a chunk with only " " content and taking its width */
        //        System.out.println();
        //        System.out.println(spaceWidth);
        //        /*float */spaceWidth    = new Chunk(' ', new Font(first.getChunkFont())).getWidthPoint();
        //        System.out.println(spaceWidth);
        float chunkDistance = second.getLeft() - first.getRight();
        return chunkDistance <= spaceWidth * spaceWidthMultiplier;
    }
}
