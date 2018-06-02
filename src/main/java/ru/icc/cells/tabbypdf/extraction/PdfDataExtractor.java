package ru.icc.cells.tabbypdf.extraction;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import ru.icc.cells.tabbypdf.entities.Page;
import ru.icc.cells.tabbypdf.entities.Rectangle;
import ru.icc.cells.tabbypdf.entities.Ruling;
import ru.icc.cells.tabbypdf.entities.TextChunk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.icc.cells.tabbypdf.utils.PdfUtils.buildFontCharacteristics;
import static ru.icc.cells.tabbypdf.utils.PdfUtils.hasSpaceBetweenTextPositions;
import static ru.icc.cells.tabbypdf.utils.PdfUtils.sameLine;

public class PdfDataExtractor extends PDFTextStripper {

    public static class Factory {
        public PdfDataExtractor getPdfBoxTextExtractor(String file) {
            return getPdfBoxTextExtractor(new File(file));
        }


        public PdfDataExtractor getPdfBoxTextExtractor(File file) {
            try {
                return new PdfDataExtractor(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* Координата 0,0 расположена в левом верхнем углу */

    private final File               file;
    private final List<TextPosition> textPositions = new ArrayList<>();

    private PdfDataExtractor(File file) throws IOException {
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
                for (TextPosition current : textPositions) {
                    if (previous == null) {
                        chunk.add(current);
                        previous = current;
                        continue;
                    }

                    if ((hasSpaceBetweenTextPositions(current, previous) && !current.getUnicode().equals("•"))
                        || !sameLine(current, previous) || current.getUnicode().equals(" ")
                    ) {
                        if (!chunk.isEmpty()) {
                            chunks.add(chunk);
                        }
                        chunk = new ArrayList<>();
                    }

                    chunk.add(current);
                    if (current.getUnicode().equals(" ")) {
                        chunks.add(chunk);
                        chunk = new ArrayList<>();
                    }

                    previous = current;
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
                        tPos.getUnicode(),
                        tPos.getX(),
                        mediaBox.getHeight() - tPos.getY(),
                        tPos.getX() + tPos.getWidth(),
                        mediaBox.getHeight() - tPos.getY() + tPos.getHeight(),
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
                            .collect(Collectors.joining());
                        double spaceWidth = word.stream()
                            .mapToDouble(TextPosition::getWidthOfSpace)
                            .average()
                            .orElseThrow(() -> new RuntimeException("Space width not found"));

                        return new TextChunk(
                            text,
                            first.getX(),
                            mediaBox.getHeight() - first.getY(),
                            last.getX() + last.getWidth(),
                            mediaBox.getHeight() - last.getY() + last.getHeight(),
                            buildFontCharacteristics(last.getFont(), last.getFontSize(), spaceWidth)
                        );
                    })
                    .collect(Collectors.toList());

                PdfGraphicsExtractor graphicsExtractor = new PdfGraphicsExtractor(pdPage);
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