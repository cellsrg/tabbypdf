package ru.icc.cells.utils.processing;

import ru.icc.cells.utils.processing.filter.Heuristic;
import ru.icc.cells.utils.processing.filter.bi.BiHeuristic;
import ru.icc.cells.utils.processing.filter.tri.TriHeuristic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextChunkProcessorConfiguration {
    private List<BiHeuristic>  biHeuristics     = new ArrayList<>();
    private List<TriHeuristic> triHeuristics    = new ArrayList<>();
    private List<String>       stringsToReplace = new ArrayList<>();

    public TextChunkProcessorConfiguration addFilter(Heuristic filter) {
        Class<?> filterClass = filter.getClass();
        if (filterClass.getSuperclass().equals(BiHeuristic.class)) {
            biHeuristics.add(((BiHeuristic) filter));
        } else if (filterClass.getSuperclass().equals(TriHeuristic.class)) {
            triHeuristics.add(((TriHeuristic) filter));
        }
        return this;
    }

    public TextChunkProcessorConfiguration addStringsToReplace(String[] strings) {
        stringsToReplace.addAll(Arrays.asList(strings));
        return this;
    }

    public List<BiHeuristic> getBiHeuristics() {
        return biHeuristics;
    }

    public List<TriHeuristic> getTriHeuristics() {
        return triHeuristics;
    }

    public List<String> getStringsToReplace() {
        return stringsToReplace;
    }
}
