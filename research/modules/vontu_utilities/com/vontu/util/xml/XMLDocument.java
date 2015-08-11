// 
// Decompiled by Procyon v0.5.29
// 

package com.vontu.util.xml;

import org.xml.sax.SAXParseException;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.Collection;
import org.w3c.dom.Node;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.NodeList;
import javax.xml.xpath.XPathFactory;
import javax.xml.namespace.NamespaceContext;
import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.xml.sax.InputSource;
import com.vontu.util.URILoader;
import java.net.URI;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;

public class XMLDocument implements ErrorHandler
{
    private Document _document;
    
    public XMLDocument(final URI documentURI, final boolean validate) throws XMLDocumentException {
        InputStream xmlInputStream = null;
        try {
            xmlInputStream = URILoader.load(documentURI);
            this.parseDocument(new InputSource(xmlInputStream), validate);
        }
        catch (FileNotFoundException e) {
            throw new XMLDocumentException("Could not find XML document: " + documentURI.toString(), e);
        }
        catch (IOException e2) {
            throw new XMLDocumentException("Error opening XML document: " + documentURI.toString(), e2);
        }
        finally {
            if (xmlInputStream != null) {
                try {
                    xmlInputStream.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    public XMLDocument(final Reader documentReader, final boolean validate) throws XMLDocumentException {
        this.parseDocument(new InputSource(documentReader), validate);
    }
    
    public XMLDocument(final InputStream documentStream, final boolean validate) throws XMLDocumentException {
        this.parseDocument(new InputSource(documentStream), validate);
    }
    
    public XMLDocument(final Document document) {
        this._document = document;
    }
    
    public Document getDocument() {
        return this._document;
    }
    
    public Object evaluateXPath(final String xPathExpression) throws XMLDocumentException {
        return this.evaluateXPath(xPathExpression, null);
    }
    
    public Object evaluateXPath(final String xPathExpression, final NamespaceContext namespace) throws XMLDocumentException {
        try {
            final XPathFactory xpathfactory = XPathFactory.newInstance();
            final XPath xpath = xpathfactory.newXPath();
            if (namespace != null) {
                xpath.setNamespaceContext(namespace);
            }
            final NodeList nodeList = (NodeList)xpath.evaluate(xPathExpression, this._document.getDocumentElement(), XPathConstants.NODESET);
            if (nodeList.getLength() != 1) {
                return nodeList;
            }
            final Node node = nodeList.item(0);
            if (node.getNodeType() == 2 || node.getNodeType() == 3) {
                return node.getNodeValue();
            }
            if (node.getNodeType() != 1) {
                return node;
            }
            if (node.getChildNodes().getLength() == 1 && node.getFirstChild().getNodeType() == 3) {
                return node.getFirstChild().getNodeValue();
            }
            return node;
        }
        catch (XPathExpressionException e) {
            throw new XMLDocumentException("Error evaluating XPath expression: " + xPathExpression, e);
        }
    }
    
    public Object DOMNodeToJava(final String xPathExpression, final DOMNodeToJava converter) throws XMLDocumentException {
        final Node node = (Node)this.evaluateXPath(xPathExpression);
        try {
            return converter.convert(node);
        }
        catch (DOMNodeToJavaException e) {
            throw new XMLDocumentException("Error converting node to a Java object", e);
        }
    }
    
    public <T> Collection<T> DOMNodesToJava(final String xPathExpression, final DOMNodeToJava<T> converter) throws XMLDocumentException {
        return this.DOMNodesToJava(xPathExpression, converter, null);
    }
    
    public <T> Collection<T> DOMNodesToJava(final String xPathExpression, final DOMNodeToJava<T> converter, final NamespaceContext namespace) throws XMLDocumentException {
        final ArrayList<T> objects = new ArrayList<T>();
        final Object xPathResult = this.evaluateXPath(xPathExpression, namespace);
        try {
            if (xPathResult instanceof Node) {
                objects.add(converter.convert((Node)xPathResult));
            }
            else if (xPathResult instanceof NodeList) {
                final NodeList nodeList = (NodeList)xPathResult;
                for (int i = 0; i < nodeList.getLength(); ++i) {
                    objects.add(converter.convert(nodeList.item(i)));
                }
            }
            else if (xPathResult instanceof String) {
                final Node node = this._document.createTextNode((String)xPathResult);
                objects.add(converter.convert(node));
            }
            return objects;
        }
        catch (DOMNodeToJavaException e) {
            throw new XMLDocumentException("Error converting node to a Java object", e);
        }
    }
    
    private void parseDocument(final InputSource source, final boolean validate) throws XMLDocumentException {
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setValidating(validate);
        if (!validate) {
            documentBuilderFactory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.FALSE);
        }
        try {
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setErrorHandler(this);
            this._document = documentBuilder.parse(source);
        }
        catch (ParserConfigurationException e) {
            throw new XMLDocumentException("XML parser configuration error", e);
        }
        catch (IOException e2) {
            throw new XMLDocumentException("Error reading XML document", e2);
        }
        catch (SAXException e3) {
            throw new XMLDocumentException("Error parsing XML document", e3);
        }
    }
    
    @Override
    public void error(final SAXParseException exception) throws SAXException {
        throw exception;
    }
    
    @Override
    public void fatalError(final SAXParseException exception) throws SAXException {
        throw exception;
    }
    
    @Override
    public void warning(final SAXParseException exception) throws SAXException {
        throw exception;
    }
}
