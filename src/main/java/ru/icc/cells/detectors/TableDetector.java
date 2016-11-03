package ru.icc.cells.detectors;

import ru.icc.cells.common.TableBox;
import ru.icc.cells.common.TableRegion;
import ru.icc.cells.common.TextBlock;
import ru.icc.cells.common.TextLine;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TableDetector implements Detector<TableBox, TextBlock>
{
    private List<TextLine>    textLines    = new ArrayList<>();
    private List<TableRegion> tableRegions = new ArrayList<>();
    private List<TableBox>    tableBoxes   = new ArrayList<>();

    private TableDetectorConfiguration cnf;

    public TableDetector(TableDetectorConfiguration cnf)
    {
        if (cnf == null)
        {
            this.cnf = new TableDetectorConfiguration();
        }
        else
        {
            this.cnf = cnf;
        }
    }

    @Override
    public List<TableBox> detect(List<TextBlock> textBlocks)
    {
        textLines = new TextLineDetector(cnf.useSortedTextBlocks).detect(textBlocks);
        tableRegions = new TableRegionDetector(cnf.minTextLineGapProjectionIntersection, cnf.minWhitespaceWidth)
                .detect(textLines);
        tableBoxes = new TableBoxDetector(textLines, cnf.maxNonTableLinesBetweenRegions,
                                          cnf.minRegionGapProjectionIntersection, cnf.gapThreshold)
                .detect(tableRegions);

        stickTablesToKeyWords(textBlocks);

        return tableBoxes;
    }

    private void stickTablesToKeyWords(List<TextBlock> textBlocks)
    {
        List<TextBlock> tableKeyWordBlocks;
        tableKeyWordBlocks = getTableKeyWordBlocks(textBlocks);

        if (!tableKeyWordBlocks.isEmpty())
        {
            for (TableBox tableBox : tableBoxes)
            {
                List<TextBlock> upperBlocks = tableKeyWordBlocks.stream()
                                                                .filter(tb -> tb.getBottom() >= tableBox.getBottom())
                                                                .collect(Collectors.toList());
                if (!upperBlocks.isEmpty())
                {
                    TextBlock associatedBlock = upperBlocks.stream()
                                                           .min((tb1, tb2) -> Float.compare(
                                                                   tb1.getBottom() - tableBox.getBottom(),
                                                                   tb2.getBottom() - tableBox.getBottom()))
                                                           .orElse(null);
                    if (associatedBlock!=null)
                    {
                        TextBlock associatedBlocks = new TextBlock();
                        associatedBlocks.add(associatedBlock);
                        textBlocks.stream()
                                  .filter(textBlock -> TextLineDetector.vProjection(associatedBlock, textBlock))
                                  .forEach(associatedBlocks::add);
                        tableBox.setAssociatedTableKeyWordBlock(associatedBlocks);
                        tableBox.setTop(associatedBlocks.getBottom() - 1);
                        tableKeyWordBlocks.remove(associatedBlock);
                    }
                }
            }
        }
    }

    private List<TextBlock> getTableKeyWordBlocks(List<TextBlock> textBlocks) {
        List<TextBlock> tableKeyWordBlocks;

        Pattern tablePattern  = Pattern.compile(buildWhitespaceDelimiterRegex("table"));
        Pattern tablesPattern = Pattern.compile(buildWhitespaceDelimiterRegex("tables") + "\\s+");
        tableKeyWordBlocks = textBlocks
                .stream()
                .filter(tb ->
                        {
                            String  text          = tb.getText().toLowerCase().trim();
                            Matcher m             = tablePattern.matcher(text);
                            boolean containsTable = m.find();
                            if (containsTable)
                            {
                                int tableStart = m.start();
                                m = tablesPattern.matcher(text);
                                boolean containsTables = m.find();
                                if (containsTables)
                                {
                                    int tablesStart = m.start();
                                    return tableStart == 0 && tablesStart != 0;
                                }
                                else
                                {
                                    return tableStart == 0;
                                }
                            }
                            else
                            {
                                return false;
                            }
                        })
                .collect(Collectors.toList());

        Pattern exhibitPattern = Pattern.compile(buildWhitespaceDelimiterRegex("exhibit"));
        tableKeyWordBlocks.addAll(textBlocks
                                          .stream()
                                          .filter(tb ->
                                                  {
                                                      String  text = tb.getText().toLowerCase().trim();
                                                      Matcher m    = exhibitPattern.matcher(text);
                                                      return (m.find() && m.start() == 0);
                                                  }).collect(Collectors.toList()));
        return tableKeyWordBlocks;
    }

    private String buildWhitespaceDelimiterRegex(String str) {
        if (str == null || str.isEmpty()) return str;
        StringBuilder regexBuilder = new StringBuilder();
        regexBuilder.append(str.charAt(0));
        String ws = "\\s*";
        for (int i = 1; i < str.length(); i++)
        {
            regexBuilder.append(ws);
            regexBuilder.append(str.charAt(i));
        }
        return regexBuilder.toString();
    }

    public List<TextLine> getTextLines()
    {
        return textLines;
    }

    public List<TableRegion> getTableRegions()
    {
        return tableRegions;
    }

    public List<TableBox> getTableBoxes()
    {
        return tableBoxes;
    }
}
