package ru.icc.cells.utils.processing.filter.bi;

import ru.icc.cells.common.Rectangle;

public class HeightBiFilter extends BiFilter<Rectangle> {
    private float heightMultiplier;

    public HeightBiFilter() {
        this(1f);
    }

    public HeightBiFilter(float heightMultiplier) {
        super(Orientation.VERTICAL);
        this.heightMultiplier = heightMultiplier;
    }

    @Override
    public boolean filter(Rectangle first, Rectangle second) {

        float height = (Math.abs(second.getTop() - second.getBottom()) * heightMultiplier);
        float distance =
                Math.abs(((first.getTop() > second.getTop()) ? first.getBottom() : first.getTop()) - second.getTop());
        return distance <= height;
    }
}
