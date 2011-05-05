package com.pyx4j.svg.j2se;

import org.w3c.dom.Document;

import com.pyx4j.svg.basic.Text;

public class TextImpl extends ShapeImpl implements Text {

    public TextImpl(Document document, String text, int x, int y) {
        super(document.createElementNS(SvgRootImpl.SVG_NAMESPACE, "text"));
        getElement().setTextContent(text);
        getElement().setAttribute("x", String.valueOf(x));
        getElement().setAttribute("y", String.valueOf(y));
    }

    public void setFont(String font) {
        getElement().setAttribute("font", font);
    }

    //possible values are :     start | middle | end |  inherit (think enumeration)
    public void setTextAnchor(String anchor) {
        getElement().setAttribute("text-anchor", anchor);
    }

}
