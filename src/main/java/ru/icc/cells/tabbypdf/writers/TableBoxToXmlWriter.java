package ru.icc.cells.tabbypdf.writers;

import lombok.AllArgsConstructor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.icc.cells.tabbypdf.entities.TableBox;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.List;

@AllArgsConstructor
public class TableBoxToXmlWriter implements Writer<TableBox, String> {
    private String fileName;

    @Override
    public String write(List<TableBox> tables) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("document");
        rootElement.setAttribute("filename", fileName);
        doc.appendChild(rootElement);

        for (int i = 0; i < tables.size(); i++) {
            TableBox tableBox = tables.get(i);

            Element table = doc.createElement("table");
            table.setAttribute("id", String.valueOf(i + 1));

            Element region = doc.createElement("region");
            region.setAttribute("id", String.valueOf(1));
            region.setAttribute("page", String.valueOf(tableBox.getPageNumber() + 1));

            Element bBox = doc.createElement("bounding-box");
            bBox.setAttribute("x1", String.valueOf((int) tableBox.getLeft()));
            bBox.setAttribute("x2", String.valueOf((int) tableBox.getRight()));
            bBox.setAttribute("y1", String.valueOf((int) tableBox.getBottom()));
            bBox.setAttribute("y2", String.valueOf((int) tableBox.getTop()));

            region.appendChild(bBox);
            table.appendChild(region);
            rootElement.appendChild(table);
        }

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StringWriter stringWriter = new StringWriter();
            transformer.transform(source, new StreamResult(stringWriter));
            return stringWriter.getBuffer().toString().replaceAll("[\n\r]", "");
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}
