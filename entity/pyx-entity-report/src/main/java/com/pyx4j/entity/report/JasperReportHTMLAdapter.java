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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JasperReportHTMLAdapter {

    /**
     * Removes <style> tag and <xml> microsoft word style definition from
     * html code snippet. Also remove font-family attribute from all tags and face
     * attribute from <font> tag.
     *
     * @param html_css_styles
     *            html code snippet
     * @return inner html of body (will not return complete html document)
     */
    public static String makeJasperCompatibleHTML(String htmlPart) {
        Document document = Jsoup.parse(htmlPart);
        Elements elements = document.getAllElements();

        // Although style tag should be placed in <head> by Jsoup, we'll ensure
        // not to have any tag inside html body
        for (Element e : elements) {
            switch (e.tagName()) {
            case "style":
                e.remove();
                break;
            case "font": {
                Attributes attributes = e.attributes();
                for (Attribute attr : attributes) {
                    if (attr.getKey().equals("face")) {
                        e.removeAttr(attr.getKey());
                    }
                }
            }
                break;
            default:
                Attributes attributes = e.attributes();
                for (Attribute attr : attributes) {
                    if (attr.getKey().equals("style")) {
                        String[] styleItems = attr.getValue().trim().split(";");
                        String newStyle = "";
                        for (String item : styleItems) {
                            if (!item.contains("font-family")) {
                                newStyle = newStyle.concat(item).concat(";");
                            }
                        }
                        attr.setValue(newStyle);
                    }
                }

            }
        }

        return document.select("body").html();
    }

}
