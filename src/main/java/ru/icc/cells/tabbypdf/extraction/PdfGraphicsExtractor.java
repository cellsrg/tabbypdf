package ru.icc.cells.tabbypdf.extraction;

import lombok.Getter;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import ru.icc.cells.tabbypdf.entities.Rectangle;
import ru.icc.cells.tabbypdf.entities.Ruling;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Андрей on 25.11.2017.
 */
@Getter
class PdfGraphicsExtractor extends PDFGraphicsStreamEngine {

    private final List<Ruling>    rulings      = new ArrayList<>();
    private final List<Rectangle> imageRegions = new ArrayList<>();

    private double x, y;

    /**
     * Constructor.
     *
     * @param page
     */
    protected PdfGraphicsExtractor(PDPage page) {
        super(page);
    }

    public void run() {
        try {
            processPage(getPage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) throws IOException {
        rulings.addAll(Arrays.asList(
            new Ruling(p1, p2),
            new Ruling(p0, p1),
            new Ruling(p2, p3),
            new Ruling(p3, p0)
        ));
    }

    @Override
    public void drawImage(PDImage pdImage) throws IOException {
        AffineTransform at = getGraphicsState().getCurrentTransformationMatrix().createAffineTransform();
        at.scale(1, -1);
        at.translate(0, -1);

        Rectangle2D imageShape = at.createTransformedShape(new Rectangle2D.Double(0, 0, 1, 1)).getBounds2D();

        imageRegions.add(new Rectangle(
            imageShape.getX(),
            imageShape.getY(),
            (imageShape.getX() + imageShape.getWidth()),
            (imageShape.getY() + imageShape.getHeight())
        ));
    }

    @Override
    public void moveTo(float x, float y) throws IOException {
        this.x = x;
        this.y = y;
    }

    @Override
    public void lineTo(float x, float y) throws IOException {
        rulings.add(new Ruling(this.x, this.y, x, y));
        this.x = x;
        this.y = y;
    }

    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException {
    }

    @Override
    public Point2D getCurrentPoint() throws IOException {
        return new Point2D.Double(x, y);
    }

    @Override
    public void clip(int windingRule) throws IOException {
    }

    @Override
    public void closePath() throws IOException {
    }

    @Override
    public void endPath() throws IOException {
    }

    @Override
    public void strokePath() throws IOException {
    }

    @Override
    public void fillPath(int windingRule) throws IOException {
    }

    @Override
    public void fillAndStrokePath(int windingRule) throws IOException {
    }

    @Override
    public void shadingFill(COSName shadingName) throws IOException {
    }
}
