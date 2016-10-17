package ru.icc.cells.utils.content;

import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.Matrix;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import ru.icc.cells.common.Rectangle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageRegionExtractionStrategy implements TextExtractionStrategy {

    private List<Rectangle> imageRegions = new ArrayList<>();

    @Override
    public String getResultantText() {
        return null;
    }

    @Override
    public void beginTextBlock() {

    }

    @Override
    public void renderText(TextRenderInfo renderInfo) {

    }

    @Override
    public void endTextBlock() {

    }

    @Override
    public void renderImage(ImageRenderInfo renderInfo) {
        float i11 = renderInfo.getImageCTM().get(Matrix.I11);
        float i12 = renderInfo.getImageCTM().get(Matrix.I12);
        float i13 = renderInfo.getImageCTM().get(Matrix.I13);
        float i21 = renderInfo.getImageCTM().get(Matrix.I21);
        float i22 = renderInfo.getImageCTM().get(Matrix.I22);
        float i23 = renderInfo.getImageCTM().get(Matrix.I23);
        float i31 = renderInfo.getImageCTM().get(Matrix.I31);
        float i32 = renderInfo.getImageCTM().get(Matrix.I32);
        float i33 = renderInfo.getImageCTM().get(Matrix.I33);
//        System.out.printf("|%10.2f %10.2f %10.2f|%n", i11, i12, i13);
//        System.out.printf("|%10.2f %10.2f %10.2f|%n", i21, i22, i23);
//        System.out.printf("|%10.2f %10.2f %10.2f|%n", i31, i32, i33);
        try {
            float left   = renderInfo.getStartPoint().get(0);
            float bottom = renderInfo.getStartPoint().get(1);
            float width  = (Float.valueOf(renderInfo.getImage().get(new PdfName("Width")).toString()));
            float height = (Float.valueOf(renderInfo.getImage().get(new PdfName("Height")).toString()));

            width = (width + i32) / i22;
            height = (height + i31) / i11;

//            System.out.println("height = " + height);
//            System.out.println("width = " + width);
//            System.out.println();
            imageRegions.add(new Rectangle(left, bottom, left + width, bottom + height));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Rectangle> getImageRegions() {
        return imageRegions;
    }
}
