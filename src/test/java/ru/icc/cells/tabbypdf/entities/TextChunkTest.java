package ru.icc.cells.tabbypdf.entities;

import org.junit.Test;

import static org.junit.Assert.*;

public class TextChunkTest {

    @Test
    public void sameLine() {
        TextChunk base = new TextChunk("", 0, 0, 10, 10, FontCharacteristics.newBuilder().build());
        TextChunk sameBottom = new TextChunk("", 20, 0, 30, 40, FontCharacteristics.newBuilder().build());
        TextChunk sameTop = new TextChunk("", 5, 5, 30, 10, FontCharacteristics.newBuilder().build());
        TextChunk otherLine = new TextChunk("", 5, 5, 30, 5, FontCharacteristics.newBuilder().build());

        assertTrue(base.sameLine(sameBottom));
        assertTrue(base.sameLine(sameTop));
        assertFalse(base.sameLine(otherLine));
    }

    @Test
    public void distanceFromEndOf() {
        TextChunk one = new TextChunk("", 50, 10, 70, 10, FontCharacteristics.newBuilder().build());
        TextChunk two = new TextChunk("", 0, 10, 20, 10, FontCharacteristics.newBuilder().build());

        assertEquals(one.distanceFromEndOf(two), one.getLeft() - two.getRight(), 0.0);
    }

    @Test
    public void compareTo() {
        TextChunk chunk = new TextChunk("", 0, 0, 10, 10, FontCharacteristics.newBuilder().build());
        TextChunk sameChunk = new TextChunk("", 0, 0, 10, 10, FontCharacteristics.newBuilder().build());

        TextChunk equalLeftBtmChunk = new TextChunk("", 0, 0, 20, 20, FontCharacteristics.newBuilder().build());
        TextChunk chunkToTheLeft = new TextChunk("", -5, 0, 20, 20, FontCharacteristics.newBuilder().build());
        TextChunk chunkToTheRight = new TextChunk("", 5, 0, 20, 20, FontCharacteristics.newBuilder().build());
        TextChunk upperChunk = new TextChunk("", 0, 10, 10, 20, FontCharacteristics.newBuilder().build());
        TextChunk lowerChunk = new TextChunk("", 0, -10, 10, 20, FontCharacteristics.newBuilder().build());


        assertEquals(chunk.compareTo(chunk), 0);
        assertEquals(chunk.compareTo(sameChunk), 0);
        assertEquals(chunk.compareTo(equalLeftBtmChunk), 0);
        assertEquals(chunk.compareTo(chunkToTheLeft), 1);
        assertEquals(chunk.compareTo(chunkToTheRight), -1);
        assertEquals(chunk.compareTo(upperChunk), -1);
        assertEquals(chunk.compareTo(lowerChunk), 1);

    }
}