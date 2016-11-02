package ru.icc.cells.utils.content;

import com.itextpdf.text.pdf.parser.*;
import ru.icc.cells.common.TextChunk;

import java.util.*;
import java.util.stream.Collectors;

public class MikhailovTextExtractionStrategy implements TextExtractionStrategy
{

    private static boolean DUMP_STATE = false;

    private final ArrayList<TextChunk> locationalResult = new ArrayList<>();

    private final ArrayList<TextChunk> locationalChunkResult = new ArrayList<>();

    private final ArrayList<TextChunk> locationalWordResult = new ArrayList<>();

    private final Map<TextChunk, List<TextChunk>> originalCharacterChunksMapping = new HashMap<>();

    private ArrayList<Line> lines = null;

    private int rotation = 0;

    private float width = 0;

    public MikhailovTextExtractionStrategy(int rotation, float width)
    {
        this.rotation = rotation;
        this.width = width;
    }

    public ArrayList<TextChunk> getLocationalResult()
    {
        return locationalResult;
    }

    public ArrayList<TextChunk> getLocationalChunkResult()
    {
        return locationalChunkResult;
    }

    public Map<TextChunk, List<TextChunk>> getOriginalCharacterChunksMapping()
    {
        return originalCharacterChunksMapping;
    }

    /**
     * Returns the result so far.
     *
     * @return a String with the resulting text.
     */
    @Override
    public String getResultantText()
    {
        return getResultantText(null);
    }

    @Override
    public void beginTextBlock()
    {}


    @Override
    public void endTextBlock()
    {}

    @Override
    public void renderImage(ImageRenderInfo renderInfo)
    {
        // do nothing
    }

    @Override
    public void renderText(TextRenderInfo renderInfo)
    {
        List<TextChunk> characterChunks = new ArrayList<>();
        renderInfo.getCharacterRenderInfos()
                  .stream()//extract chunks per each character
                  .filter(rI -> !rI.getText().isEmpty())
                  .forEachOrdered(rI ->
                                  {
                                      TextChunk characterChunk = extractLocation(rI);
                                      locationalResult.add(characterChunk);
                                      characterChunks.add(characterChunk);
                                  });
        TextChunk originChunk = extractLocation(renderInfo);
        originalCharacterChunksMapping.put(originChunk, characterChunks);
        locationalChunkResult.add(originChunk); //extract chunk
    }

    private TextChunk extractLocation(TextRenderInfo renderInfo)
    {
        LineSegment topSegment = renderInfo.getAscentLine();
        LineSegment btmSegment = renderInfo.getDescentLine();
        if (renderInfo.getRise() != 0)
        {
            // remove the rise from the baseline - we do this because the text from a super/subscript render operations should probably be considered as part of the baseline of the text the super/sub is relative to
            Matrix riseOffsetTransform = new Matrix(0, -renderInfo.getRise());
            btmSegment = btmSegment.transformBy(riseOffsetTransform);
            topSegment = topSegment.transformBy(riseOffsetTransform);
        }
        TextChunk location;
        float     left, bottom, right, top;
        if (rotation == 90)
        {  //transform chunk coordinates according to rotation angle
            left = btmSegment.getStartPoint().get(1);
            bottom = width - btmSegment.getStartPoint().get(0);
            right = topSegment.getEndPoint().get(1);
            top = width - topSegment.getEndPoint().get(0);
        }
        else
        {
            left = btmSegment.getStartPoint().get(0);
            bottom = btmSegment.getStartPoint().get(1);
            right = topSegment.getEndPoint().get(0);
            top = topSegment.getEndPoint().get(1);
        }

        if (right < left)
        {
            float tmp = right;
            right = left;
            left = tmp;
        }

        if (top < bottom)
        {
            float tmp = top;
            top = bottom;
            bottom = tmp;
        }
        location = new TextChunk(renderInfo.getText(), left, bottom, right, top, renderInfo.getSingleSpaceWidth());
        GraphicsState gs = ReflectionIText.getGs(renderInfo);
        location.setChunkFont(gs.getFont());
        location.setFontSize(gs.getFontSize());
        return location;
    }

    public interface TextChunkFilter
    {
        boolean accept(TextChunk textChunk);
    }

    private boolean startsWithSpace(String str)
    {
        return !str.isEmpty() && str.charAt(0) == ' ';
    }

    private boolean endsWithSpace(String str)
    {
        return !str.isEmpty() && str.charAt(str.length() - 1) == ' ';
    }

    private boolean isEmptyChunk(String str)
    {
        return str.isEmpty() || str.charAt(str.length() - 1) == ' ';
    }

    private List<TextChunk> filterTextChunks(List<TextChunk> textChunks, TextChunkFilter filter)
    {
        if (filter == null) return textChunks;
        return textChunks.stream().filter(filter::accept).collect(Collectors.toList());
    }

    private boolean isChunkAtWordBoundary(TextChunk chunk, TextChunk previousChunk)
    {
        if (chunk.getCharSpaceWidth() < 0.1f) return false;
        float dist = chunk.distanceFromEndOf(previousChunk);
        return dist < -chunk.getCharSpaceWidth() || dist > chunk.getCharSpaceWidth() / 2.0f;

    }

    private boolean isChunkAtSpace(TextChunk chunk, TextChunk previousChunk)
    {
        if (chunk == previousChunk) return false;
        float dist = chunk.distanceFromEndOf(previousChunk);
        float sp   = chunk.getCharSpaceWidth();
        if (sp > 56)
        {// TODO: 15.09.2016 What are that magic constants here and below?
            sp = sp / 5.0f;
        }
        else if (sp > 14)
        {
            sp += 1;
        }
        else if (sp > 5)
        {
            sp += 0.1;
        }
        else
        {
            sp = 5;
        }
        return Math.abs(dist) > sp;
    }

    protected boolean isDiffFonts(TextChunk chunk, TextChunk previousChunk)
    {
        return false;
    }

    public String getResultantText(TextChunkFilter chunkFilter)
    {
        if (DUMP_STATE) dumpState();
        List<TextChunk> filteredTextChunks = filterTextChunks(locationalResult, chunkFilter);
        Collections.sort(filteredTextChunks);

        StringBuilder sb        = new StringBuilder();
        TextChunk     lastChunk = null;
        for (TextChunk chunk : filteredTextChunks)
        {
            if (lastChunk == null)
            {
                sb.append(chunk.getText());
            }
            else
            {
                if (chunk.sameLine(lastChunk))
                {
                    if (isChunkAtWordBoundary(chunk, lastChunk) && !startsWithSpace(chunk.getText()) &&
                        !endsWithSpace(lastChunk.getText())) sb.append(' ');
                    sb.append(chunk.getText());
                }
                else
                {
                    sb.append('\n');
                    sb.append(chunk.getText());
                }
            }
            lastChunk = chunk;
        }
        return sb.toString();
    }

    public ArrayList<TextChunk> getResultantWordLocation(ArrayList<Line> lines)
    {
        this.lines = lines;
        return getResultantWordLocation((TextChunkFilter) null);
    }

    public boolean clearChunk(TextChunk c)
    {
        for (int i = 0; i < c.getText().length(); i++)
        {
            if (c.getText().toCharArray()[i] != '-')
            {
                return true;
            }
        }
        return false;
    }

    private boolean vSplit(TextChunk t1, TextChunk t2)
    {
        if (rotation != 0) return false;
        if (lines != null) for (Line l : lines)
        {
            float x1   = (float) l.getBasePoints().get(0).getX();
            float x2   = (float) l.getBasePoints().get(1).getX();
            float t1x  = t1.getRight();
            float t2x  = t2.getLeft();
            float t1y1 = t1.getTop();
            float t1y2 = t1.getBottom();
            float y1   = (float) Math.max(l.getBasePoints().get(0).getY(), (float) l.getBasePoints().get(1).getY());
            float y2   = (float) Math.min(l.getBasePoints().get(0).getY(), (float) l.getBasePoints().get(1).getY());
            if (x1 == x2 && (t1x <= x1) && (t2x >= x2) && (Math.min(t1y1, y1) - Math.max(t1y2, y2)) > 0)
            {
                return true;
            }
        }
        return false;
    }

    public ArrayList<TextChunk> getResultantWordLocation(TextChunkFilter chunkFilter)
    {
        locationalWordResult.clear();
        if (DUMP_STATE) dumpState();
        List<TextChunk> chunks        = filterTextChunks(locationalResult, chunkFilter);
        Float           left          = null;
        Float           bottom        = null;
        Float           right         = null;
        Float           top           = null;
        TextChunk       previousChunk = null;
        StringBuilder   lr            = new StringBuilder();

        for (TextChunk chunk : chunks)
        {
            replaceSpecialChars(chunk);
            if (previousChunk == null)
            {
                previousChunk = chunk;
                continue;
            }

            if (left == null || bottom == null)
            {
                if (previousChunk.getText().isEmpty()) continue;
                left = previousChunk.getLeft();
                bottom = previousChunk.getBottom();
            }

            if ((!isChunkAtSpace(chunk, previousChunk) || previousChunk.getText().equals("•")) &&
                chunk.sameLine(previousChunk) && !vSplit(previousChunk, chunk) &&
                !previousChunk.getText().equals(" "))
            {
                lr.append(previousChunk.getText());
            }
            else
            {
                right = previousChunk.getRight();
                top = previousChunk.getTop();
                lr.append(previousChunk.getText());
                TextChunk newChunk =
                        new TextChunk(lr.toString(), left, bottom, right, top, previousChunk.getCharSpaceWidth());
                newChunk.setChunkFont(previousChunk.getChunkFont());
                locationalWordResult.add(newChunk);

                left = null;
                bottom = null;
                lr = new StringBuilder();
            }
            previousChunk = chunk;
        }

        TextChunk chunk = chunks.get(chunks.size() - 1);
        if (left != null && bottom != null)
        {
            right = chunk.getRight();
            top = chunk.getTop();

            lr.append(chunk.getText());
            TextChunk newChunk =
                    new TextChunk(lr.toString(), left, bottom, right, top, previousChunk.getCharSpaceWidth());
            newChunk.setChunkFont(chunk.getChunkFont());
            locationalWordResult.add(newChunk);
        }
        else
        {
            locationalWordResult.add(chunk);
        }
        return locationalWordResult;
    }

    private void replaceSpecialChars(TextChunk chunk)
    {
        if (chunk.getText().equals("¦"))
        {
            chunk.setText("");
        }
        else if (chunk.getText().equals("&"))
        {
            chunk.setText("&amp;");
        }
        else if (chunk.getText().equals("<"))
        {
            chunk.setText("&lt;");
        }
        else if (chunk.getText().equals(">"))
        {
            chunk.setText("&gt;");
        }
        else if (chunk.getText().equals("\""))
        {
            chunk.setText("&quot;");
        }
        else if (chunk.getText().equals("‘"))
        {
            chunk.setText("'");
        }
    }

    /**
     * Used for debugging only
     */
    private void dumpState()
    {
        for (TextChunk location : locationalResult)
        {
            location.printDiagnostics();
            System.out.println();
        }

    }
}
