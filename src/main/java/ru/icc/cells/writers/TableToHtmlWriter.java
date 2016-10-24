package ru.icc.cells.writers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.icc.cells.common.table.Cell;
import ru.icc.cells.common.table.Row;
import ru.icc.cells.common.table.Table;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class TableToHtmlWriter {

    public void write(Table table, String path) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder        docBuilder = docFactory.newDocumentBuilder();
        Document               doc        = docBuilder.newDocument();

        Element root = doc.createElement("table");
        root.setAttribute("border", "1px solid black");
        doc.appendChild(root);

        for (Row row : table.getRows()) {
            if (!row.getCells().isEmpty()) {
                Element tr = doc.createElement("tr");

                for (Cell cell : row.getCells()) {
                    Element td = doc.createElement("td");
                    if (cell.getRowHeight() > 1) {
                        td.setAttribute("rowspan", String.valueOf(cell.getRowHeight()));
                    }
                    if (cell.getColumnWidth()>1){
                        td.setAttribute("colspan", String.valueOf(cell.getColumnWidth()));
                    }
                    td.appendChild(doc.createTextNode(cell.getText()));
                    tr.appendChild(td);
                }
                root.appendChild(tr);
            }
        }


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer        transformer        = transformerFactory.newTransformer();
        DOMSource          source             = new DOMSource(doc);
        StreamResult       result             = new StreamResult(path);

        transformer.transform(source, result);
    }

}
