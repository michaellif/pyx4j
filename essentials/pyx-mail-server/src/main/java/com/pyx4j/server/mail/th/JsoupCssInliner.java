/*
/*
 * Pyx4j framework
 * Copyright (C) 2008-2016 pyx4j.com.
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
 * Created on Mar 6, 2016
 * @author vlads
 *
 */
package com.pyx4j.server.mail.th;

import java.io.IOException;
import java.io.StringReader;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;

import com.pyx4j.config.shared.ApplicationMode;

public class JsoupCssInliner implements CssInliner {

    private boolean removeClasses = false;

    private boolean removeEmbededStyles = false;

    @Override
    public String inline(String htmlContent) {
        Document document = Jsoup.parse(htmlContent);
        CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
        InputSource source = new InputSource(new StringReader(embededStyles(document)));
        CSSStyleSheet stylesheet;
        try {
            stylesheet = parser.parseStyleSheet(source, null, null);
        } catch (IOException e) {
            throw new Error(e);
        }

        CSSRuleList ruleList = stylesheet.getCssRules();
        Map<Element, Map<String, String>> allElementsStyles = new IdentityHashMap<>();
        for (int ruleIndex = 0; ruleIndex < ruleList.getLength(); ruleIndex++) {
            CSSRule item = ruleList.item(ruleIndex);
            if (item instanceof CSSStyleRule) {
                CSSStyleRule styleRule = (CSSStyleRule) item;
                String cssSelector = styleRule.getSelectorText();

                if (cssSelector.contains(":")) {
                    // a:hover -> pseudo-selector can't be inlined
                    continue;
                }

                Elements elements = document.select(cssSelector);
                for (int elementIndex = 0; elementIndex < elements.size(); elementIndex++) {
                    Element element = elements.get(elementIndex);
                    Map<String, String> elementStyles = allElementsStyles.get(element);
                    if (elementStyles == null) {
                        elementStyles = new LinkedHashMap<String, String>();
                        allElementsStyles.put(element, elementStyles);
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

        for (Map.Entry<Element, Map<String, String>> elementEntry : allElementsStyles.entrySet()) {
            Element element = elementEntry.getKey();
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, String> styleEntry : elementEntry.getValue().entrySet()) {
                builder.append(styleEntry.getKey()).append(":").append(styleEntry.getValue()).append(";");
            }
            builder.append(element.attr("style"));
            element.attr("style", builder.toString());
            if (removeClasses) {
                element.removeAttr("class");
            }
        }

        Document.OutputSettings settings = document.outputSettings();
        settings.prettyPrint(ApplicationMode.isDevelopment());
        settings.escapeMode(Entities.EscapeMode.extended);
        settings.charset("ASCII");

        return document.html();
    }

    private String embededStyles(Document doc) {
        Elements els = doc.select("style");
        StringBuilder styles = new StringBuilder();
        for (Element e : els) {
            if (!"true".equals(e.attr("data-skip-inline"))) {
                styles.append(e.data());
                if (removeEmbededStyles) {
                    e.remove();
                }
            }
        }
        return styles.toString();
    }

}
