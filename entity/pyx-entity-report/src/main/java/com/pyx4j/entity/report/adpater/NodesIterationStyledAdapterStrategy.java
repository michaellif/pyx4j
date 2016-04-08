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
 * Created on Apr 2, 2016
 * @author ernestog
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.pyx4j.entity.report.adpater;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;

public class NodesIterationStyledAdapterStrategy implements JasperReportStyledAdapterStrategy {

    private Stack<Integer> olStackIndex; // Store for <ol> indexes and counter for indents at same time

    private int ulCounter = 0; // Counter for <ul> elements (for indents)

    // Markers for spectial tags
    private boolean isSup = false;

    private boolean isSub = false;

    private StringBuffer styledResult = new StringBuffer();

    @Override
    public String makeJasperCompatibleStyled(String cleanedHtmlPart) {

        cleanedHtmlPart = JasperReportStyledUtils.ensureNoBreakLinesNorTabs(cleanedHtmlPart);

        Document htmlDocument = Jsoup.parse(cleanedHtmlPart);

        List<Node> childNodes = htmlDocument.select("body").get(0).childNodes();

        String converted = convertToStyled(childNodes, null);
        styledResult.setLength(0); // TODO Enhance this

        System.out.println("\n\n************** STYLED CONVERSION ***************");
        System.out.println("Received -> " + cleanedHtmlPart);
        System.out.println("\nConverted-> " + converted);
        System.out.println("*******************************************\n\n");
        return converted;
    }

    private String convertToStyled(List<Node> childNodes, Attributes inhiretedAttributes) {

        for (Node node : childNodes) {
            styledResult.append(convertToStyled(node, inhiretedAttributes, node.attributes()));
        }

        return styledResult.toString();
    }

    private String convertToStyled(Node node, Attributes parentAttribs, Attributes nodeAttribs) {

        StringBuffer converted = new StringBuffer();

        // After each node, search for closing <ol> and <li> depth level
        recalculateIndentsAndIndexForLists(node.previousSibling());

        // After each node, search for closing <sup> and <sub>
        treatSpecialSupportedTags(node.previousSibling());

        if (node instanceof TextNode) {
            TextNode textNode = (TextNode) node;

            // Enclose sup or sub tag if needed
            encloseTextInSpecialTagIfRequired(textNode);

            Map<String, String> styledMapAttributes = JasperReportStyledUtils.toStyledMap(parentAttribs);

            converted.append(JasperReportStyledUtils.createStyledElement(textNode, styledMapAttributes));

        } else if ((node instanceof Element && node.childNodes().size() == 0)) {
            Tag htmlTag = ((Element) node).tag();

            // Deal with br
            switch (htmlTag.getName()) {
            case "br":
                converted.append(JasperReportStyledUtils.createStyledElement(new TextNode("\n", ""), null));
                break;
            }

        } else if (node instanceof Element) {
            Node parent = node.parent();
            String elementTagName = ((Element) node).tag().getName();

            List<Node> childNodes = new ArrayList<Node>();

            switch (elementTagName) {
            case "p":
            case "div":
                if (((Element) node).text().length() == 0) {
                    childNodes.add(new TextNode("\n", ""));
                } else {
                    if (!hasPreviousBreakLiner(node)) {
                        childNodes.add(new TextNode("\n", ""));
                    }
                    childNodes.addAll(node.childNodes());
                    if (!hasPosteriorBreakLiner(node)) {
                        childNodes.add(new TextNode("\n", ""));
                    }
                }
                break;
            case "ol":
                if (olStackIndex == null) {
                    olStackIndex = new Stack<Integer>();
                }

                if (parent != null && (isElementBody(parent) && node.siblingIndex() > 0)) {
                    olStackIndex.removeAllElements();
                }

                olStackIndex.push(new Integer(1));

                if (parent != null && (!isElementBody(parent) || node.siblingIndex() > 0) && !isElementTag(parent, "li")) {
                    childNodes.add(new TextNode("\n", ""));
                }
                childNodes.addAll(node.childNodes());
                break;
            case "ul":
                if (parent != null //
                        && (((Element) parent).tagName().equalsIgnoreCase("body") && node.siblingIndex() > 0)) {
                    ulCounter = 0;
                }

                ulCounter++;

                if (parent != null && (!isElementBody(parent) || node.siblingIndex() > 0) && !isElementTag(parent, "li")) {
                    childNodes.add(new TextNode("\n", ""));
                }
                childNodes.addAll(node.childNodes());
                break;
            case "li":
                Node firstChild = node.childNodes().get(0);
                if (firstChild != null && (isElementTag(firstChild, "ol") || isElementTag(firstChild, "li"))) { //
                    // Do nothing
                } else if (parent != null && isElementTag(parent, "ol")) {
                    int liIndex = olStackIndex.peek();
                    childNodes.add(new TextNode("\n" + getTabSpaces() + liIndex + ".  ", ""));
                    olStackIndex.pop();
                    olStackIndex.push(++liIndex);
                } else {
                    childNodes.add(new TextNode("\n" + getTabSpaces() + JasperReportStyledUtils.UL_LI_STARTER + "  ", ""));
                }
                childNodes.addAll(node.childNodes());
                break;
            case "font":
                nodeAttribs = JasperReportStyledUtils.convertFontAttributeToStyleAttribute(node, nodeAttribs);
                childNodes.addAll(node.childNodes());
                break;
            case "sup":
                isSup = true;
                childNodes.addAll(node.childNodes());
                break;
            case "sub":
                isSub = true;
                childNodes.addAll(node.childNodes());
                break;
            default:
                childNodes.addAll(node.childNodes());
            }

            Attributes inheritedAttributes = JasperReportStyledUtils.inheriteAttributes(parentAttribs, nodeAttribs);
            Attributes tagImplicitAttributes = JasperReportStyledUtils.getTagImplicitAttributes(elementTagName);

            if (tagImplicitAttributes.size() > 0) {
                inheritedAttributes = JasperReportStyledUtils.inheriteAttributes(inheritedAttributes, tagImplicitAttributes);
            }

            convertToStyled(childNodes, inheritedAttributes);
        }

        return converted.toString();

    }

    private void encloseTextInSpecialTagIfRequired(TextNode textNode) {
        if (isSup) {
            Element el = new Element(Tag.valueOf("sup"), "");
            el.text(textNode.text());
            textNode.text(el.outerHtml());
        } else if (isSub) {
            Element el = new Element(Tag.valueOf("sub"), "");
            el.text(textNode.text());
            textNode.text(el.outerHtml());
        }
    }

    /**
     * Treat special supported tags that has no styled attribute conventions and
     * has to be used "as is". Each chunk of text with these properties has to be enclosed
     * with style properties and sup at the lowest level.
     *
     * Sample:
     * html source : regular text <sub><b>sup text bold </b><i>sup text italic</i></sub>
     * styled result : <style >regular text </style><style isBold="true" ><sub>sup text bold </sub></style>
     * <style isItalic="true" ><sub>sup text italic</sub></style>
     *
     * @param previousSibling
     */
    private void treatSpecialSupportedTags(Node previousSibling) {
        if (previousSibling != null && (previousSibling instanceof Element)) {
            if (((Element) previousSibling).tagName().equalsIgnoreCase("sup")) {
                isSup = false;
            } else if (((Element) previousSibling).tagName().equalsIgnoreCase("sub")) {
                isSub = false;
            }

            if (previousSibling.childNodes().size() > 0) {
                for (Node node : previousSibling.childNodes()) {
                    treatSpecialSupportedTags(node);
                }
            }
        }
    }

    private void recalculateIndentsAndIndexForLists(Node node) {
        if (!(node instanceof Element)) {
            return;
        }

        if (((Element) node).tagName().equalsIgnoreCase("ul")) {
            if (ulCounter > 0) {
                ulCounter--;
            }
        }

        if (((Element) node).tagName().equalsIgnoreCase("ol")) {
            if (!olStackIndex.isEmpty()) {
                olStackIndex.pop();
            }
        }

        if (node.childNodes().size() > 0) {
            for (Node child : node.childNodes()) {
                recalculateIndentsAndIndexForLists(child);
            }
        }

    }

    private String getTabSpaces() {
        String spacesStr = "";
        int nSpaces = ulCounter + (olStackIndex != null ? olStackIndex.size() : 0);
        for (int i = 1; i <= nSpaces; i++) {
            spacesStr += JasperReportStyledUtils.DEFAULT_TAB_SIZE;
        }
        return spacesStr;
    }

    private boolean hasPreviousBreakLiner(Node node) {
        Node previous = node.previousSibling();

        // Previous node is text
        if (previous != null && isTextNode(previous)) {
            return false;
        }

        // Previous node is tag no break-liner
        if (previous != null && (isElement(previous) && !JasperReportStyledUtils.isBreakLiner(previous))) {
            return false;
        }

        // No siblings, and parent is no break-liner
        Node parent = node.parent();
        if (parent != null && !isElementBody(parent) && !JasperReportStyledUtils.isBreakLiner(parent)) {
            return false;
        }

        return true;

    }

    private boolean hasPosteriorBreakLiner(Node node) {
        Node after = node.nextSibling();

        // Node after is text
        if (after != null && isTextNode(after)) {
            return false;
        }

        // Node after is tag no break-liner
        if (after != null && (isElement(after) && !JasperReportStyledUtils.isBreakLiner(after))) {
            return false;
        }

        // No siblings, check first child
        List<Node> children = node.childNodes();
        if (!children.isEmpty()) {
            Node firstChild = children.get(0);

            // First child node is text
            if (firstChild != null && (isTextNode(firstChild))) {
                return false;
            }

            // First child node is tag no break-liner
            if (firstChild != null && (isElement(firstChild) && !JasperReportStyledUtils.isBreakLiner(firstChild))) {
                return false;
            }

        }

        return true;

    }

    private static boolean isTextNode(Node node) {
        return node instanceof TextNode;
    }

    private static boolean isElement(Node node) {
        return node instanceof Element;
    }

    private static Element toElement(Node node) {
        return (Element) node;
    }

    private static boolean isElementBody(Node node) {
        return isElementTag(node, "body");
    }

    private static boolean isElementTag(Node node, String tagName) {
        if (node != null && (node instanceof Element)) {
            Element element = (Element) node;
            return element.tagName().equalsIgnoreCase(tagName);
        }

        return false;
    }

}
