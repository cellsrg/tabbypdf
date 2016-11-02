package ru.icc.cells.utils.content;

import com.itextpdf.text.pdf.parser.GraphicsState;
import com.itextpdf.text.pdf.parser.PathPaintingRenderInfo;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

import java.lang.reflect.Field;

/**
 * Created by sunveil on 27/06/16.
 */
public class ReflectionIText
{

    public static GraphicsState getGs(PathPaintingRenderInfo ri)
    {
        return getGsFrom(ri);
    }

    public static GraphicsState getGs(TextRenderInfo ri)
    {
        return getGsFrom(ri);
    }

    private static GraphicsState getGsFrom(Object ri)
    {
        try
        {
            Field field = ri.getClass().getDeclaredField("gs");
            field.setAccessible(true);
            GraphicsState r = (GraphicsState) field.get(ri);
            return r;
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            return null;
        }
    }

}
