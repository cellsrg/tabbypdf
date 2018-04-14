package ru.icc.cells.tabbypdf.common;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class RectangularTextContainer extends Rectangle implements TextContainer {
    public RectangularTextContainer(double left, double bottom, double right, double top) {
        super(left, bottom, right, top);
    }
}
