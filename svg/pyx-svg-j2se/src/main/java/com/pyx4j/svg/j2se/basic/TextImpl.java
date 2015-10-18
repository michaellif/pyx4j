package com.pyx4j.svg.j2se.basic;

import org.w3c.dom.Document;
import org.w3c.dom.svg.GetSVGDocument;
import org.w3c.dom.svg.SVGElement;

import com.pyx4j.svg.basic.Text;

public class TextImpl extends ShapeImpl implements Text {

    private String text;

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
    }

    public void setFont(String font) {
        getElement().setAttribute("font", font);
    }

    public void setFontSize(String fontSize) {
        getElement().setAttribute("font-size", fontSize);
    }

    //possible values are :     start | middle | end |  inherit (think enumeration)
    public void setTextAnchor(String anchor) {
        getElement().setAttribute("text-anchor", anchor);
    }

    @Override
    public String getTextValue() {
        return text;
    }

    public void setTextValue(String text) {
        SVGElement txt = (SVGElement) getElement().getOwnerDocument().getElementById(getId());
        txt.setTextContent(text);
        this.text = text;
    }
}
