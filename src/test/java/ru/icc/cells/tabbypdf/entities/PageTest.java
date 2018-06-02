package ru.icc.cells.tabbypdf.entities;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PageTest {

    @Test
    public void getRegion() {
        List<TextChunk> chunks = new ArrayList<TextChunk>() {{
            add(new TextChunk("", 10, 10, 15, 15, FontCharacteristics.newBuilder().build()));
            add(new TextChunk("", 75, 175, 80, 180, FontCharacteristics.newBuilder().build()));
        }};
        List<Ruling> rulings = new ArrayList<Ruling>(){{
            add(new Ruling(5, 5, 5, 30));
            add(new Ruling(80, 5, 90, 5));
        }};
        List<Rectangle> imageRegions = new ArrayList<Rectangle>(){{
            add(new Rectangle(10, 10, 15, 15));
            add(new Rectangle( 75, 175, 80, 180));
        }};

        Page page = new Page(
            0,
            0,
            100,
            200,
            0,
            chunks,
            chunks,
            chunks,
            rulings,
            imageRegions
        );

        Page pageRegion = page.getRegion(new Rectangle(0, 0, 50, 50));

        assertEquals(pageRegion.getLeft(), 0, 0.0);
        assertEquals(pageRegion.getRight(), 50, 0.0);
        assertEquals(pageRegion.getBottom(), 0, 0.0);
        assertEquals(pageRegion.getTop(), 50, 0.0);

        assertEquals(pageRegion.getOriginChunks().size(), 1);
        assertEquals(pageRegion.getCharacterChunks().size(), 1);
        assertEquals(pageRegion.getWordChunks().size(), 1);
        assertEquals(pageRegion.getRulings().size(), 1);
        assertEquals(pageRegion.getImageRegions().size(), 1);
    }
}