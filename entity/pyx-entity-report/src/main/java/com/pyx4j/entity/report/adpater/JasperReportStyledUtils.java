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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;

import net.sf.jasperreports.engine.util.JRColorUtil;

public class JasperReportStyledUtils {

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

    public static String createStyledElement(Node node, Map<String, String> currentAttribs) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(createOpenStyleTag(node, currentAttribs));
        buffer.append(((TextNode) node).getWholeText()); // TODO Test with only getting text to not get breaklines
        buffer.append(createCloseStyleTag());
        return buffer.toString();
    }

    public static String createOpenStyleTag(Node node, Map<String, String> currenAttr) {

        StringBuffer buffer = new StringBuffer();
        buffer.append("<");
        buffer.append("style");
        buffer.append(" ");
        if (currenAttr != null) {
            for (Map.Entry<String, String> entry : currenAttr.entrySet()) {
                buffer.append(entry.getKey());
                buffer.append("=");
                buffer.append("\"");
                buffer.append(entry.getValue());
                buffer.append("\"");
                buffer.append(" ");
            }
        }
        buffer.append(">");

        return buffer.toString();
    }

    public static String createCloseStyleTag() {
        return "</style>";
    }

    public static Map<String, String> toStyledMap(Map<String, String> parentAttributes) {
        Map<String, String> styledAttributes = new HashMap<String, String>();

        if (parentAttributes == null || parentAttributes.isEmpty()) {
            return styledAttributes;
        }

        for (Map.Entry<String, String> entry : parentAttributes.entrySet()) {

            if (entry.getKey().equalsIgnoreCase("style")) {
                parseStyleProperty(styledAttributes, entry.getValue());
            } else if (EnumUtils.isValidEnum(ReservedStyledWords.class, entry.getKey())) {
                parseStyleProperty(styledAttributes, entry.getKey() + ":true;");
            }

        }

        return styledAttributes;

    }

    public static void parseStyleProperty(Map<String, String> resultMap, String value) {
        if (value != null && !value.isEmpty()) {
            String[] properties = value.split(";");
            for (String property : properties) {
                String[] values = property.split(":");
//              System.out.println("Item : " + entry.getKey() + " Count : " + entry.getValue());

//              Object value = attrs.get(TextAttribute.FAMILY);
//              Object oldValue = parentAttrs.get(TextAttribute.FAMILY);
                //
//              if (value != null && !value.equals(oldValue)) {
//                  sbuffer.append(SPACE);
//                  sbuffer.append(ATTRIBUTE_fontName);
//                  sbuffer.append(EQUAL_QUOTE);
//                  sbuffer.append(value);
//                  sbuffer.append(QUOTE);
//              }

//              Object value = attr.getKey().equalsIgnoreCase(FONT_WEIGHT);
//              Object oldValue = parentAttrs.get(TextAttribute.WEIGHT);

//              if (value != null && !value.equals(oldValue)) {
//                  sbuffer.append(SPACE);
//                  sbuffer.append(ATTRIBUTE_isBold);
//                  sbuffer.append(EQUAL_QUOTE);
//                  sbuffer.append(value.equals(TextAttribute.WEIGHT_BOLD));
//                  sbuffer.append(QUOTE);
//              }
                //
//              value = attrs.get(TextAttribute.POSTURE);
//              oldValue = parentAttrs.get(TextAttribute.POSTURE);
                //
//              if (value != null && !value.equals(oldValue)) {
//                  sbuffer.append(SPACE);
//                  sbuffer.append(ATTRIBUTE_isItalic);
//                  sbuffer.append(EQUAL_QUOTE);
//                  sbuffer.append(value.equals(TextAttribute.POSTURE_OBLIQUE));
//                  sbuffer.append(QUOTE);
//              }

                String keyAttribute = values[0].trim();
                String valueAttribute = values[1].trim();

                // ******************  Bold ***********************
                if (keyAttribute.equalsIgnoreCase(JasperReportStyledUtils.FONT_WEIGHT)) {
                    resultMap.put("isBold", String.valueOf(valueAttribute.equalsIgnoreCase("bold")));
                }

                if (keyAttribute.equalsIgnoreCase("isbold")) {
                    resultMap.put("isBold", String.valueOf(Boolean.TRUE));
                }

                // ******************  Strikethrough ***********************
                if (keyAttribute.equalsIgnoreCase(JasperReportStyledUtils.TEXT_DECORATION)) {
                    resultMap.put("isStrikeThrough", String.valueOf(valueAttribute.equalsIgnoreCase("line-through")));
                }

                // ******************  Italic ***********************
                if (keyAttribute.equalsIgnoreCase("isitalic")) {
                    resultMap.put("isItalic", String.valueOf(Boolean.TRUE));
                }

                // ******************  Underline ***********************
                if (keyAttribute.equalsIgnoreCase("isunderline")) {
                    resultMap.put("isUnderline", String.valueOf(Boolean.TRUE));
                }

                if (keyAttribute.equalsIgnoreCase(JasperReportStyledUtils.FONT_SIZE)) {
                    Matcher matcher = Pattern.compile("(\\d+\\.?\\d*)(.*)").matcher(valueAttribute);
                    if (matcher.find()) {
                        resultMap.put("size", String.valueOf(matcher.group(1)));
                    } else {
                        resultMap.put("size", getFontSize(valueAttribute));
                    }

                }

//              value = attrs.get(TextAttribute.STRIKETHROUGH);
//              oldValue = parentAttrs.get(TextAttribute.STRIKETHROUGH);
                //
//              if ((value == null && oldValue != null) || (value != null && !value.equals(oldValue))) {
//                  sbuffer.append(SPACE);
//                  sbuffer.append(ATTRIBUTE_isStrikeThrough);
//                  sbuffer.append(EQUAL_QUOTE);
//                  sbuffer.append(value != null);
//                  sbuffer.append(QUOTE);
//              }

                if (keyAttribute.equalsIgnoreCase(JasperReportStyledUtils.COLOR)) {
                    Color color = JRColorUtil.getColor(valueAttribute, Color.black);
                    resultMap.put("forecolor", JRColorUtil.getCssColor(color));
                }

                if (keyAttribute.equalsIgnoreCase(JasperReportStyledUtils.BACKGROUND_COLOR) //
                        || keyAttribute.equalsIgnoreCase(JasperReportStyledUtils.BACKGROUND)) {
                    Color color = JRColorUtil.getColor(valueAttribute, Color.black);
                    resultMap.put("backcolor", JRColorUtil.getCssColor(color));
                }

            }
        }

    }

    public static String getFontSize(String fontSize) {
        double pt;
        if (NumberUtils.isNumber(fontSize)) {
            Double value = new Double(fontSize);
            int index = (int) Math.round(value);
            pt = FontSizeKeyword.getValueByNumber(index);
        } else {
            pt = FontSizeKeyword.getValueByName(fontSize);
        }

        return String.valueOf(pt);
    }

    public static Attributes getTagImplicitAttributes(Tag tag) {
        Attributes attributes = new Attributes();
        switch (tag.getName()) {
        case "b":
            attributes.put("isBold", true);
            break;
        case "i":
            attributes.put("isItalic", true);
            break;
        case "u":
            attributes.put("isUnderline", true);
            break;
        }

        return attributes;

    }

    public static Attributes inheriteAttributes(Attributes inhiretedAttr, Attributes nodeAttr) {
        if (inhiretedAttr != null) {
            if (nodeAttr != null) {
                for (Attribute inhiretedAttribute : inhiretedAttr.asList()) {

                    Map<String, Object> inhiretedValues = toMap(inhiretedAttribute.getValue().trim());
                    Map<String, Object> currentValues = toMap(nodeAttr.get(inhiretedAttribute.getKey()).trim());

                    if (nodeAttr.hasKey(inhiretedAttribute.getKey())) {
                        // Copy carefully
                        for (Map.Entry<String, Object> entry : inhiretedValues.entrySet()) {
                            String key = entry.getKey();
                            if (!currentValues.containsKey(key)) {
                                currentValues.put(key, entry.getValue());
                            }
                        }
                    } else {
                        currentValues.putAll(inhiretedValues);
                    }

                    StringBuffer newAttribs = new StringBuffer();
                    for (Map.Entry<String, Object> entry : currentValues.entrySet()) {
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

    public static Map<String, Object> toMap(String styleProperties) {
        Map<String, Object> map = new HashMap<String, Object>();
        String[] keyValuePairs = styleProperties.trim().split(";");
        for (String keyValuePair : keyValuePairs) {
            String[] splitedValue = keyValuePair.split(":");
            if (splitedValue.length == 2) {
                map.put(splitedValue[0], splitedValue[1]);
            }
        }

        return map;
    }

    public static Map<String, String> toMap(Attributes attributes) {
        Map<String, String> attrMap = new HashMap<String, String>();

        if (attributes != null) {
            for (Attribute attr : attributes) {
                if (attr.getKey() != "text") { // avoid text attribute
                    String key = attr.getKey() != null ? attr.getKey().trim() : null;
                    String value = attr.getValue() != null ? attr.getValue().trim() : null;
                    attrMap.put(key, value);
                }
            }
        }

        return attrMap;
    }

    public static String ensureNoBreakLinesNorTabs(String cleanedHtmlPart) {
        return cleanedHtmlPart.replaceAll("\n", "").replaceAll("\t", "");
    }
}
