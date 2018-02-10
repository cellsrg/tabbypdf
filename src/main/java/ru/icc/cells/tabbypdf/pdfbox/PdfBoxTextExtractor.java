package ru.icc.cells.tabbypdf.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import ru.icc.cells.tabbypdf.common.*;
import ru.icc.cells.tabbypdf.common.Rectangle;
import ru.icc.cells.tabbypdf.debug.Debug;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PdfBoxTextExtractor extends PDFTextStripper {

    /* Координата 0,0 расположена в левом нижнем углу */

    private final File file;
    private List<TextPosition> textPositions = new ArrayList<>();

    private PdfBoxTextExtractor(File file) throws IOException {
        this.file = file;
    }

    public static void main(String[] args) throws IOException {
        File pdfDir = new File("src/test/resources/pdf");
        File[] pdfFiles = pdfDir.listFiles(pathname -> !pathname.isDirectory());

        Debug.ENABLE_DEBUG = true;

        for (File pdf : pdfFiles) {
            PdfBoxTextExtractor extractor = new PdfBoxTextExtractor(pdf);
            Debug.handleFile(pdf);
            System.out.println(String.format("Processing file %s", pdf.getName()));

            List<Page> pages = extractor.extractContent();

            for (int i = 0; i < pages.size(); i++) {
                Debug.setPage(i);

                Page page = pages.get(i);
                if (page.getRotation() != 0) {
                    System.out.println("page.getRotation() = " + page.getRotation());
                }

                Debug.setColor(Color.RED);
                Debug.drawRect(page);

                Debug.setColor(Color.GREEN);
                Debug.drawRects(page.getImageRegions());

                Debug.setColor(Color.BLUE);
                page.getRulings().forEach(Debug::drawRuling);

                Debug.setColor(Color.CYAN);
                List<TextChunk> chunks = page.getWordChunks().stream()
                        .filter(chunk -> !chunk.getText().isEmpty())
                        .peek(textChunk -> textChunk.rotate(page.getRotation(), page.getTop(), page.getRight()))
                        .collect(Collectors.toList());
                Debug.drawRects(chunks);

                Debug.setColor(Color.RED);
                for (int j = 0; j < chunks.size(); j++) {
                    TextChunk chunk = chunks.get(j);
                    Debug.printText(String.valueOf(j), chunk.getLeft(), chunk.getBottom());
                }
            }

            Debug.close("src/test/resources/" + pdf.getName() + "-edited.pdf");
        }
    }

    public List<Page> extractContent() throws IOException {
        List<Page> pages = new ArrayList<>();

        try (PDDocument document = PDDocument.load(file)) {

            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); ++pageIndex)
            {
                textPositions.clear();
                stripPage(document, pageIndex);

                List<TextPosition>       chunk  = new ArrayList<>();
                List<List<TextPosition>> chunks = new ArrayList<>();

                TextPosition  previous = null;
                for (TextPosition textPosition : textPositions) {
                    if (previous == null) {
                        previous = textPosition;
                        continue;
                    }

                    chunk.add(previous);

                    if ((hasSpaceBetweenTextPositions(textPosition, previous) && !previous.getUnicode().equals("•"))
                            || !sameLine(textPosition, previous) || previous.getUnicode().equals(" ")) {
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
                                mediaBox.getHeight() - tPos.getY(), // вычитаем, потому чт
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
                                    .orElseThrow(() -> new RuntimeException("Impossible #1"));

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
                        mediaBox.getUpperRightY(),pdPage.getRotation(), originChunks, originChunks, wordChunks, rulings,
                        imageRegions
                ));
            }
        }

        return pages;
    }

    private static FontCharacteristics buildFontCharacteristics(PDFont font, float fontSize, float spaceWidth) {
        FontCharacteristics.Builder fbuilder = FontCharacteristics.newBuilder()
                .setFontName(font.getName())
                .setSize(fontSize)
                .setSpaceWidth(spaceWidth);

        if (font.getFontDescriptor() != null) {
            final PDFontDescriptor fd = font.getFontDescriptor();
            fbuilder.setFontName(fd.getFontName())
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

        return fbuilder.build();
    }

    private void stripPage(PDDocument document, int page) throws IOException
    {
        setStartPage(page + 1);
        setEndPage(page + 1);

        Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
        writeText(document, dummy);
    }

    private static boolean hasSpaceBetweenTextPositions(TextPosition current, TextPosition previous) {
        float distance   = current.getXDirAdj() - previous.getXDirAdj() - previous.getWidthDirAdj();
        float spaceWidth = previous.getWidthOfSpace();

        return Math.abs(distance) > spaceWidth;
    }

    private static boolean sameLine(TextPosition current, TextPosition previous) {
        return (current.getYDirAdj() + current.getHeightDir()) == (previous.getYDirAdj() + previous.getHeightDir());
    }
    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException
    {
        this.textPositions.addAll(textPositions);
    }
}