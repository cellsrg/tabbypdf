package ru.icc.cells.tabbypdf.entities;

import org.junit.Test;

import static org.junit.Assert.*;

public class TextBlockTest {

    @Test
    public void add() {
    }

    @Test
    public void add1() {
    }

    @Test
    public void join() {
        TextBlock textBlock = new TextBlock();
        TextChunk textChunk = new TextChunk("", 10, 10, 20, 20, FontCharacteristics.newBuilder().build());
        textBlock.join(textChunk);
        textBlock.getChunks().add(textChunk);

        assertEquals(textBlock.getLeft(), textChunk.getLeft(), 0);
        assertEquals(textBlock.getRight(), textChunk.getRight(), 0);
        assertEquals(textBlock.getTop(), textChunk.getTop(), 0);
        assertEquals(textBlock.getBottom(), textChunk.getBottom(), 0);

        textChunk = new TextChunk("", 15, 15, 25, 20, FontCharacteristics.newBuilder().build());
        textBlock.join(textChunk);
        textBlock.getChunks().add(textChunk);

        assertEquals(textBlock.getLeft(), 10, 0);
        assertEquals(textBlock.getRight(), textChunk.getRight(), 0);
        assertEquals(textBlock.getTop(), textChunk.getTop(), 0);
        assertEquals(textBlock.getBottom(), 10, 0);
    }

    @Test
    public void isEmpty() {
        TextBlock textBlock = new TextBlock();
        assertTrue(textBlock.isEmpty());

        textBlock.add(new TextBlock());
        assertTrue(textBlock.isEmpty());

        textBlock.add(new TextChunk("", 0, 0, 0, 0, FontCharacteristics.newBuilder().build()));
        assertFalse(textBlock.isEmpty());
    }

    @Test
    public void getText() {
        TextBlock textBlock = new TextBlock();

        assertEquals(textBlock.getText(), "");

        textBlock.add(new TextChunk("a", 0, 0, 0, 0, FontCharacteristics.newBuilder().build()));
        textBlock.add(new TextChunk("b", 0, 0, 0, 0, FontCharacteristics.newBuilder().build()));

        assertEquals(textBlock.getText(), "ab");
    }
}