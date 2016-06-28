package utils;

import com.itextpdf.text.pdf.parser.GraphicsState;
import com.itextpdf.text.pdf.parser.PathPaintingRenderInfo;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

import java.lang.reflect.Field;

/**
 * Created by sunveil on 27/06/16.
 */
public class ReflectionIText {

    public static GraphicsState getGs(PathPaintingRenderInfo ri) throws NoSuchFieldException, IllegalAccessException {
        Field f = ri.getClass().getDeclaredField("gs");
        f.setAccessible(true);
        GraphicsState r = (GraphicsState) f.get(ri);
        return null;
    }

    public static GraphicsState getGs(TextRenderInfo ri) throws NoSuchFieldException, IllegalAccessException {
        Field f = ri.getClass().getDeclaredField("gs");
        f.setAccessible(true);
        GraphicsState r = (GraphicsState) f.get(ri);
        return null;
    }

}
