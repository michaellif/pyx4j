package com.pyx4j.essentials.server.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.ZapfDingbatsList;
import com.itextpdf.text.html.HtmlTags;
import com.itextpdf.text.html.Markup;
import com.itextpdf.text.pdf.PdfWriter;

public class HtmlToPDFParser extends DefaultHandler {

	public static void parse(File input, OutputStream output)
			throws FileNotFoundException, ParserConfigurationException,
			SAXException, IOException, DocumentException {
		parse(new FileInputStream(input), output);
	}

	public static void parse(InputStream input, OutputStream output)
			throws ParserConfigurationException, SAXException, IOException,
			DocumentException {
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		parser.parse(input, new HtmlToPDFParser(output));
	}

	class StyleTags {
		public static final String FONT_FAMILY = "font-family";
		public static final String FONT_SIZE = "font-size";
		public static final String FONT_WEIGHT = "font-weight";
		public static final String FONT_STYLE = "font-style";
		public static final String TEXT_DECORATION = "text-decoration";
		public static final String COLOR = "color";

		public static final String WIDTH = "width";
		public static final String HEIGHT = "height";

		public static final String LIST_STYLE_TYPE = "list-style-type";
		public static final String LIST_STYLE_TYPE_CIRCLE = "circle";
		public static final String LIST_STYLE_TYPE_DISC = "disc";
		public static final String LIST_STYLE_TYPE_SQUARE = "square";
		public static final String LIST_STYLE_TYPE_DECIMAL = "decimal";
		public static final String LIST_STYLE_TYPE_LOWER_ALPHA = "lower-alpha";
		public static final String LIST_STYLE_TYPE_UPPER_ALPHA = "upper-alpha";

		public static final String MARGIN_TOP = "margin-top";
		public static final String MARGIN_BOTTOM = "margin-bottom";
		public static final String MARGIN_LEFT = "margin-left";
		public static final String MARGIN_RIGHT = "margin-right";
	}

	class ZapfDingbats {
		public static final int BLACK_CIRCLE = 108;
		public static final int WHITE_CIRCLE = 109;
		public static final int BLACK_SQUARE = 110;
		public static final int WHITE_SQUARE = 111;
	}

	class Style extends HashMap<String, String> {

		private static final long serialVersionUID = 6685475202964712619L;

		public Style() {
			super();
		}

		public Style(Style baseStyle) {
			super(baseStyle);
		}

		public Style(String styleAttr) {
			if (styleAttr != null) {
				for (String styleEntry : styleAttr.split(";")) {
					String[] styleValues = styleEntry.split(":", 2);

					if (styleValues.length != 2)
						continue;

					put(styleValues[0].trim(), styleValues[1].trim());
				}
			}
		}

		public Font getFont() {
			String fontFamily = get(StyleTags.FONT_FAMILY);
			String fontSize = get(StyleTags.FONT_SIZE);
			String fontWeight = get(StyleTags.FONT_WEIGHT);
			String fontStyle = get(StyleTags.FONT_STYLE);
			String textDecor = get(StyleTags.TEXT_DECORATION);
			String textColor = get(StyleTags.COLOR);

			float size = Markup.parseLength(fontSize);
			if (size == 0)
				size = -1;

			int style = Font.NORMAL;
			if (fontStyle != null)
				style |= Font.getStyleValue(fontStyle);
			if (fontWeight != null)
				style |= Font.getStyleValue(fontWeight);
			if (textDecor != null)
				style |= Font.getStyleValue(textDecor);

			BaseColor color = Markup.decodeColor(textColor);

			return FontFactory.getFont(fontFamily, size, style, color);
		}

		public Rectangle getMargin() {
			float top = Markup.parseLength(get(StyleTags.MARGIN_TOP));
			float bottom = Markup.parseLength(get(StyleTags.MARGIN_BOTTOM));
			float left = Markup.parseLength(get(StyleTags.MARGIN_LEFT));
			float right = Markup.parseLength(get(StyleTags.MARGIN_RIGHT));

			return new Rectangle(left, bottom, right, top);
		}
	}

	class Node {
		String tag;
		Attributes attributes;
		Style style;
		ArrayList<Element> children;

		public Node(String tag, Attributes attributes, Style style) {
			super();
			this.tag = tag;
			this.attributes = attributes;
			this.style = style;
			this.children = null;
		}

		public String getTag() {
			return tag;
		}

		public Attributes getAttributes() {
			return attributes;
		}

		public Style getStyle() {
			return style;
		}

		public ArrayList<Element> getChildren() {
			return children;
		}

		public void addChild(Element element) {
			if (children == null)
				children = new ArrayList<Element>();
			children.add(element);
		}
	}

	Document document;
	StringBuilder characterBuffer;
	Stack<Node> nodes;

	Style baseStyle;
	HashMap<String, Integer> tagMap;
	HashMap<String, Style> stylesMap;
	HashMap<String, Style> classMap;

	URL baseUrl;

	Locator locator;

	private HtmlToPDFParser(OutputStream output) throws DocumentException {
		document = new Document();
		nodes = new Stack<Node>();

		// Get supported tags
		tagMap = getDefaultTags();

		// Define default style  for text
		baseStyle = new Style();
		
		// Get default tag styles
		stylesMap = getDefaultStyles();
		
		// Create placeholder for class styles
		classMap = new HashMap<String, Style>();

		// Initialize PDF Document Writer
		PdfWriter.getInstance(document, output);

		characterBuffer = new StringBuilder();
	}

	private HashMap<String, Integer> getDefaultTags() {
		HashMap<String, Integer> tags = new HashMap<String, Integer>();

		tags.put(HtmlTags.TITLE, Element.TITLE);
		tags.put(HtmlTags.META, Element.MARKED);

		for (String header : HtmlTags.H) {
			tags.put(header, Element.PARAGRAPH);
		}
		tags.put(HtmlTags.PARAGRAPH, Element.PARAGRAPH);
		tags.put(HtmlTags.DIV, Element.PARAGRAPH);
		tags.put(HtmlTags.SPAN, Element.PHRASE);
		tags.put(HtmlTags.ORDEREDLIST, Element.LIST);
		tags.put(HtmlTags.UNORDEREDLIST, Element.LIST);
		tags.put(HtmlTags.LISTITEM, Element.LISTITEM);

		tags.put(HtmlTags.CHUNK, Element.CHUNK);
		tags.put(HtmlTags.B, Element.CHUNK);
		tags.put(HtmlTags.I, Element.CHUNK);
		tags.put(HtmlTags.S, Element.CHUNK);
		tags.put(HtmlTags.NEWLINE, Element.CHUNK);

		// remove script and style content from output
		tags.put(HtmlTags.SCRIPT, -1);
		tags.put(HtmlTags.STYLE, -1);

		tags.put(HtmlTags.ANCHOR, Element.ANCHOR);
		tags.put(HtmlTags.IMAGE, Element.IMGTEMPLATE);

		return tags;
	}

	private HashMap<String, Style> getDefaultStyles() {
		HashMap<String, Style> styles = new HashMap<String, Style>();

		styles.put("h1", new Style("font-size: 2em; margin-top: 1em"));
		styles.put("h2", new Style("font-size: 1.5em; margin-top: 0.8em"));
		styles.put("h3", new Style("font-size: 1.17em; margin-top: 0.5em"));
		styles.put("h4", new Style("font-size: 1.12em; margin-top: 0.5em"));

		styles.put(HtmlTags.ORDEREDLIST, new Style("list-style-type: decimal"));
		styles.put(HtmlTags.UNORDEREDLIST, new Style("list-style-type: disc"));

		styles.put(HtmlTags.ANCHOR, new Style(
				"color: blue; text-decoration: underline"));

		styles.put(HtmlTags.B, new Style("font-weight: bold"));
		styles.put(HtmlTags.I, new Style("font-style: italic"));
		styles.put(HtmlTags.S, new Style("text-decoration: line-through"));

		return styles;
	}
	
	private HashMap<String, Style> parseCssStyles(String css) {
		HashMap<String, Style> styles = new HashMap<String, Style>();
		
		Pattern pattern = Pattern.compile("(.*?)\\{(.*?)\\}");
		Matcher matcher = pattern.matcher(css);
		while(matcher.find()) {
			String tags = matcher.group(1);
			String styleString = matcher.group(2);
			Style style = new Style(styleString);
			for (String tag : tags.split(",")) {
				styles.put(tag.trim(), style);
			}
		}
		
		return styles;
	}

	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equals(HtmlTags.BODY))
			document.open();

		if (!tagMap.containsKey(qName))
			return;

		// Handle text between two open tags
		if (characterBuffer.length() != 0) {
			String content = characterBuffer.toString();
			characterBuffer = new StringBuilder();

			if (!content.trim().isEmpty()) {
				Chunk chunk = new Chunk(content);
				if (nodes.empty()) {
					try {
						document.add(chunk);
					} catch (DocumentException e) {
						throw new SAXException(e);
					}
				} else {
					nodes.peek().addChild(chunk);
				}
			}
		}

		Style style = getStyle(qName, attributes);

		Node node = new Node(qName, new AttributesImpl(attributes), style);
		nodes.add(node);
	}

	private Style getStyle(String qName, Attributes attributes) {
		Style style;
		if (nodes.empty())
			style = new Style(baseStyle);
		else
			style = new Style(nodes.peek().getStyle());

		Style tagStyle = stylesMap.get(qName);
		if (tagStyle != null)
			style.putAll(tagStyle);

		String classAttr = attributes.getValue("class");
		if (classAttr != null) {
			Style classStyle = classMap.get(classAttr);
			if (classStyle != null)
				style.putAll(classStyle);
		}

		String styleAttr = attributes.getValue("style");
		if (styleAttr != null) {
			Style inlineStyle = new Style(styleAttr);
			style.putAll(inlineStyle);
		}

		return style;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String content = new String(ch, start, length).replaceAll("\\s+", " ");
		if (!content.trim().isEmpty())
			characterBuffer.append(content);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equals(HtmlTags.BODY))
			document.close();

		if (!tagMap.containsKey(qName))
			return;

		Node node = nodes.pop();
		Element element = null;

		Attributes attributes = node.getAttributes();

		Style style = node.getStyle();
		Font font = style.getFont();

		String content = characterBuffer.toString();
		characterBuffer = new StringBuilder();

		switch (tagMap.get(node.getTag())) {
		case Element.TITLE:
			document.addTitle(content);
			break;

		case Element.MARKED:
			String metaName = attributes.getValue("name");
			String metaHttpEquiv = attributes.getValue("http-equiv");
			String metaContent = attributes.getValue("content");

			if (metaContent == null)
				break;

			if (metaName != null) {
				if (metaName.equals("author")) {
					document.addAuthor(metaContent);
				} else if (metaName.equals("description")) {
					document.addSubject(metaContent);
				} else if (metaName.equals("keywords")) {
					document.addKeywords(metaContent);
				}
			} else if (metaHttpEquiv != null) {
				if (metaHttpEquiv.equals("Location")) {
					try {
						baseUrl = new URL(metaContent);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			}

			break;

		case Element.PARAGRAPH:
			Paragraph paragraph;
			if (node.getChildren() == null) {
				paragraph = new Paragraph(content, font);
			} else {
				paragraph = new Paragraph();
				paragraph.setFont(font);
				paragraph.addAll(node.getChildren());

				if (!content.trim().isEmpty())
					paragraph.add(content);
			}

			String align = attributes.getValue(HtmlTags.ALIGN);
			if (align != null) {
				paragraph.setAlignment(align);
			}

			Rectangle margin = style.getMargin();
			paragraph.setSpacingBefore(margin.getTop());
			paragraph.setSpacingAfter(margin.getBottom());
			paragraph.setIndentationLeft(margin.getLeft());
			paragraph.setIndentationRight(margin.getRight());

			element = paragraph;
			break;

		case Element.PHRASE:
			Phrase phrase;
			if (node.getChildren() == null) {
				phrase = new Phrase(content, font);
			} else {
				phrase = new Phrase();
				phrase.setFont(font);
				phrase.addAll(node.getChildren());

				if (!content.trim().isEmpty())
					phrase.add(content);
			}

			element = phrase;
			break;

		case Element.LIST:
			List list;

			String listStyleAttr = style.get(StyleTags.LIST_STYLE_TYPE);
			if (listStyleAttr == null) {
				list = new List(List.UNORDERED);
			} else if (listStyleAttr.equals(StyleTags.LIST_STYLE_TYPE_DECIMAL)) {
				list = new List(List.ORDERED, List.NUMERICAL);
			} else if (listStyleAttr
					.equals(StyleTags.LIST_STYLE_TYPE_LOWER_ALPHA)) {
				list = new List(List.ORDERED, List.ALPHABETICAL);
				list.setLowercase(true);
			} else if (listStyleAttr
					.equals(StyleTags.LIST_STYLE_TYPE_UPPER_ALPHA)) {
				list = new List(List.ORDERED, List.ALPHABETICAL);
				list.setLowercase(false);
			} else if (listStyleAttr.equals(StyleTags.LIST_STYLE_TYPE_DISC)) {
				list = new ZapfDingbatsList(ZapfDingbats.BLACK_CIRCLE);
			} else if (listStyleAttr.equals(StyleTags.LIST_STYLE_TYPE_CIRCLE)) {
				list = new ZapfDingbatsList(ZapfDingbats.WHITE_CIRCLE);
			} else if (listStyleAttr.equals(StyleTags.LIST_STYLE_TYPE_SQUARE)) {
				list = new ZapfDingbatsList(ZapfDingbats.BLACK_SQUARE);
			} else {
				list = new List(List.UNORDERED);
			}

			if (node.getChildren() != null) {
				for (Element child : node.getChildren())
					list.add(child);
			}
			element = list;
			break;

		case Element.LISTITEM:
			ListItem item;
			if (node.getChildren() == null) {
				item = new ListItem(content, font);
			} else {
				item = new ListItem();
				item.setFont(font);
				item.addAll(node.getChildren());

				if (!content.trim().isEmpty())
					item.add(content);
			}
			element = item;
			break;

		case Element.ANCHOR:
			Anchor anchor;
			if (node.getChildren() == null) {
				anchor = new Anchor(content, font);
			} else {
				anchor = new Anchor();
				anchor.setFont(font);
				anchor.addAll(node.getChildren());

				if (!content.trim().isEmpty())
					anchor.add(content);
			}

			String anchorRef = attributes.getValue(HtmlTags.REFERENCE);
			if (anchorRef != null)
				anchor.setReference(anchorRef);

			String anchorName = attributes.getValue(HtmlTags.NAME);
			if (anchorName != null)
				anchor.setName(anchorName);

			element = anchor;
			break;

		case Element.CHUNK:
			Chunk chunk;
			if (qName.equals(HtmlTags.NEWLINE)) {
				chunk = Chunk.NEWLINE;
			} else {
				chunk = new Chunk(content, font);
			}
			element = chunk;
			break;

		case Element.IMGTEMPLATE:
			try {
				String imageSrc = attributes.getValue(HtmlTags.URL);

				Image image;
				if (baseUrl != null)
					image = Image.getInstance(new URL(baseUrl, imageSrc));
				else
					image = Image.getInstance(new URL(imageSrc));

				String imageWidth = style.get(StyleTags.WIDTH);
				String imageHeight = style.get(StyleTags.HEIGHT);
				if (imageWidth != null) {
					float width = Markup.parseLength(imageWidth, image
							.getWidth());
					image.scaleAbsoluteWidth(width);
				}
				if (imageHeight != null) {
					float height = Markup.parseLength(imageHeight, image
							.getHeight());
					image.scaleAbsoluteHeight(height);
				}

				String imageAlt = attributes.getValue(HtmlTags.ALT);
				if (imageAlt != null)
					image.setAlt(imageAlt);

				element = image;
			} catch (BadElementException e1) {
				e1.printStackTrace();
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			break;

		case -1:
			if (qName.equals(HtmlTags.STYLE)) {
				HashMap<String, Style> cssStyles = parseCssStyles(content);
				for (Entry<String, Style> entry : cssStyles.entrySet()) {
					String cssTag = entry.getKey();
					Style newStyle = entry.getValue();
					
					if (cssTag.startsWith(".")) {
						String classTag = cssTag.substring(1);
						Style oldStyle = classMap.get(classTag);
						if (oldStyle == null) {
							classMap.put(classTag, newStyle);
						} else {
							oldStyle.putAll(newStyle);
						}
					} else {
						Style oldStyle = stylesMap.get(cssTag);
						if (oldStyle == null) {
							stylesMap.put(cssTag, newStyle);
						} else {
							oldStyle.putAll(newStyle);
						}
					}
				}
			}
		}

		if (element != null) {
			if (nodes.empty()) {
				try {
					document.add(element);
				} catch (DocumentException e) {
					throw new SAXException(e);
				}
			} else {
				nodes.peek().addChild(element);
			}
		}
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
		super.setDocumentLocator(locator);
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		throw new SAXException("Error on line: " + locator.getLineNumber(), e);
	}
}
