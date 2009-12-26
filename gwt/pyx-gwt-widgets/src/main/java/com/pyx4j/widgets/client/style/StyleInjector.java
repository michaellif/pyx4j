/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 26, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client.style;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.StyleElement;

public class StyleInjector {

    private static StyleElement style;

    private static class Impl {
        public void injectStyle(String compiledStyle) {
            style = Document.get().createStyleElement();
            style.setPropertyString("language", "text/css");
            style.setInnerText(compiledStyle);
            Document.get().getElementsByTagName("head").getItem(0).appendChild(style);
        }
    }

    @SuppressWarnings("unused")
    private static class ImplIE6 extends Impl {
        @Override
        public void injectStyle(String compiledStyle) {
            style = createElement();
            setContents(style, compiledStyle);
        }

        protected native com.google.gwt.dom.client.StyleElement createElement() /*-{
            return $doc.createStyleSheet();
        }-*/;

        protected native void setContents(com.google.gwt.dom.client.StyleElement style, String contents)/*-{
            style.cssText=contents;
        }-*/;
    }

    private final Impl impl = GWT.create(Impl.class);

    public void injectStyle(String stylesString) {
        if (style != null) {
            NodeList<Element> list = Document.get().getElementsByTagName("style");
            for (int i = 0; i < list.getLength(); i++) {
                list.getItem(i).getParentNode().removeChild(list.getItem(i));
            }
        }
        //System.err.println(stylesString);
        impl.injectStyle(stylesString);
    }
}
