package ru.icc.cells.tabbypdf.utils.content;

import ru.icc.cells.tabbypdf.common.TextBlock;
import ru.icc.cells.tabbypdf.common.TextChunk;

import java.util.stream.Stream;

public class FontUtils
{
    public static String[] getFontFamilies(TextChunk chunk)
    {
        String[][] fontNameArrays = chunk.getChunkFont().getFamilyFontName();
        String[]   fontFamilies   = new String[fontNameArrays.length];
        for (int i = 0; i < fontNameArrays.length; i++)
        {
            String[] fontNameArray = fontNameArrays[i];
            if (fontNameArray.length == 4)
            {
                fontFamilies[i] = fontNameArray[3].split("[-,]")[0];
            }
        }
        return fontFamilies;
    }

    public static String[] getFontAttributes(TextChunk chunk)
    {
        String[][] fontNameArrays = chunk.getChunkFont().getFamilyFontName();
        String[]   fontAttributes = new String[fontNameArrays.length];
        for (int i = 0; i < fontNameArrays.length; i++)
        {
            String[] fontNameArray = fontNameArrays[i];
            if (fontNameArray.length == 4)
            {
                fontAttributes[i] = Stream
                        .of(fontNameArray[3].split("[-,]"))
                        .skip(1)
                        .reduce(String::concat)
                        .orElse("");
            }
        }
        return fontAttributes;
    }

    public static boolean isBold(TextChunk chunk)
    {
        String[][] fontNameArrays = chunk.getChunkFont().getFamilyFontName();
        for (String[] fontNameArray : fontNameArrays) {
            for (String fontName : fontNameArray) {
                if (fontName.toLowerCase().contains("bold")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isBold(TextBlock block)
    {
        for (TextChunk chunk : block.getChunks())
        {
            if (isBold(chunk))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isItalic(TextChunk chunk)
    {
        String[][] fontNameArrays = chunk.getChunkFont().getFamilyFontName();
        for (String[] fontNameArray : fontNameArrays)
        {
            for (String fontName : fontNameArray)
            {
                if (fontName.toLowerCase().contains("italic"))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isItalic(TextBlock block)
    {
        for (TextChunk chunk : block.getChunks())
        {
            if (isItalic(chunk))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isRegular(TextChunk chunk)
    {
        return !isItalic(chunk) && !isBold(chunk);
    }

    public static boolean isRegular(TextBlock block)
    {
        for (TextChunk chunk : block.getChunks())
        {
            if (isRegular(chunk))
            {
                return true;
            }
        }
        return false;
    }
}
