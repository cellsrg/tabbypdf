package ru.icc.cells.tabbypdf.common;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page object contains raw data from PDF page.
 */
@Getter
public class Page extends Rectangle {
    private final List<TextChunk> originChunks;
    private final List<TextChunk> characterChunks;
    private final List<TextChunk> wordChunks;
    private final List<Ruling>    rulings;
    private final List<Rectangle> imageRegions;
    private final int             rotation;

    public Page(double left, double bottom, double right, double top, int rotation,
                List<TextChunk> originChunks, List<TextChunk> characterChunks, List<TextChunk> wordChunks,
                List<Ruling> rulings, List<Rectangle> imageRegions) {
        super(left, bottom, right, top);
        this.rotation = rotation;
        this.originChunks = originChunks;
        this.characterChunks = characterChunks;
        this.wordChunks = wordChunks;
        this.rulings = rulings;
        this.imageRegions = imageRegions;
    }

    public Page getRegion(Rectangle bound) {
        List<TextChunk> originChunks = getChunksWithinBound(this.originChunks, bound);
        List<TextChunk> characterChunks = getChunksWithinBound(this.characterChunks, bound);
        List<TextChunk> wordChunks = getChunksWithinBound(this.wordChunks, bound);
        List<Ruling> rulings = this.rulings.stream()
            .filter(ruling -> bound.intersects(Rectangle.fromRuling(ruling)))
            .collect(Collectors.toList());
        List<Rectangle> imageRegions = this.imageRegions.stream()
            .filter(bound::intersects)
            .collect(Collectors.toList());

        return new Page(
            bound.getLeft(),
            bound.getBottom(),
            bound.getRight(),
            bound.getTop(),
            rotation,
            originChunks,
            characterChunks,
            wordChunks,
            rulings,
            imageRegions
        );
    }

    private static List<TextChunk> getChunksWithinBound(List<TextChunk> rectangles, Rectangle bound) {
        return rectangles.stream().filter(bound::intersects).collect(Collectors.toList());
    }
}
