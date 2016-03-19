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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;

import com.pyx4j.gwt.server.IOUtils;

public class JasperReportHTMLAdapterTest {

    @Test
    public void testRemoveExtraStylesAndFontFamily() throws IOException {

        String htmlText = null;

        htmlText = IOUtils.getTextResource("mixedHtml.html", this.getClass());

        if (htmlText == null) {
            Assert.fail("Unable to load html resource for test");
        }

        String plaintext = JasperReportHTMLAdapter.makeJasperCompatibleHTML(htmlText);

        // Test no unsupported tags in html
        String[] tags = { "style", "xml" };
        for (String tagName : tags) {
            Assert.assertTrue("Found <" + tagName + "> tag yet but it shouldn't be present", !hasTag(plaintext, tagName));
        }

        // Test no font-family nor font face attributes
        Assert.assertTrue("Found font-family style attribute yet but it shouldn't be present", !hasFontFamilyStyleAttr(plaintext));

        // Test no face in font tag
        Assert.assertTrue("Found face attribute in <font> tag yet but it shouldn't be present", !hasFaceAttribute(plaintext));

    }

    private boolean hasTag(String html, String tagName) {
        Elements htmlElements = Jsoup.parse(html).getAllElements();
        for (Element e : htmlElements) {
            if (e.tagName().equalsIgnoreCase(tagName)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasFontFamilyStyleAttr(String html) {
        Elements htmlElements = Jsoup.parse(html).getAllElements();
        for (Element e : htmlElements) {
            Attributes attributes = e.attributes();
            for (Attribute attr : attributes) {
                if (attr.getKey().equals("style")) {
                    String[] styleItems = attr.getValue().trim().split(";");
                    for (String item : styleItems) {
                        if (item.contains("font-family")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean hasFaceAttribute(String html) {
        Elements htmlElements = Jsoup.parse(html).getAllElements();
        for (Element e : htmlElements) {
            if (e.tagName().equals("font")) {
                Attributes attributes = e.attributes();
                for (Attribute attr : attributes) {
                    if (attr.getKey().equals("face")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
