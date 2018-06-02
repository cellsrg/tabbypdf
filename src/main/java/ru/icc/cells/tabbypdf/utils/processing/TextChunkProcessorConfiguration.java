package ru.icc.cells.tabbypdf.utils.processing;

import lombok.Getter;
import ru.icc.cells.tabbypdf.utils.processing.filter.Heuristic;
import ru.icc.cells.tabbypdf.utils.processing.filter.bi.BiHeuristic;
import ru.icc.cells.tabbypdf.utils.processing.filter.tri.TriHeuristic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class TextChunkProcessorConfiguration {
    private List<BiHeuristic>  biHeuristics       = new ArrayList<>();
    private List<TriHeuristic> triHeuristics      = new ArrayList<>();
    private List<String>       stringsToReplace   = new ArrayList<>();
    private boolean            removeColons       = false;
    private boolean            useCharacterChunks = false;

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

    public TextChunkProcessorConfiguration setRemoveColons(boolean removeColons) {
        this.removeColons = removeColons;
        return this;
    }


    public TextChunkProcessorConfiguration setUseCharacterChunks(boolean useCharacterChunks) {
        this.useCharacterChunks = useCharacterChunks;
        return this;
    }
}
