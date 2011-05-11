package com.pyx4j.svg.j2se;

import org.w3c.dom.Document;

import com.pyx4j.svg.basic.Text;

public class TextImpl extends ShapeImpl implements Text {

    private final String text;

    public TextImpl(Document document, String text, int x, int y) {
        super(document.createElementNS(SvgRootImpl.SVG_NAMESPACE, "text"));
        this.text = text;
        getElement().setTextContent(text);
        getElement().setAttribute("x", String.valueOf(x));
        getElement().setAttribute("y", String.valueOf(y));
        getElement().setAttribute("stroke-width", "0");
        getElement().setAttribute("font-size", String.valueOf(DEFAULT_FONT_SIZE));
        getElement().setAttribute("fill", "black");
        getElement().setAttribute("font-family", "Arial");
        getElement().setAttribute("dominant-baseline", "mathematical");
    }

    public void setFont(String font) {
        getElement().setAttribute("font", font);
    }

    //possible values are :     start | middle | end |  inherit (think enumeration)
    public void setTextAnchor(String anchor) {
        getElement().setAttribute("text-anchor", anchor);
    }

    @Override
    public String getTextValue() {
        return text;
    }

}
