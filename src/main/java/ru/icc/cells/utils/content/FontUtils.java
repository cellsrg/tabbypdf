package ru.icc.cells.utils.content;

import ru.icc.cells.common.TextChunk;

import java.util.stream.Stream;

public class FontUtils {
    public static String[] getFontFamilies(TextChunk chunk) {
        String[][] fontNameArrays = chunk.getChunkFont().getFamilyFontName();
        String[]   fontFamilies   = new String[fontNameArrays.length];
        for (int i = 0; i < fontNameArrays.length; i++) {
            String[] fontNameArray = fontNameArrays[i];
            if (fontNameArray.length == 4) {
                fontFamilies[i] = fontNameArray[3].split("[-,]")[0];
            }
        }
        return fontFamilies;
    }

    public static String[] getFontAttributes(TextChunk chunk) {
        String[][] fontNameArrays = chunk.getChunkFont().getFamilyFontName();
        String[]   fontAttributes = new String[fontNameArrays.length];
        for (int i = 0; i < fontNameArrays.length; i++) {
            String[] fontNameArray = fontNameArrays[i];
            if (fontNameArray.length == 4) {
                fontAttributes[i] = Stream.of(fontNameArray[3].split("[-,]")).skip(1).reduce(String::concat).orElse("");
            }
        }
        return fontAttributes;
    }
}
