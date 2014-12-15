/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Nov 6, 2014
 * @author arminea
 * @version $Id$
 */
package com.pyx4j.widgets.client.selector;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;

public class OptionQueryHighlighter {

    public static SafeHtml highlight(SafeHtml html, String query) {

        Document parser;
        try {
            parser = XMLParser.parse(html.asString());
            Element root = parser.getDocumentElement();
            NodeList nodes = root.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                recursiveHighlightQuery(nodes.item(i), query);
            }
            return new SafeHtmlBuilder().appendHtmlConstant((root.getOwnerDocument().toString())).toSafeHtml();
        } catch (DOMParseException ex) {
            return new SafeHtmlBuilder().appendHtmlConstant(highlightQueryInString(html.asString(), query)).toSafeHtml();
        }

    }

    private static void recursiveHighlightQuery(Node node, String query) {
        if (node.hasChildNodes()) {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node currentChild = children.item(i);
                i = i + 2; // highlighting increments count of children
                recursiveHighlightQuery(currentChild, query);
            }
        } else if (node.getNodeValue() != null) {
            highlightQuery(node, query);
        }
    }

    private static void highlightQuery(Node node, String query) {
        String text = node.getNodeValue().toLowerCase();
        Node parent = node.getParentNode();

        if (text.contains(query.toLowerCase())) {
            Document document = node.getOwnerDocument();
            String value = node.getNodeValue();
            parent.removeChild(node);
            int start = text.indexOf(query.toLowerCase());
            int end = start + query.length();
            if (text.indexOf(query) != 0) {
                parent.appendChild(document.createTextNode(value.substring(0, start)));
            }
            Node highlighted = document.createElement("b");
            highlighted.appendChild(document.createTextNode(value.substring(start, end)));
            parent.appendChild(highlighted);
            parent.appendChild(document.createTextNode(value.substring(end, value.length())));

        }
    }

    private static String highlightQueryInString(String inputText, String query) {
        StringBuilder ret = new StringBuilder();
        String text = inputText.toLowerCase();
        if (text.contains(query.toLowerCase())) {
            int start = text.indexOf(query.toLowerCase());
            int end = start + query.length();
            ret.append(inputText.substring(0, start)).append("<b>").append(inputText.substring(start, end)).append("</b>")
                    .append(inputText.subSequence(end, inputText.length()));
        }
        return ret.toString();

    }
}
