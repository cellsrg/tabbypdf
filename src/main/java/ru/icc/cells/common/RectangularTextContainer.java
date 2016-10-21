package ru.icc.cells.common;

public abstract class RectangularTextContainer extends Rectangle implements TextContainer {
    public RectangularTextContainer() {
        super();
    }

    public RectangularTextContainer(float left, float bottom, float right, float top) {
        super(left, bottom, right, top);
    }
}
