package ru.icc.cells.utils;

import com.itextpdf.awt.geom.AffineTransform;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.parser.*;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by sunveil on 23/06/16.
 */

public class MikhailovExtRenderListener implements ExtRenderListener {
    public ArrayList<PathPaintingRenderInfo> extList = new ArrayList<PathPaintingRenderInfo>();

    public PathConstructionRenderInfo pathConstruction;

    private ArrayList<Line> allLines      = new ArrayList<Line>();
    private ArrayList<Line> allTrashLines = new ArrayList<Line>();

    private ArrayList<Rectangle> allRectangles      = new ArrayList<Rectangle>();
    private ArrayList<Rectangle> allTrashRectangles = new ArrayList<Rectangle>();

    private ArrayList<Rectangle> tmpRectangles = new ArrayList<Rectangle>();
    private ArrayList<Line>      tmpLines      = new ArrayList<Line>();

    private Stack<PathConstructionRenderInfo> pathStack = new Stack<>();

    private static int TEXT_RENDER_MODE_INVISIBLE = 3;

    private float         x0    = 0;
    private float         y0    = 0;
    private GraphicsState curGs = null;

    private int rule;
    private static int INVISIABLE_LINE = 0;

    @Override
    public void modifyPath(PathConstructionRenderInfo renderInfo) {
        pathConstruction = renderInfo;
        pathStack.add(renderInfo);
        AffineTransform af = new AffineTransform(pathConstruction.getCtm().get(0), pathConstruction.getCtm().get(3),
                                                 pathConstruction.getCtm().get(1), pathConstruction.getCtm().get(4),
                                                 pathConstruction.getCtm().get(3), pathConstruction.getCtm().get(5));
        switch (pathConstruction.getOperation()) {
            case PathConstructionRenderInfo.MOVETO: {
                float x = pathConstruction.getSegmentData().get(0);
                float y = pathConstruction.getSegmentData().get(1);
                x = x + pathConstruction.getCtm().get(6);
                y = y + pathConstruction.getCtm().get(7);
                af.translate(x, y);
                x0 = (float) af.getTranslateX();
                y0 = (float) af.getTranslateY();
                break;
            }
            case PathConstructionRenderInfo.LINETO: {
                float x = pathConstruction.getSegmentData().get(0);
                float y = pathConstruction.getSegmentData().get(1);
                x += pathConstruction.getCtm().get(6);
                y += pathConstruction.getCtm().get(7);
                af.translate(x, y);
                x = (float) af.getTranslateX();
                y = (float) af.getTranslateY();
                Line tmpLine = new Line(x0, y0, x, y);
                x0 = x;
                y0 = y;
                tmpLines.add(tmpLine);
                break;
            }
            case PathConstructionRenderInfo.RECT: {
                float x = pathConstruction.getSegmentData().get(0);
                float y = pathConstruction.getSegmentData().get(1);
                x += pathConstruction.getCtm().get(6);
                y += pathConstruction.getCtm().get(7);
                float width  = pathConstruction.getSegmentData().get(2);
                float height = pathConstruction.getSegmentData().get(3);
                width += pathConstruction.getCtm().get(6);
                height += pathConstruction.getCtm().get(7);
                af.translate(x, y);
                x = (float) af.getTranslateX();
                y = (float) af.getTranslateY();
                af.translate(width, height);
                width = (float) af.getTranslateX();
                height = (float) af.getTranslateY();
                Rectangle tmpRect = new Rectangle(x, y, width, height);
                tmpRectangles.add(tmpRect);
                break;
            }
        }
    }

    @Override
    public Path renderPath(PathPaintingRenderInfo renderInfo) {
        extList.add(renderInfo);
        curGs = ReflectionIText.getGs(renderInfo);
        this.rule = renderInfo.getOperation();
        pathStack.clear();
        if (this.rule != 0) {
            allLines.addAll(tmpLines);
            allRectangles.addAll(tmpRectangles);
        }
        tmpLines.clear();
        tmpRectangles.clear();
        return null;
    }

    @Override
    public void clipPath(int rule) {
    }

    @Override
    public void beginTextBlock() {

    }

    @Override
    public void renderText(TextRenderInfo renderInfo) {

        AffineTransform af = null;

        af = new AffineTransform(ReflectionIText.getGs(renderInfo).getCtm().get(0),
                                 ReflectionIText.getGs(renderInfo).getCtm().get(3),
                                 ReflectionIText.getGs(renderInfo).getCtm().get(1),
                                 ReflectionIText.getGs(renderInfo).getCtm().get(4),
                                 ReflectionIText.getGs(renderInfo).getCtm().get(3),
                                 ReflectionIText.getGs(renderInfo).getCtm().get(5));


        float x = renderInfo.getAscentLine().getStartPoint().get(0);
        float y = renderInfo.getAscentLine().getStartPoint().get(1);
        af.translate(x, y);
        x = renderInfo.getAscentLine().getEndPoint().get(0);
        y = renderInfo.getAscentLine().getEndPoint().get(1);
        af.translate(x, y);
        x = renderInfo.getDescentLine().getStartPoint().get(0);
        y = renderInfo.getDescentLine().getStartPoint().get(1);
        af.translate(x, y);
        x = renderInfo.getDescentLine().getEndPoint().get(0);
        y = renderInfo.getDescentLine().getEndPoint().get(1);
        af.translate(x, y);
        x = renderInfo.getBaseline().getStartPoint().get(0);
        y = renderInfo.getBaseline().getStartPoint().get(1);
        af.translate(x, y);
        x = renderInfo.getBaseline().getEndPoint().get(0);
        y = renderInfo.getBaseline().getEndPoint().get(1);
        af.translate(x, y);
    }

    @Override
    public void endTextBlock() {

    }

    @Override
    public void renderImage(ImageRenderInfo renderInfo) {

    }

    public ArrayList<Line> getLines() {
        for (Rectangle r : allRectangles) {
            allLines.add(new Line(r.getLeft(), r.getBottom(), r.getRight(), r.getBottom()));
            allLines.add(new Line(r.getLeft(), r.getTop(), r.getRight(), r.getTop()));
            allLines.add(new Line(r.getLeft(), r.getBottom(), r.getLeft(), r.getTop()));
            allLines.add(new Line(r.getRight(), r.getBottom(), r.getRight(), r.getTop()));
        }
        return allLines;
    }

    public ArrayList<Rectangle> getAllRectangles() {
        return allRectangles;
    }

    public ArrayList<Line> getTrashLines() {
        return allTrashLines;
    }

    public ArrayList<Rectangle> getAllTrashRectangles() {
        return allTrashRectangles;
    }
}
