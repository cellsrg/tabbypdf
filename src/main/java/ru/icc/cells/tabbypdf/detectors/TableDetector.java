package ru.icc.cells.tabbypdf.detectors;

import lombok.Getter;
import ru.icc.cells.tabbypdf.common.TableBox;
import ru.icc.cells.tabbypdf.common.TableRegion;
import ru.icc.cells.tabbypdf.common.TextBlock;
import ru.icc.cells.tabbypdf.common.TextLine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
public class TableDetector implements Detector<TableBox, TextBlock> {
    private static final Pattern TABLE_PATTERN   = Pattern.compile(buildWhitespaceDelimiterRegex("table"));
    private static final Pattern TABLES_PATTERN  = Pattern.compile(buildWhitespaceDelimiterRegex("tables") + "\\s+");
    private static final Pattern EXHIBIT_PATTERN = Pattern.compile(buildWhitespaceDelimiterRegex("exhibit"));

    private List<TextLine>             textLines    = new ArrayList<>();
    private List<TableRegion>          tableRegions = new ArrayList<>();
    private List<TableBox>             tableBoxes   = new ArrayList<>();
    private TableDetectorConfiguration cnf;

    public TableDetector(TableDetectorConfiguration cnf) {
        if (cnf == null) {
            this.cnf = new TableDetectorConfiguration();
        } else {
            this.cnf = cnf;
        }
    }

    @Override
    public List<TableBox> detect(List<TextBlock> textBlocks) {
        textLines    = new TextLineDetector(cnf.useSortedTextBlocks).detect(textBlocks);
        tableRegions = new TableRegionDetector(cnf.minTextLineGapProjectionIntersection, cnf.minWhitespaceWidth)
            .detect(textLines);
        tableBoxes   = new TableBoxDetector(
            textLines,
            cnf.maxNonTableLinesBetweenRegions,
            cnf.minRegionGapProjectionIntersection,
            cnf.gapThreshold,
            cnf.maxDistanceBetweenRegions
        ).detect(tableRegions);

        stickTablesToKeyWords(textBlocks);

        return tableBoxes;
    }

    private void stickTablesToKeyWords(List<TextBlock> textBlocks) {
        List<TextBlock> tableKeyWordBlocks = getTableKeyWordBlocks(textBlocks);
        if (tableKeyWordBlocks.isEmpty()) {
            return;
        }

        for (TableBox tableBox : tableBoxes) {
            List<TextBlock> upperBlocks = tableKeyWordBlocks.stream()
                .filter(tb -> tb.getBottom() >= tableBox.getBottom())
                .collect(Collectors.toList());
            if (upperBlocks.isEmpty()) {
                continue;
            }

            TextBlock associatedBlock = upperBlocks.stream()
                .min(Comparator.comparingDouble(tb -> tb.getBottom() - tableBox.getBottom()))
                .orElse(null);

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

    private List<TextBlock> getTableKeyWordBlocks(List<TextBlock> textBlocks) {
        List<TextBlock> tableKeyWordBlocks = textBlocks.stream()
            .filter(tb -> textContainsTableKeywords(tb.getText().toLowerCase().trim()))
            .collect(Collectors.toList());

        if (tableKeyWordBlocks.isEmpty()) {
            tableKeyWordBlocks.addAll(textBlocks.stream()
                .filter(tb -> {
                    Matcher m = EXHIBIT_PATTERN.matcher(tb.getText().toLowerCase().trim());
                    return (m.find() && m.start() == 0);
                })
                .collect(Collectors.toList())
            );
        }
        return tableKeyWordBlocks;
    }

    private static boolean textContainsTableKeywords(String text) {
        Matcher m = TABLE_PATTERN.matcher(text);
        if (!m.find()) {
            return false;
        }

        int tableStart = m.start();
        m = TABLES_PATTERN.matcher(text);

        return m.find() ? tableStart == 0 && m.start() != 0 : tableStart == 0;
    }

    private static String buildWhitespaceDelimiterRegex(String str) {
        if (str == null || str.isEmpty()) return str;
        StringBuilder regexBuilder = new StringBuilder();
        regexBuilder.append(str.charAt(0));
        String ws = "\\s*";
        for (int i = 1; i < str.length(); i++) {
            regexBuilder.append(ws);
            regexBuilder.append(str.charAt(i));
        }
        return regexBuilder.toString();
    }
}
