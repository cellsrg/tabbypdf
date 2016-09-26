package ru.icc.cells.utils.content;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;

/**
 * Created by sunveil on 23/06/16.
 */
public class PdfParser {

    private String                     path     = "";
    private PdfReader                  reader   = null;
    private PdfReaderContentParser     parser   = null;
    private MikhailovExtRenderListener strategy = null;
    private PdfWriter                  writer   = null;
    private PdfStamper                 stamper  = null;

    public PdfParser(String path) {
        this.path = path;
    }

    public boolean open() {

        return false;
    }


}
