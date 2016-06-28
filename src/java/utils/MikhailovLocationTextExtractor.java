package utils;

import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.DocumentFont;
import com.itextpdf.text.pdf.parser.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sunveil on 27/06/16.
 */
public class MikhailovLocationTextExtractor implements TextExtractionStrategy {

    static boolean DUMP_STATE = false;

    private final ArrayList<TextChunk> locationalResult = new ArrayList<TextChunk>();

    private final ArrayList<TextChunk> locationalChunkResult = new ArrayList<TextChunk>();

    private final ArrayList<TextChunk> locationalJChunkResult = new ArrayList<TextChunk>();

    private final ArrayList<TextChunk> locationalWordResult = new ArrayList<TextChunk>();

    private final ArrayList<Rectangle> locationalTextBlocks = new ArrayList<Rectangle>();

    private ArrayList<Line> lines = null;

    int rotation = 0;
    float width = 0;

    public MikhailovLocationTextExtractor(int rotation, float width) {
        this.rotation = rotation;
        this.width = width;
        if (rotation != 0){
            System.out.println(rotation);
        }
    }


    public void beginTextBlock(){
    }


    public void endTextBlock(){
    }

    public ArrayList<TextChunk> getLocationalResult(){
        return locationalResult;
    }


    private boolean startsWithSpace(String str){
        if (str.length() == 0) return false;
        return str.charAt(0) == ' ';
    }

    private boolean endsWithSpace(String str){
        if (str.length() == 0) return false;
        return str.charAt(str.length()-1) == ' ';
    }

    private boolean isEmptyChunk(String str){
        if (str.length() == 0 || str == "" || str.charAt(str.length()-1) == ' ')
            return true;
        else
            return false;
    }

    private List<TextChunk> filterTextChunks(List<TextChunk> textChunks, TextChunkFilter filter){
        if (filter == null)
            return textChunks;

        List<TextChunk> filtered = new ArrayList<TextChunk>();
        for (TextChunk textChunk : textChunks) {
            if (filter.accept(textChunk))
                filtered.add(textChunk);
        }
        return filtered;
    }

    protected boolean isChunkAtWordBoundary(TextChunk chunk, TextChunk previousChunk){
        if (chunk.getCharSpaceWidth() < 0.1f)
            return false;

        float dist = chunk.distanceFromEndOf(previousChunk);

        if (dist < -chunk.getCharSpaceWidth() || dist > chunk.getCharSpaceWidth() / 2.0f)
            return true;

        return false;
    }

    protected boolean isChunkAtSpace(TextChunk chunk, TextChunk previousChunk){
        if (chunk == previousChunk) return false;
        float dist = chunk.distanceFromEndOf(previousChunk);
        float sp = chunk.getCharSpaceWidth();
        if (sp > 56){
            sp = sp / 5.0f;
        } else
        if (sp > 14){
            sp += 1;
        } else
        if (sp > 5){
            sp+=0.1;
        } else
        {
            sp = 5;
        }
        if (Math.abs(dist) > sp)
            return true;
        return false;
    }

    protected boolean isDiffFonts(TextChunk chunk, TextChunk previousChunk){
        return false;
    }

    public String getResultantText(TextChunkFilter chunkFilter){
        if (DUMP_STATE) dumpState();

        List<TextChunk> filteredTextChunks = filterTextChunks(locationalResult, chunkFilter);
        Collections.sort(filteredTextChunks);

        StringBuffer sb = new StringBuffer();
        TextChunk lastChunk = null;
        for (TextChunk chunk : filteredTextChunks) {

            if (lastChunk == null){
                sb.append(chunk.text);
            } else {
                if (chunk.sameLine(lastChunk)){
                    if (isChunkAtWordBoundary(chunk, lastChunk) && !startsWithSpace(chunk.text) && !endsWithSpace(lastChunk.text))
                        sb.append(' ');
                    sb.append(chunk.text);
                } else {
                    sb.append('\n');
                    sb.append(chunk.text);
                }
            }
            lastChunk = chunk;
        }

        return sb.toString();
    }

    public ArrayList<TextChunk> getResultantWordLocation(ArrayList<Line> lines){
        this.lines = lines;
        return getResultantWordLocation((TextChunkFilter)null);
    }

    public boolean clearChunk(TextChunk c){
        for (int i =0; i < c.getText().length(); i++){
            if (c.getText().toCharArray()[i] != '-' ){
                return true;
            }
        }
        return false;
    }

    public boolean vSplit(TextChunk t1, TextChunk t2){
        if (rotation != 0)
            return false;
        if (lines != null)
            for (Line l: lines){
                float x1 =  (float)l.getBasePoints().get(0).getX();
                float x2 =  (float)l.getBasePoints().get(1).getX();
                float t1x = t1.getEndLocation().get(0);
                float t2x = t2.getStartLocation().get(0);
                float t1y1 = t1.getRightTopPoint().get(1);
                float t1y2 = t1.getStartLocation().get(1);
                float y1 =  (float) Math.max(l.getBasePoints().get(0).getY(), (float)l.getBasePoints().get(1).getY());
                float y2 =  (float) Math.min(l.getBasePoints().get(0).getY(), (float)l.getBasePoints().get(1).getY());
                if (x1 == x2 && (t1x <= x1) && (t2x >= x2) &&
                        (Math.min(t1y1, y1) - Math.max(t1y2, y2)) > 0){
                    return true;
                }
            }
        return false;
    }

    public ArrayList<TextChunk> getResultantWordLocation(TextChunkFilter chunkFilter){

        locationalWordResult.clear();
        if (DUMP_STATE) dumpState();
        List<TextChunk> filteredTextChunks = filterTextChunks(locationalResult, chunkFilter);
        Vector start = null;
        Vector end = null;
        boolean isWord = false;
        StringBuffer lr = new StringBuffer();
        TextChunk lastChunk = null;
        int order = 0;
        TextChunk tmpChunk = null;

        Font f = null;

        for (TextChunk chunk : filteredTextChunks) {

            if (chunk.getText().equals("¦")){
                chunk.text = "";
            }
            if (chunk.getText().equals("&")) {
                chunk.text = "&amp;";
            } else if (chunk.getText().equals("<")) {
                chunk.text = "&lt;";
            } else if (chunk.getText().equals(">")) {
                chunk.text = "&gt;";
            } else if (chunk.getText().equals("\"")) {
                chunk.text = "&quot;";
            } else if (chunk.getText().equals("‘")) {
                chunk.text = "'";
            }

            if (lastChunk == null) {
                lastChunk = chunk;
                continue;
            }
            if ((!isChunkAtSpace(chunk, lastChunk) || lastChunk.getText().equals("•")) && chunk.sameLine(lastChunk) && !vSplit(lastChunk, chunk)){
                if (!isWord){
                    if ( !isEmptyChunk(lastChunk.getText()) ) {
                        start = lastChunk.getStartLocation();
                    } else {
                        lr.append(chunk.text);
                        lastChunk = chunk;
                        start = chunk.getStartLocation();
                        continue;
                    }
                    if ( !chunk.sameLine(lastChunk)) {
                        start = chunk.getStartLocation();
                        lr.append(chunk.text);
                        lastChunk = chunk;
                        continue;
                    }
                    lr.append(lastChunk.text);
                    isWord = true;
                } else {
                    lr.append(lastChunk.text);
                    f = lastChunk.getFont();
                    isWord = true;
                }
                end = chunk.getEndLocation();
            } else {
                if (lr.length() > 0) {
                    lr.append(lastChunk.text);
                    f = lastChunk.getFont();
                }
                if ( isWord ){
                    isWord = false;
                    tmpChunk = new TextChunk(lr.toString(), start, end, chunk.getCharSpaceWidth());
                    tmpChunk.setOrder(order);
                    tmpChunk.setRightTopPoint(lastChunk.getRightTopPoint());
                    tmpChunk.setFont(lastChunk.getFont());
                    locationalWordResult.add(tmpChunk);
                    order++;
                    lr.delete(0, lr.length());
                } else if (!isEmptyChunk(lastChunk.getText())) {
                    isWord = false;
                    tmpChunk = new TextChunk(lastChunk.getText(), lastChunk.getStartLocation(), lastChunk.getEndLocation(), chunk.getCharSpaceWidth());
                    tmpChunk.setOrder(order);
                    tmpChunk.setRightTopPoint(lastChunk.getRightTopPoint());
                    tmpChunk.setFont(lastChunk.getFont());
                    locationalWordResult.add(tmpChunk);
                    order++;
                }
                if ( chunk == filteredTextChunks.get(filteredTextChunks.size() - 1) && !isEmptyChunk(lastChunk.getText())) {
                    isWord = false;
                    tmpChunk = new TextChunk(chunk.getText(), chunk.getStartLocation(), chunk.getEndLocation(), chunk.getCharSpaceWidth());
                    tmpChunk.setOrder(order);
                    tmpChunk.setRightTopPoint(chunk.getRightTopPoint());
                    tmpChunk.setFont(lastChunk.getFont());
                    locationalWordResult.add(tmpChunk);
                    order++;
                }
            }
            if (!chunk.equals(" ")) {
                lastChunk = chunk;
            }
        }
        if (isWord) {
            lr.append(lastChunk.text);
            tmpChunk = new TextChunk(lr.toString(), start, lastChunk.getEndLocation(), lastChunk.getCharSpaceWidth());
            tmpChunk.setRightTopPoint(lastChunk.getRightTopPoint());
            tmpChunk.setOrder(order);
            tmpChunk.setFont(lastChunk.getFont());
            if (clearChunk(tmpChunk)) {
                locationalWordResult.add(tmpChunk);
            }

        }
        return locationalWordResult;
    }

    /**
     * Returns the result so far.
     * @return  a String with the resulting text.
     */

    public String getResultantText(){
        return getResultantText(null);
    }

    /** Used for debugging only */
    private void dumpState(){
        for (Iterator<TextChunk> iterator = locationalResult.iterator(); iterator.hasNext(); ) {
            TextChunk location = (TextChunk) iterator.next();

            location.printDiagnostics();

            System.out.println();
        }

    }

    public void renderText(TextRenderInfo renderInfo) {

        List<TextRenderInfo> rInfos = renderInfo.getCharacterRenderInfos();

        if (this.rotation == 90){
            for (TextRenderInfo rI : rInfos){
                //rI.
            }
        }

        for (TextRenderInfo rI : rInfos){
            LineSegment segment = rI.getBaseline();
            LineSegment topSegment = rI.getAscentLine();
            try {
                if (rI.getText().equals(" ")){
                    continue;
                }
                if (rI.getRise() != 0){ // remove the rise from the baseline - we do this because the text from a super/subscript render operations should probably be considered as part of the baseline of the text the super/sub is relative to
                    Matrix riseOffsetTransform = new Matrix(0, -rI.getRise());
                    segment = segment.transformBy(ReflectionIText.getGs(rI).getCtm());
                    topSegment = topSegment.transformBy(ReflectionIText.getGs(rI).getCtm());
                }
                TextChunk location;
                if (rotation == 90){
                    location = new TextChunk(
                            rI.getText(),
                            (new Vector(segment.getStartPoint().get(1), width - segment.getStartPoint().get(0), 0)),
                            (new Vector(segment.getEndPoint().get(1), width - segment.getEndPoint().get(0), 0)),
                            rI.getSingleSpaceWidth());
                    location.setRightTopPoint(new Vector(topSegment.getEndPoint().get(1), width - topSegment.getEndPoint().get(0), 0));
                    GraphicsState gs = ReflectionIText.getGs(rI);
                    Font font = new Font(gs.getFont(), gs.getFontSize(), gs.getFont().getFontType(), gs.getFillColor());
                    location.setFont(font);
                } else {
                    location = new TextChunk(
                            rI.getText(),
                            segment.getStartPoint(),
                            segment.getEndPoint(),
                            rI.getSingleSpaceWidth());
                    location.setRightTopPoint(topSegment.getEndPoint());
                    GraphicsState gs = ReflectionIText.getGs(rI);
                    Font font = new Font(gs.getFont(), gs.getFontSize(), gs.getFont().getFontType(), gs.getFillColor());
                    location.setFont(font);
                }
                locationalResult.add(location);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        LineSegment segment = renderInfo.getAscentLine();
        LineSegment topSegment = renderInfo.getDescentLine();
        if (renderInfo.getRise() != 0){
            Matrix riseOffsetTransform = new Matrix(0, -renderInfo.getRise());
            segment = segment.transformBy(riseOffsetTransform);
        }
        GraphicsState gs = null;
        try {
            gs = ReflectionIText.getGs(renderInfo);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Font font = new Font(gs.getFont() , gs.getFontSize(), gs.getFont().getFontType(), gs.getFillColor() );
        TextChunk location = new TextChunk(renderInfo.getText(), segment.getStartPoint(), segment.getEndPoint(), renderInfo.getSingleSpaceWidth());
        location.setFont(font);
        location.setRightTopPoint(topSegment.getEndPoint());
        locationalChunkResult.add(location);
    }

    public static class TextChunk extends Chunk implements Comparable<TextChunk>{
        /** the text of the chunk */
        private String text;
        /** the starting location of the chunk */
        private final Vector startLocation;
        /** the ending location of the chunk */
        private final Vector endLocation;
        /** unit vector in the orientation of the chunk */
        private final Vector orientationVector;
        /** the orientation as a scalar for quick sorting */
        private final int orientationMagnitude;
        /** perpendicular distance to the orientation unit vector (i.e. the Y position in an unrotated coordinate system)
         * we round to the nearest integer to handle the fuzziness of comparing floats */
        private final int distPerpendicular;
        /** distance of the start of the chunk parallel to the orientation unit vector (i.e. the X position in an unrotated coordinate system) */
        private final float distParallelStart;
        /** distance of the end of the chunk parallel to the orientation unit vector (i.e. the X position in an unrotated coordinate system) */
        private final float distParallelEnd;
        /** the width of a single space character in the font of the chunk */
        private final float charSpaceWidth;
        private Vector rightTopPoint;
        private int order;
        private DocumentFont font;

        public void setChunkFont(DocumentFont font){
            this.font = font;
        }

        public DocumentFont getChunkFont(){
            return this.font;
        }


        public void setRightTopPoint(Vector rightTopPoint){
            this.rightTopPoint = rightTopPoint;
        }

        public Vector getRightTopPoint(){
            return rightTopPoint;
        }

        public void setOrder(int order){
            this.order = order;
        }

        public int getOrder(){
            return this.order;
        }

        public TextChunk(String string, Vector startLocation, Vector endLocation, float charSpaceWidth ) {
            this.text = string;
            this.startLocation = startLocation;
            this.endLocation = endLocation;
            this.charSpaceWidth = charSpaceWidth;

            Vector oVector = endLocation.subtract(startLocation);
            if (oVector.length() == 0) {
                oVector = new Vector(1, 0, 0);
            }
            orientationVector = oVector.normalize();
            orientationMagnitude = (int)(Math.atan2(orientationVector.get(Vector.I2), orientationVector.get(Vector.I1))*1000);

            Vector origin = new Vector(0,0,1);
            distPerpendicular = (int)(startLocation.subtract(origin)).cross(orientationVector).get(Vector.I3);

            distParallelStart = orientationVector.dot(startLocation);
            distParallelEnd = orientationVector.dot(endLocation);
        }


        public Vector getStartLocation(){
            return startLocation;
        }

        public Vector getEndLocation(){
            return endLocation;
        }


        public String getText(){
            return text;
        }

        public float getCharSpaceWidth() {
            return charSpaceWidth;
        }

        private void printDiagnostics(){
            System.out.println("Text (@" + startLocation + " -> " + endLocation + "): " + text);
            System.out.println("orientationMagnitude: " + orientationMagnitude);
            System.out.println("distPerpendicular: " + distPerpendicular);
            System.out.println("distParallel: " + distParallelStart);
        }


        public boolean sameLine(TextChunk as){
            if (orientationMagnitude != as.orientationMagnitude) return false;
            if (distPerpendicular != as.distPerpendicular) return false;
            return true;
        }

        public boolean sameLine2(TextChunk as){
            if (getStartLocation().get(1) != as.getStartLocation().get(1)) return false;
            return true;
        }

        public float distanceFromEndOf(TextChunk other){
            float distance = distParallelStart - other.distParallelEnd;
            return distance;
        }


        public int compareTo(TextChunk rhs) {
            if (this == rhs) return 0;

            int rslt;
            rslt = compareInts(orientationMagnitude, rhs.orientationMagnitude);
            if (rslt != 0) return rslt;

            rslt = compareInts(distPerpendicular, rhs.distPerpendicular);
            if (rslt != 0) return rslt;

            return Float.compare(distParallelStart, rhs.distParallelStart);
        }


        private static int compareInts(int int1, int int2){
            return int1 == int2 ? 0 : int1 < int2 ? -1 : 1;
        }


    }

    public void renderImage(ImageRenderInfo renderInfo) {
        // do nothing
    }

    public static interface TextChunkFilter{

        public boolean accept(TextChunk textChunk);
    }
}
