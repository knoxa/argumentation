package order;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class ContextBuilder implements ContentHandler {
	
	private Map<String, Set<String>> context;
	String objectName = null;
	StringBuffer attributeText;
	Boolean inAttribute = false;
	private int objectNumber;

	public ContextBuilder() {

		objectNumber = 0;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

		if ( inAttribute ) {
			
			attributeText.append(ch, start, length);
		}
	}

	@Override
	public void endDocument() throws SAXException {

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if ( qName.equals("object") ) {
			
			objectName = null;
		}
		else if ( qName.equals("attribute") ) {
			
			String attr = attributeText.toString();
			Set<String> objects = context.get(attr);
			
			if ( objects == null )  objects = new HashSet<String>();
			objects.add(objectName);
			
			context.put(attr, objects);			
			inAttribute = false;
		}
	}

	@Override
	public void endPrefixMapping(String arg0) throws SAXException {

	}

	@Override
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {

	}

	@Override
	public void processingInstruction(String arg0, String arg1) throws SAXException {

	}

	@Override
	public void setDocumentLocator(Locator arg0) {

	}

	@Override
	public void skippedEntity(String arg0) throws SAXException {

	}

	@Override
	public void startDocument() throws SAXException {

	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException {

		if ( qName.equals("context") ) {
			
			context = new HashMap<String, Set<String>>();
		}
		else if ( qName.equals("object") ) {
			
			objectName = attr.getValue("name");
			if ( objectName == null ) objectName = String.valueOf(++objectNumber);
		}
		else if ( qName.equals("attribute") ) {
			
			inAttribute = true;
			attributeText = new StringBuffer();
		}
	}

	@Override
	public void startPrefixMapping(String arg0, String arg1) throws SAXException {

	}

	public Map<String, Set<String>> getContext() {
		return context;
	}

}
