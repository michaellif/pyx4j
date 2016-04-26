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

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import com.pyx4j.commons.CommonsStringUtils;

import net.sf.jasperreports.engine.util.JRColorUtil;
import net.sf.jasperreports.engine.util.JRStringUtil;

public class JasperReportStyledUtils {

    public static final String DEFAULT_TAB_SIZE = "    "; // Default tab space 4 chars

    public static final String UL_LI_STARTER = "\u2022";

    public static final String FONT_WEIGHT = "font-weight";

    public static final String FONT_STYLE = "font-style";

    public static final String TEXT_DECORATION = "text-decoration";

    public static final String COLOR = "color";

    public static final String BACKGROUND_COLOR = "background-color";

    public static final String BACKGROUND = "background";

    public static final String FONT_SIZE = "font-size";

    public enum ReservedStyledWords {
        isitalic, isbold, isunderline, isstrikethrough
    }

    public enum BreakLiners {
        p, div
    }

    static Map<String, Set<String>> cssAcceptedAttributes = new HashMap<String, Set<String>>();

    static {
        cssAcceptedAttributes.put(FONT_WEIGHT, new HashSet<String>(Arrays.asList("normal", "bold")));
        cssAcceptedAttributes.put(FONT_STYLE, new HashSet<String>(Arrays.asList("normal", "italic")));
        cssAcceptedAttributes.put(TEXT_DECORATION, new HashSet<String>(Arrays.asList("line-through", "underline")));
    }

    public static String createStyledElement(Node node, Map<String, String> currentAttribs, SpecialAttributes specialAttributes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(createOpenTag("style", currentAttribs));
        buffer.append(createFinalText(node, specialAttributes)); // TODO Test with only getting text to not get breaklines
        buffer.append(createCloseTag("style"));
        return buffer.toString();
    }

    private static String createFinalText(Node node, SpecialAttributes specialAttributes) {
        String nodeText = ((TextNode) node).getWholeText();
        String encodedText = null;

        if (specialAttributes.isSup) {
            encodedText = encloseSpecialAttribute(SpecialAttributes.SUP, nodeText);
        } else if (specialAttributes.isSub) {
            encodedText = encloseSpecialAttribute(SpecialAttributes.SUB, nodeText);
        } else {
            encodedText = JRStringUtil.xmlEncode(nodeText);
        }

        return encodedText;
    }

    private static String encloseSpecialAttribute(String attribute, String text) {
        StringBuilder encodedBuilder = new StringBuilder();
        encodedBuilder.append(createOpenTag(attribute, null));
        encodedBuilder.append(JRStringUtil.xmlEncode(text));
        encodedBuilder.append(createCloseTag(attribute));
        return encodedBuilder.toString();
    }

    public static String createOpenTag(String tagName, Map<String, String> currenAttr) {

        StringBuffer buffer = new StringBuffer();
        buffer.append("<");
        buffer.append(tagName);
        if (currenAttr != null) {
            for (Map.Entry<String, String> entry : currenAttr.entrySet()) {
                buffer.append(" ");
                buffer.append(entry.getKey());
                buffer.append("=");
                buffer.append("\"");
                buffer.append(entry.getValue());
                buffer.append("\"");
            }
        }
        buffer.append(">");

        return buffer.toString();
    }

    public static String createCloseTag(String tagName) {
        StringBuilder builder = new StringBuilder();
        builder.append("</");
        builder.append(tagName);
        builder.append(">");
        return builder.toString();
    }

    public static Map<String, String> toStyledMap(Attributes attributes) {
        System.out.println("");
        Map<String, String> parentAttributes = JasperReportStyledUtils.toMap(attributes);
        Map<String, String> styledAttributes = new HashMap<String, String>();

        if (parentAttributes == null || parentAttributes.isEmpty()) {
            return styledAttributes;
        }

        for (Map.Entry<String, String> entry : parentAttributes.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("style")) {
                parseStyleProperty(styledAttributes, entry.getValue());
            }
        }

        return styledAttributes;
    }

    static <E extends Enum<E>> boolean isValidEnum(final Class<E> enumClass, final String enumName) {
        if (enumName == null) {
            return false;
        }
        return EnumUtils.isValidEnum(enumClass, enumName.toLowerCase());
    }

    public static void parseStyleProperty(Map<String, String> resultMap, String value) {
        if (value != null && !value.isEmpty()) {
            String[] properties = value.split(";");
            for (String property : properties) {
                String[] values = property.split(":");

                String keyAttribute = values[0].trim();
                String valueAttribute = values[1].trim();

                // ******************  Bold ***********************
                if (keyAttribute.equalsIgnoreCase(JasperReportStyledUtils.FONT_WEIGHT)) {
                    resultMap.put("isBold", String.valueOf(valueAttribute.equalsIgnoreCase("bold")));
                }

                // ******************  Strikethrough ***********************
                if (keyAttribute.equalsIgnoreCase(JasperReportStyledUtils.TEXT_DECORATION)) {
                    resultMap.put("isStrikeThrough", String.valueOf(valueAttribute.equalsIgnoreCase("line-through")));
                }

                // ******************  Italic ***********************
                if (keyAttribute.equalsIgnoreCase(JasperReportStyledUtils.FONT_STYLE)) {
                    resultMap.put("isItalic", String.valueOf(valueAttribute.equalsIgnoreCase("italic")));
                }

                // ******************  Underline ***********************
                if (keyAttribute.equalsIgnoreCase(JasperReportStyledUtils.TEXT_DECORATION)) {
                    resultMap.put("isUnderline", String.valueOf(valueAttribute.equalsIgnoreCase("underline")));
                }

                // ******************  Font-Size ***********************
                if (keyAttribute.equalsIgnoreCase(JasperReportStyledUtils.FONT_SIZE)) {
                    String fontSizeValue = getCssFontSize(valueAttribute);
                    if (fontSizeValue != null) {
                        resultMap.put("size", fontSizeValue);
                    }
                }

                // ******************  Color ***********************
                if (keyAttribute.equalsIgnoreCase(JasperReportStyledUtils.COLOR)) {
                    String colorValue = getCssColor(valueAttribute);
                    if (colorValue != null) {
                        resultMap.put("forecolor", colorValue);
                    }
                }

                // ******************  Background color ***********************
                if (keyAttribute.equalsIgnoreCase(JasperReportStyledUtils.BACKGROUND_COLOR) //
                        || keyAttribute.equalsIgnoreCase(JasperReportStyledUtils.BACKGROUND)) {
                    String backgroundColorValue = getCssColor(valueAttribute);
                    if (backgroundColorValue != null) {
                        resultMap.put("backcolor", backgroundColorValue);
                    }
                }

            }
        }

    }

    public static String getCssColor(String strColor) {
        Color color;
        try {
            color = JRColorUtil.getColor(strColor.toLowerCase(), null);
        } catch (Exception e) { // This should be JRRuntimeException but this is fine... isn't it?
            color = null;
        }

        if (color != null) {
            return JRColorUtil.getCssColor(color);
        } else {
            return null;
        }
    }

    public static boolean isBreakLiner(Node node) {
        if (node instanceof TextNode) {
            return false;
        }

        Element element = (Element) node;

        if (element == null) {
            return false;
        }

        if (isValidEnum(BreakLiners.class, element.tagName()) && element.text().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static String getTagFontSize(String fontSize) {
        String pt;
        if (NumberUtils.isNumber(fontSize)) {
            Double value = new Double(fontSize);
            int index = (int) Math.round(value);
            pt = FontSizeKeyword.getValueByNumber(index);
        } else {
            pt = FontSizeKeyword.getValueByName(fontSize);
        }

        return pt;
    }

    public static String getCssFontSize(String fontSize) {
        Matcher matcher = Pattern.compile("(\\d+\\.?\\d*)(.*)").matcher(fontSize);
        if (matcher.find()) {
            return String.valueOf(matcher.group(1));
        } else {
            return getTagFontSize(fontSize);
        }
    }

    public static Attributes getTagImplicitAttributes(String tagName) {
        Attributes attributes = new Attributes();
        switch (tagName) {
        case "b":
            attributes.put("style", FONT_WEIGHT + ":" + "bold;");
            break;
        case "i":
            attributes.put("style", FONT_STYLE + ":" + "italic;");
            break;
        case "u":
            attributes.put("style", TEXT_DECORATION + ":" + "underline;");
            break;
        case "strike":
        case "s":
        case "del":
            attributes.put("style", TEXT_DECORATION + ":" + "line-through;");
            break;
        }

        return attributes;
    }

    public static Attributes inheriteAttributes(Attributes inhiretedAttr, Attributes nodeAttr) {
        if (inhiretedAttr != null) {
            if (nodeAttr != null) {
                for (Attribute inhiretedAttribute : inhiretedAttr.asList()) {

                    Map<String, String> inhiretedValues = toMap(inhiretedAttribute.getValue().trim());
                    Map<String, String> currentValues = toMap(nodeAttr.get(inhiretedAttribute.getKey()).trim());

                    if (nodeAttr.hasKey(inhiretedAttribute.getKey())) {
                        // Copy carefully
                        for (Map.Entry<String, String> entry : inhiretedValues.entrySet()) {
                            String key = entry.getKey();
                            if (!currentValues.containsKey(key)) {
                                currentValues.put(key, entry.getValue());
                            }
                        }
                    } else {
                        currentValues.putAll(inhiretedValues);
                    }

                    StringBuffer newAttribs = new StringBuffer();
                    for (Map.Entry<String, String> entry : currentValues.entrySet()) {
                        newAttribs.append(entry.getKey());
                        newAttribs.append(":");
                        newAttribs.append(entry.getValue());
                        newAttribs.append(";");
                    }

                    nodeAttr.put(inhiretedAttribute.getKey(), newAttribs.toString());
                }
            } else {
                nodeAttr = inhiretedAttr;
            }
        }

        return nodeAttr;
    }

    private static boolean isValidCssAttributeValue(String key, String value) {
        if (value == null) {
            return false;
        }

        switch (key) {
        case COLOR:
        case BACKGROUND:
        case BACKGROUND_COLOR:
            return getCssColor(value) != null;
        case FONT_SIZE:
            return getCssFontSize(value) != null;
        default:
            // do nothing; check hash
        }

        if (cssAcceptedAttributes.containsKey(key)) {
            if (cssAcceptedAttributes.get(key).contains(value)) {
                return true;
            }
        }

        return false;
    }

    public static Map<String, String> toMap(String styleProperties) {
        Map<String, String> map = new HashMap<String, String>();
        String[] keyValuePairs = styleProperties.trim().split(";");
        for (String keyValuePair : keyValuePairs) {
            String[] splitedValue = keyValuePair.split(":");

            if (splitedValue.length == 1 && splitedValue[0].trim().length() > 0) {
                map.put(splitedValue[0].trim(), null);
            } else if (splitedValue.length == 2) {
                String key = splitedValue[0].trim();
                String value = splitedValue[1];
                if (isValidCssAttributeValue(key, value)) {
                    map.put(key, value);
                }
            }
        }

        return map;
    }

    public static Map<String, String> toMap(Attributes attributes) {
        Map<String, String> attrMap = new HashMap<String, String>();

        if (attributes != null) {
            for (Attribute attr : attributes) {
                if (attr.getKey() != "text") { // avoid text attribute of TextNodes
                    String key = attr.getKey().trim();
                    String cleanedValue = cleanAttributeValues(attr);
                    if (!CommonsStringUtils.isEmpty(cleanedValue)) {
                        attrMap.put(key, cleanedValue);
                    }
                }
            }
        }

        return attrMap;
    }

    private static String cleanAttributeValues(Attribute attribute) {
        StringBuilder builder = new StringBuilder();
        if (attribute.getKey().equalsIgnoreCase("style")) {
            Map<String, String> mapValues = toMap(attribute.getValue());
            for (Map.Entry<String, String> entry : mapValues.entrySet()) {
                if (entry.getValue() != null && isValidCssAttributeValue(entry.getKey().trim(), entry.getValue().trim())) {
                    builder.append(entry.getKey());
                    builder.append(":");
                    builder.append(entry.getValue());
                    builder.append(";");
                }
            }
        }

        return builder.toString();
    }

    public static String ensureNoBreakLinesNorTabs(String cleanedHtmlPart) {
        return cleanedHtmlPart.replaceAll("\n", "").replaceAll("\t", "");
    }

    public static Attributes getFontAttributesToStyleAttribute(Node node, Attributes nodeAttribs) {
        Attributes styleAttribute = new Attributes();
        StringBuffer newAttributes = new StringBuffer();
        for (Attribute attribute : nodeAttribs) {
            if (attribute.getKey().equalsIgnoreCase("size")) {
                String sizeValue = JasperReportStyledUtils.getTagFontSize(attribute.getValue());
                if (sizeValue != null) {
                    newAttributes.append(JasperReportStyledUtils.FONT_SIZE);
                    newAttributes.append(":");
                    newAttributes.append(sizeValue);
                    newAttributes.append(";");
                }
            } else if (attribute.getKey().equalsIgnoreCase("color")) {
                String color = JasperReportStyledUtils.getCssColor(attribute.getValue());
                if (color != null) {
                    newAttributes.append(JasperReportStyledUtils.COLOR);
                    newAttributes.append(":");
                    newAttributes.append(color);
                    newAttributes.append(";");
                }
            }
        }

        styleAttribute.put("style", newAttributes.toString());

        return styleAttribute;
    }
}
