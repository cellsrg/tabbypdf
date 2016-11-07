package ru.icc.cells.tabbypdf.writers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.icc.cells.tabbypdf.common.table.Cell;
import ru.icc.cells.tabbypdf.common.table.Row;
import ru.icc.cells.tabbypdf.common.table.Table;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;

public class TableToHtmlWriter
{

    public void write(Table table, String path) throws ParserConfigurationException, TransformerException, IOException
    {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder        docBuilder = docFactory.newDocumentBuilder();
        Document               doc        = docBuilder.newDocument();

        Element root = doc.createElement("table");
        root.setAttribute("border", "1px solid black");
        doc.appendChild(root);

        handleTable(table, doc, root);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer        transformer        = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource    source = new DOMSource(doc);
        StreamResult result = new StreamResult(path);

        transformer.transform(source, result);
    }

    private void handleTable(Table table, Document doc, Element root)
    {
        for (int rowNumber = 0; rowNumber < table.getRowsSize(); rowNumber++)
        {
            Row row = table.getRow(rowNumber);
            if (!row.getCells().isEmpty())
            {
                Element tr = doc.createElement("tr");
                for (Cell cell : row.getCells())
                {
                    tr.appendChild(createCellElement(doc, cell));
                }
                root.appendChild(tr);
            }
        }
    }

    private Element createCellElement(Document doc, Cell cell)
    {
        Element td = doc.createElement("td");
        handleSpan(cell, td);
        handleText(doc, cell, td);
        return td;
    }

    private void handleSpan(Cell cell, Element td)
    {
        if (cell.getRowHeight() > 1)
        {
            td.setAttribute("rowspan", String.valueOf(cell.getRowHeight()));
        }
        if (cell.getColumnWidth() > 1)
        {
            td.setAttribute("colspan", String.valueOf(cell.getColumnWidth()));
        }
    }

    private void handleText(Document doc, Cell cell, Element td)
    {
        String[] lines = cell.getText().split("\n");
        if (lines.length > 0)
        {
            td.appendChild(doc.createTextNode(lines[0]));
            if (lines.length > 1)
            {
                for (int i = 1; i < lines.length; i++)
                {
                    String line = lines[i];
                    if (!(line.matches("\\s*") || line.isEmpty()))
                    {
                        Element br = doc.createElement("br");
                        td.appendChild(br);
                        td.appendChild(doc.createTextNode(line));
                    }
                }
            }
        }
    }

}
