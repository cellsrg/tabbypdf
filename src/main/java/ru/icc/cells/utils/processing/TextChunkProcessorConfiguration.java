package ru.icc.cells.utils.processing;

import ru.icc.cells.utils.processing.filter.ChunkFilter;
import ru.icc.cells.utils.processing.filter.bi.BiFilter;
import ru.icc.cells.utils.processing.filter.tri.TriFilter;

import java.util.ArrayList;
import java.util.List;

public class TextChunkProcessorConfiguration {
    private List<BiFilter>  biFilters  = new ArrayList<>();
    private List<TriFilter> triFilters = new ArrayList<>();

    public TextChunkProcessorConfiguration addFilter(ChunkFilter filter) {
        Class<?> filterClass = filter.getClass();
        if (filterClass.getSuperclass().equals(BiFilter.class)) {
            biFilters.add(((BiFilter) filter));
        } else if (filterClass.getSuperclass().equals(TriFilter.class)) {
            triFilters.add(((TriFilter) filter));
        }
        return this;
    }

    public List<BiFilter> getBiFilters() {
        return biFilters;
    }

    public List<TriFilter> getTriFilters() {
        return triFilters;
    }
}
