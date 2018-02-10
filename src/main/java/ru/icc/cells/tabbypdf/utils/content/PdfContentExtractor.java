package ru.icc.cells.tabbypdf.utils.content;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import ru.icc.cells.tabbypdf.common.Page;
import ru.icc.cells.tabbypdf.common.Ruling;
import ru.icc.cells.tabbypdf.common.TextChunk;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PdfContentExtractor
{
    private PdfReader              reader;
    private PdfReaderContentParser parser;
    private Map<Integer, MikhailovTextExtractionStrategy> processedText    = new HashMap<>();
    private Map<Integer, MikhailovExtRenderListener>      processedRulings = new HashMap<>();
    private Map<Integer, ImageRegionExtractionStrategy>   processedImages  = new HashMap<>();


    public PdfContentExtractor(String path) throws IOException
    {
        this.reader = new PdfReader(path);
        this.parser = new PdfReaderContentParser(reader);
        processImageContent(1);
    }

    public PdfContentExtractor(PdfReader reader)
    {
        this.reader = reader;
        this.parser = new PdfReaderContentParser(reader);
    }

    public void close() {
        reader.close();
    }

    public int getNumberOfPages()
    {
        return reader.getNumberOfPages();
    }

    public Page getPageContent(int pageNumber) throws IOException
    {
        pageNumber++;
        Rectangle pageBound = reader.getPageSize(pageNumber);
        return new Page(pageBound.getLeft(), pageBound.getBottom(), pageBound.getRight(), pageBound.getTop(), 0,
                        getChunks(pageNumber), getCharacterChunks(pageNumber), getWordChunks(pageNumber),
                        getRulings(pageNumber), getImageRegions(pageNumber));
    }

    public List<TextChunk> getWordChunks(int pageNumber) throws IOException
    {
        return processTextContent(pageNumber).getResultantWordLocation(
                (MikhailovTextExtractionStrategy.TextChunkFilter) null);
    }

    public List<TextChunk> getChunks(int pageNumber) throws IOException
    {
        return processTextContent(pageNumber).getLocationalChunkResult();
    }

    public List<TextChunk> getCharacterChunks(int pageNumber) throws IOException
    {
        return processTextContent(pageNumber).getLocationalResult();
    }

    public String getText(int pageNumber) throws IOException
    {
        return processTextContent(pageNumber).getResultantText();
    }

    public List<Ruling> getRulings(int pageNumber) throws IOException
    {
        MikhailovExtRenderListener extRenderListener = processGraphicContent(pageNumber);
        List<Ruling> lines = extRenderListener.getAllLines()
                .stream()
                .map(line -> new Ruling(
                        (float) line.getBasePoints().get(0).getX(),
                        (float) line.getBasePoints().get(0).getY(),
                        (float) line.getBasePoints().get(1).getX(),
                        (float) line.getBasePoints().get(1).getY()
                ))
                .collect(Collectors.toList());

        lines.addAll(extRenderListener.getAllRectangles()
                                      .stream()
                                      .map(this::mapRectangleToRuling)
                                      .collect(Collectors.toList()));

        return lines;
    }

    public List<ru.icc.cells.tabbypdf.common.Rectangle> getImageRegions(int pageNumber) throws IOException
    {
        return processImageContent(pageNumber).getImageRegions();
    }

    private Ruling mapRectangleToRuling(Rectangle rectangle)
    {
        float  height = Math.abs(rectangle.getTop() - rectangle.getBottom());
        float  width  = Math.abs(rectangle.getRight() - rectangle.getLeft());
        Ruling ruling;
        if (height > width)
        {
            float x = (rectangle.getLeft() + rectangle.getRight()) / 2;
            ruling = new Ruling(new Point2D.Float(x, rectangle.getBottom()), new Point2D.Float(x, rectangle.getTop()));
        }
        else
        {
            float y = (rectangle.getBottom() + rectangle.getTop()) / 2;
            ruling = new Ruling(new Point2D.Float(rectangle.getLeft(), y), new Point2D.Float(rectangle.getRight(), y));
        }
        return ruling;
    }

    private MikhailovExtRenderListener processGraphicContent(int pageNumber) throws IOException
    {
        MikhailovExtRenderListener extRenderListener;
        if (!processedRulings.containsKey(pageNumber))
        {
            extRenderListener = new MikhailovExtRenderListener();
            parser.processContent(pageNumber, extRenderListener);
            processedRulings.put(pageNumber, extRenderListener);
        }
        else
        {
            extRenderListener = processedRulings.get(pageNumber);
        }
        return extRenderListener;
    }


    private MikhailovTextExtractionStrategy processTextContent(int pageNumber) throws IOException
    {
        MikhailovTextExtractionStrategy textExtractionStrategy;
        if (!processedText.containsKey(pageNumber))
        {
            textExtractionStrategy = new MikhailovTextExtractionStrategy(reader.getPageRotation(pageNumber),
                                                                         reader.getPageSize(pageNumber).getWidth());
            parser.processContent(pageNumber, textExtractionStrategy);
            processedText.put(pageNumber, textExtractionStrategy);
        }
        else
        {
            textExtractionStrategy = processedText.get(pageNumber);
        }
        return textExtractionStrategy;
    }

    private ImageRegionExtractionStrategy processImageContent(int pageNumber) throws IOException
    {
        ImageRegionExtractionStrategy strategy;
        if (!processedImages.containsKey(pageNumber))
        {
            strategy = new ImageRegionExtractionStrategy();
            parser.processContent(pageNumber, strategy);
            processedImages.put(pageNumber, strategy);
        }
        else
        {
            strategy = processedImages.get(pageNumber);
        }
        return strategy;
    }
}
