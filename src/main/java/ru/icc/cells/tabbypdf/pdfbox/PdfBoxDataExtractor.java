package ru.icc.cells.tabbypdf.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import ru.icc.cells.tabbypdf.common.Page;
import ru.icc.cells.tabbypdf.common.Rectangle;
import ru.icc.cells.tabbypdf.common.Ruling;
import ru.icc.cells.tabbypdf.common.TextChunk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.icc.cells.tabbypdf.pdfbox.PdfBoxUtils.buildFontCharacteristics;
import static ru.icc.cells.tabbypdf.pdfbox.PdfBoxUtils.hasSpaceBetweenTextPositions;
import static ru.icc.cells.tabbypdf.pdfbox.PdfBoxUtils.sameLine;

public class PdfBoxDataExtractor extends PDFTextStripper {

    public static class Factory {
        public PdfBoxDataExtractor getPdfBoxTextExtractor(String file) {
            return getPdfBoxTextExtractor(new File(file));
        }


        public PdfBoxDataExtractor getPdfBoxTextExtractor(File file) {
            try {
                return new PdfBoxDataExtractor(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* Координата 0,0 расположена в левом нижнем углу */

    private final File               file;
    private final List<TextPosition> textPositions = new ArrayList<>();

    private PdfBoxDataExtractor(File file) throws IOException {
        this.file = file;
    }

    public List<Page> getPageContent() {
        List<Page> pages = new ArrayList<>();

        try (PDDocument document = PDDocument.load(file)) {

            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); ++pageIndex) {
                textPositions.clear();
                stripPage(document, pageIndex);

                List<TextPosition> chunk = new ArrayList<>();
                List<List<TextPosition>> chunks = new ArrayList<>();

                TextPosition previous = null;
                for (TextPosition textPosition : textPositions) {
                    if (previous == null) {
                        previous = textPosition;
                        continue;
                    }

                    chunk.add(previous);

                    if ((hasSpaceBetweenTextPositions(textPosition, previous) && !previous.getUnicode().equals("•"))
                        || !sameLine(textPosition, previous) || previous.getUnicode().equals(" ")
                    ) {
                        chunks.add(chunk);
                        chunk = new ArrayList<>();
                    }
                    previous = textPosition;
                }

                if (!textPositions.isEmpty()) {
                    TextPosition textPosition = textPositions.get(textPositions.size() - 1);
                    chunk.add(textPosition);
                    chunks.add(chunk);
                }

                PDPage pdPage = document.getPage(pageIndex);
                PDRectangle mediaBox = pdPage.getMediaBox();

                final List<TextChunk> originChunks = textPositions.stream()
                    .map(tPos -> new TextChunk(
                        tPos.getUnicode().trim(),
                        tPos.getX(),
                        mediaBox.getHeight() - tPos.getY(),
                        tPos.getX() + tPos.getWidth(),
                        mediaBox.getHeight() - (tPos.getY() + tPos.getHeight()),
                        buildFontCharacteristics(tPos.getFont(), tPos.getFontSize(), tPos.getWidthOfSpace())
                    ))
                    .collect(Collectors.toList());

                List<TextChunk> wordChunks = chunks.stream()
                    .filter(word -> word.size() > 0)
                    .map(word -> {
                        TextPosition first = word.get(0);
                        TextPosition last = word.get(word.size() - 1);

                        String text = word.stream()
                            .map(TextPosition::getUnicode)
                            .map(String::trim)
                            .collect(Collectors.joining());
                        float spaceWidth = (float) word.stream()
                            .mapToDouble(TextPosition::getWidthOfSpace)
                            .average()
                            .orElseThrow(() -> new RuntimeException("Space width not found"));

                        return new TextChunk(
                            text,
                            first.getX(),
                            mediaBox.getHeight() - first.getY(),
                            last.getX() + last.getWidth(),
                            mediaBox.getHeight() - (last.getY() + last.getHeight()),
                            buildFontCharacteristics(last.getFont(), last.getFontSize(), spaceWidth)
                        );
                    })
                    .collect(Collectors.toList());

                PdfBoxGraphicsExtractor graphicsExtractor = new PdfBoxGraphicsExtractor(pdPage);
                graphicsExtractor.run();
                List<Ruling> rulings = graphicsExtractor.getRulings();
                List<Rectangle> imageRegions = graphicsExtractor.getImageRegions();

                pages.add(new Page(
                    mediaBox.getLowerLeftX(), mediaBox.getLowerLeftY(), mediaBox.getUpperRightX(),
                    mediaBox.getUpperRightY(), pdPage.getRotation(), originChunks, originChunks, wordChunks, rulings,
                    imageRegions
                ));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return pages;
    }

    private void stripPage(PDDocument document, int page) {
        setStartPage(page + 1);
        setEndPage(page + 1);

        Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
        try {
            writeText(document, dummy);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) {
        this.textPositions.addAll(textPositions);
    }
}