package ru.icc.cells.tabbypdf.utils.processing.filter.bi;

import ru.icc.cells.tabbypdf.entities.Rectangle;
import ru.icc.cells.tabbypdf.entities.TextBlock;
import ru.icc.cells.tabbypdf.utils.processing.filter.Heuristic;

public class HeightBiHeuristic extends BiHeuristic<Rectangle> {
    private double heightMultiplier;

    public HeightBiHeuristic() {
        this(1f);
    }

    public HeightBiHeuristic(double heightMultiplier) {
        super(Heuristic.Orientation.VERTICAL);
        this.heightMultiplier = heightMultiplier;
    }

    @Override
    public boolean test(Rectangle first, Rectangle second) {
        double height;
        if (second.getClass().equals(TextBlock.class)) {
            height = (second.getTop() - ((TextBlock) second).getChunks().get(0).getBottom()) * heightMultiplier;
        } else {
            height = (Math.abs(second.getTop() - second.getBottom()) * heightMultiplier);
        }
        double distance = Math.abs(
            (first.getTop() > second.getTop()
                ? first.getBottom()
                : first.getTop()
            ) - second.getTop()
        );
        return distance <= height;
    }
}
