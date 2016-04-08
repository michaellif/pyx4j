/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Mar 19, 2016
 * @author vlads
 */
package com.pyx4j.entity.report;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

import com.google.common.base.Joiner;
import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;

import com.pyx4j.config.shared.ApplicationMode;

public class JasperReportHTMLAdapter {

    // The css properties supported by jasper without modification
    private static List<String> supportedStyleProperties = Arrays.asList(//
            "font-weight", "font-style", "text-decoration", //
            "color", "background-color", "background");

    /**
     * Removes <style> tag and <xml> Microsoft word style definition from
     * html code snippet. Also remove font-family attribute from all tags and face
     * attribute from <font> tag.
     *
     * @param htmlPart
     *            html code snippet
     * @return inner html of body (will not return complete html document)
     */
    public static String makeJasperCompatibleHTML(String htmlPart) {
        Document dirtyDocument = Jsoup.parse(htmlPart);

        CSSRuleList cssRuleList = buildCSSRuleList(extractEmbededStyles(dirtyDocument));
        Map<Element, Map<String, String>> elementsStyles = buildStyleAttributes(dirtyDocument, cssRuleList);

        // Keep only supported style values
        for (Element element : dirtyDocument.getAllElements()) {
            Map<String, String> elementStylePropertiesCombined = new LinkedHashMap<String, String>();
            Map<String, String> elementStylePropertiesFromCSS = elementsStyles.get(element);
            if (elementStylePropertiesFromCSS != null) {
                elementStylePropertiesCombined.putAll(elementStylePropertiesFromCSS);
            }
            // style attribute values override the style in document
            for (String nameValue : element.attr("style").split(";")) {
                String[] nameValuParts = nameValue.split(":");
                if (nameValuParts.length > 1) {
                    elementStylePropertiesCombined.put(nameValuParts[0].trim(), nameValuParts[1].trim());
                }
            }

            // keep add supported properties only
            Map<String, String> elementStylePropertiesNew = new LinkedHashMap<String, String>();

            String fontSize = elementStylePropertiesCombined.get("font-size");
            if (fontSize != null) {
                elementStylePropertiesNew.put("font-size", normalizeFontSize(fontSize));
            }

            for (String property : supportedStyleProperties) {
                String propertyValue = elementStylePropertiesCombined.get(property);
                if (propertyValue != null) {
                    elementStylePropertiesNew.put(property, propertyValue);
                }

            }

            String newStyle = Joiner.on("; ").withKeyValueSeparator(":").join(elementStylePropertiesNew);

            if (newStyle.length() != 0) {
                element.attr("style", newStyle);
            } else {
                element.removeAttr("style");
            }

            // remove classes...
            element.removeAttr("class");
        }

        // Base On http://jasperreports.sourceforge.net/sample.reference/styledtext/
        // TODO This may affect presentation in browser; so may be moved to new function or approach can be changed...
        Whitelist whitelist = Whitelist.none()//
                .addTags("b", "i", "u", "font", "sup", "sub", "li", "br", "ol", "ul", "strike", "s", "del") //
                .addAttributes("font", "size", "color") //
                .addAttributes("span", "style") //
                .addAttributes("p", "style") //
                .addAttributes("div", "style");

        Cleaner cleaner = new Cleaner(whitelist);
        Document document = cleaner.clean(dirtyDocument);

        Document.OutputSettings settings = document.outputSettings();
        settings.prettyPrint(ApplicationMode.isDevelopment());
        settings.escapeMode(Entities.EscapeMode.extended);
        settings.charset(StandardCharsets.UTF_8);

        return document.select("body").html();
    }

    private static String normalizeFontSize(String fontSize) {
        Matcher matcher = Pattern.compile("(\\d+\\.?\\d*)(.*)").matcher(fontSize);
        if (matcher.find()) {
            switch (matcher.group(2)) {
            case "px":
                fontSize = 0.75 * Float.valueOf(matcher.group(1)) + "pt";
                break;
            case "%":
                fontSize = 0.12 * Float.valueOf(matcher.group(1)) + "pt";
                break;
            case "em":
                fontSize = 12 * Float.valueOf(matcher.group(1)) + "pt";
                break;
            default:
                break;
            }
        }
        return fontSize;
    }

    private static Map<Element, Map<String, String>> buildStyleAttributes(Document document, CSSRuleList cssRuleList) {
        Map<Element, Map<String, String>> elementsStyles = new IdentityHashMap<>();
        for (int ruleIndex = 0; ruleIndex < cssRuleList.getLength(); ruleIndex++) {
            CSSRule item = cssRuleList.item(ruleIndex);
            if (item instanceof CSSStyleRule) {
                CSSStyleRule styleRule = (CSSStyleRule) item;
                String cssSelector = styleRule.getSelectorText();

                Elements elements = document.select(cssSelector);
                for (int elementIndex = 0; elementIndex < elements.size(); elementIndex++) {
                    Element element = elements.get(elementIndex);
                    Map<String, String> elementStyles = elementsStyles.get(element);
                    if (elementStyles == null) {
                        elementStyles = new LinkedHashMap<String, String>();
                        elementsStyles.put(element, elementStyles);
                    }
                    CSSStyleDeclaration style = styleRule.getStyle();
                    for (int propertyIndex = 0; propertyIndex < style.getLength(); propertyIndex++) {
                        String propertyName = style.item(propertyIndex);
                        String propertyValue = style.getPropertyValue(propertyName);
                        elementStyles.put(propertyName, propertyValue);
                    }
                }

            }
        }
        return elementsStyles;
    }

    private static CSSRuleList buildCSSRuleList(String cssStyles) {
        CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
        InputSource source = new InputSource(new StringReader(cssStyles));
        CSSStyleSheet stylesheet;
        try {
            stylesheet = parser.parseStyleSheet(source, null, null);
        } catch (IOException e) {
            throw new Error(e);
        }
        return stylesheet.getCssRules();
    }

    // Also removes embedded Styles tags if any
    private static String extractEmbededStyles(Document doc) {
        StringBuilder styles = new StringBuilder();
        for (Element element : doc.select("style")) {
            styles.append(element.data());
            element.remove();
        }
        return styles.toString();
    }

}
