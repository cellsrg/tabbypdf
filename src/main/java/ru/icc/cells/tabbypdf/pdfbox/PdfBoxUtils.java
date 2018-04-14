package ru.icc.cells.tabbypdf.pdfbox;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.text.TextPosition;
import ru.icc.cells.tabbypdf.common.FontCharacteristics;

public class PdfBoxUtils {

    public static FontCharacteristics buildFontCharacteristics(PDFont font, float fontSize, float spaceWidth) {
        FontCharacteristics.Builder builder = FontCharacteristics.newBuilder()
            .setFontName(font.getName())
            .setSize(fontSize)
            .setSpaceWidth(spaceWidth);

        if (font.getFontDescriptor() != null) {
            final PDFontDescriptor fd = font.getFontDescriptor();
            builder.setFontName(fd.getFontName())
                .setFontFamily(fd.getFontFamily())
                .setAllCap(fd.isAllCap())
                .setForceBold(fd.isForceBold())
                .setFixedPitch(fd.isFixedPitch())
                .setItalic(fd.isItalic())
                .setNonSymbolic(fd.isNonSymbolic())
                .setScript(fd.isScript())
                .setSerif(fd.isSerif())
                .setSmallCap(fd.isSmallCap())
                .setSymbolic(fd.isSymbolic());
        }

        return builder.build();
    }

    public static boolean hasSpaceBetweenTextPositions(TextPosition current, TextPosition previous) {
        float distance = current.getXDirAdj() - previous.getXDirAdj() - previous.getWidthDirAdj();
        float spaceWidth = previous.getWidthOfSpace();

        return Math.abs(distance) > spaceWidth;
    }

    public static boolean sameLine(TextPosition current, TextPosition previous) {
        return (current.getYDirAdj() + current.getHeightDir()) == (previous.getYDirAdj() + previous.getHeightDir());
    }
}
