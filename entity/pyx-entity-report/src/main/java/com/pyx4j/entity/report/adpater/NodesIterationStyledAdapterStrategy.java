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
import java.util.Stack;

import org.apache.commons.lang3.EnumUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;

import com.pyx4j.entity.report.adpater.JasperReportStyledUtils.BreakLiners;

public class NodesIterationStyledAdapterStrategy implements JasperReportStyledAdapterStrategy {

    Stack<Integer> olStackIndex;

    Stack<Integer> ulStackIndex;

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

        if (node instanceof TextNode) {
            converted.append(
                    JasperReportStyledUtils.createStyledElement(node, JasperReportStyledUtils.toStyledMap(JasperReportStyledUtils.toMap(parentAttribs))));

        } else if ((node instanceof Element && node.childNodes().size() == 0)) {
            Tag htmlTag = ((Element) node).tag();

            // Deal with br
            switch (htmlTag.getName()) {
//            case "p":
//            case "div":
            case "br":
                converted.append(JasperReportStyledUtils.createStyledElement(new TextNode("\n", ""), null));
                break;
            }

        } else if (node instanceof Element) {
            Node parent = node.parent();
            Tag htmlTag = ((Element) node).tag();

            // ********************************************************
            // Special treatment for closing <ol> and <li> depth level
            if (node.previousSibling() != null //
                    && (node.previousSibling() instanceof Element) //
                    && ((Element) node.previousSibling()).tagName().equalsIgnoreCase("ol")) {
                olStackIndex.pop();
            }

            if (node.previousSibling() != null //
                    && (node.previousSibling() instanceof Element) //
                    && ((Element) node.previousSibling()).tagName().equalsIgnoreCase("ul")) {
                // TODO pop all ul childs recursively
//                removeFromStackRecursively(node.previousSibling());
                ulStackIndex.pop();
            }
            // Special treatment for closing <ol> and <li> depth level
            // *********************************************************

            List<Node> childNodes = new ArrayList<Node>();

            // Deal with spetial tags
            switch (htmlTag.getName()) {
            case "p":
            case "div":
                if (!hasPreviousBreakLiner(node)) {
                    childNodes.add(new TextNode("\n", ""));
                }
                childNodes.addAll(node.childNodes());
                if (!hasPosteriorBreakLiner(node)) {
                    childNodes.add(new TextNode("\n", ""));
                }
                break;
            case "ol":
                if (olStackIndex == null) {
                    olStackIndex = new Stack<Integer>();
                }

                if (parent != null //
                        && (((Element) parent).tagName().equalsIgnoreCase("body") && node.siblingIndex() > 0)) {
                    olStackIndex.removeAllElements();
                }

                olStackIndex.push(new Integer(1));

                if (parent != null //
                        && (!((Element) parent).tagName().equalsIgnoreCase("body") || node.siblingIndex() > 0) //
                        && !((Element) parent).tagName().equalsIgnoreCase("li")) {
                    childNodes.add(new TextNode("\n", ""));
                }
                childNodes.addAll(node.childNodes());
                break;
            case "ul":
                if (ulStackIndex == null) {
                    ulStackIndex = new Stack<Integer>();
                }

                if (parent != null //
                        && (((Element) parent).tagName().equalsIgnoreCase("body") && node.siblingIndex() > 0)) {
                    ulStackIndex.removeAllElements();
                }

                ulStackIndex.push(new Integer(1));

                if (parent != null //
                        && (!((Element) parent).tagName().equalsIgnoreCase("body") || node.siblingIndex() > 0) //
                        && !((Element) parent).tagName().equalsIgnoreCase("li")) {
                    childNodes.add(new TextNode("\n", ""));
                }
                childNodes.addAll(node.childNodes());
                break;
            case "li":
                Node firstChild = node.childNodes().get(0);
                if (firstChild != null //
                        && (firstChild instanceof Element) //
                        && (((Element) firstChild).tagName().equalsIgnoreCase("ol") || ((Element) firstChild).tagName().equalsIgnoreCase("li"))) { //
                    // Do nothing
                } else if (parent != null && ((Element) parent).tagName().equalsIgnoreCase("ol")) {
                    int liIndex = olStackIndex.peek();
                    childNodes.add(new TextNode("\n" + getTabSpaces() + liIndex + ".  ", ""));
                    olStackIndex.pop();
                    olStackIndex.push(++liIndex);
                } else {
                    childNodes.add(new TextNode("\n" + getTabSpaces() + "\u2022  ", ""));
                }
                childNodes.addAll(node.childNodes());
                break;
            case "font":
                nodeAttribs = convertFontAttributeToStyleAttribute(node, nodeAttribs);
            default:
                childNodes.addAll(node.childNodes());
            }

            Attributes inheritedAttributes = JasperReportStyledUtils.inheriteAttributes(parentAttribs, nodeAttribs);
            Attributes tagImplicitAttributes = JasperReportStyledUtils.getTagImplicitAttributes(htmlTag);

            if (tagImplicitAttributes != null && tagImplicitAttributes.size() > 0) {
                inheritedAttributes = JasperReportStyledUtils.inheriteAttributes(tagImplicitAttributes, inheritedAttributes);
            }

            convertToStyled(childNodes, inheritedAttributes);
        }

        return converted.toString();

    }

    private Attributes convertFontAttributeToStyleAttribute(Node node, Attributes nodeAttribs) {
        Attributes styleAttribute = new Attributes();
        StringBuffer newAttributes = new StringBuffer();
        for (Attribute attribute : nodeAttribs) {
            if (attribute.getKey().equalsIgnoreCase("size")) {
                newAttributes.append(JasperReportStyledUtils.FONT_SIZE);
                newAttributes.append(":");
                newAttributes.append(JasperReportStyledUtils.getFontSize(attribute.getValue()));
                newAttributes.append(";");
            } else if (attribute.getKey().equalsIgnoreCase("color")) {
                newAttributes.append(JasperReportStyledUtils.COLOR);
                newAttributes.append(":");
                newAttributes.append(attribute.getValue());
                newAttributes.append(";");
            }
        }

        styleAttribute.put("style", newAttributes.toString());

        return styleAttribute;
    }

//    private void removeFromStackRecursively(Node previousSibling) {
//        List<Node> children = previousSibling.childNodes();
//        for (Node child : children) {
//            if (child instanceof Element && ((Element) child).tagName().equalsIgnoreCase("ul")) {
//                ulStackIndex.pop();
//            }
//            if (child.childNodes().size() > 0) {
//                removeFromStackRecursively(child);
//            }
//        }
//    }

    private String getTabSpaces() {
        String spacesStr = "";
        int nSpaces = (ulStackIndex != null ? ulStackIndex.size() : 0) + (olStackIndex != null ? olStackIndex.size() : 0);
        for (int i = 1; i <= nSpaces; i++) {
            spacesStr += "    "; // Default tab space 4 chars
        }
        return spacesStr;
    }

    private boolean hasPreviousBreakLiner(Node node) {
        Node previous = node.previousSibling();

        // Previous node is text
        if (previous != null && (previous instanceof TextNode)) {
            return false;
        }

        // Previous node is tag no break-liner
        if (previous != null && ((previous instanceof Element) && !EnumUtils.isValidEnum(BreakLiners.class, ((Element) previous).tagName()))) {
            return false;
        }

        // No siblings, and parent is no break-liner
        Node parent = node.parent();
        if (parent != null && !((Element) parent).tagName().equalsIgnoreCase("body")
                && !EnumUtils.isValidEnum(BreakLiners.class, ((Element) parent).tagName())) {
            return false;
        }

        return true;

    }

    private boolean hasPosteriorBreakLiner(Node node) {
        Node after = node.nextSibling();

        // Node after is text
        if (after != null && (after instanceof TextNode)) {
            return false;
        }

        // Node after is tag no break-liner
        if (after != null && ((after instanceof Element) && !EnumUtils.isValidEnum(BreakLiners.class, ((Element) after).tagName()))) {
            return false;
        }

        // No siblings, check first child
        List<Node> children = node.childNodes();
        if (!children.isEmpty()) {
            Node firstChild = children.get(0);

            // First child node is text
            if (firstChild != null && (firstChild instanceof TextNode)) {
                return false;
            }

            // First child node is tag no break-liner
            if (firstChild != null && ((firstChild instanceof Element) && !EnumUtils.isValidEnum(BreakLiners.class, ((Element) firstChild).tagName()))) {
                return false;
            }

        }

        return true;
    }

}
