package ru.icc.cells.detectors;

import ru.icc.cells.common.*;
import ru.icc.cells.utils.processing.TextChunkProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        textLines = new TextLineDetector().detect(textBlocks);
        tableRegions = new TableRegionDetector(cnf.minTextLineGapProjectionIntersection, cnf.minWhitespaceWidth)
                .detect(textLines);
        tableBoxes = new TableBoxDetector(textLines, cnf.maxNonTableLinesBetweenRegions,
                                          cnf.minRegionGapProjectionIntersection, cnf.gapThreshold)
                .detect(tableRegions);

//        stickTablesToKeyWords(textBlocks);

        return tableBoxes;
    }

    private void stickTablesToKeyWords(List<TextBlock> textBlocks)
    {
        List<TextBlock> tableKeyWordBlocks = textBlocks.stream()
                                                       .filter(textBlock -> textBlock.getText()
                                                                                     .toLowerCase()
                                                                                     .contains("table"))
                                                       .collect(Collectors.toList());
        for (TextBlock tableKeyWordBlock : tableKeyWordBlocks)
        {
            List<TextBlock> blocksOnSameLine = TextChunkProcessor.getBlocksOnSameLine(textBlocks, tableKeyWordBlock);
            float bottomBorder =
                    Collections.min(blocksOnSameLine, (b1, b2) -> Float.compare(b1.getBottom(), b2.getBottom()))
                               .getBottom() - 1;
            List<TableBox> bottomTableBoxes = tableBoxes.stream()
                                                        .filter(tableBox -> tableBox.getTop() <= bottomBorder)
                                                        .collect(Collectors.toList());
            if (!bottomTableBoxes.isEmpty())
            {
                Collections.min(bottomTableBoxes,
                                (b1, b2) -> Float.compare(bottomBorder - b1.getTop(), bottomBorder - b1.getTop()))
                           .setTop(bottomBorder);
            }
        }
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
