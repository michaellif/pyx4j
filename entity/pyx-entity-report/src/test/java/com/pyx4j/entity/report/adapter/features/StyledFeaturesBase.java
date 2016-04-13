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
 * Created on Apr 6, 2016
 * @author ernestog
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.pyx4j.entity.report.adapter.features;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.Assert;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.report.adapter.JasperReportStyledUtilsTest;
import com.pyx4j.entity.report.adpater.JasperReportStyledAdapter;
import com.pyx4j.entity.report.adpater.JasperReportStyledUtils;

public class StyledFeaturesBase {

    protected static void testStyledAttributes(String htmlPart, Attribute... expected) throws IOException {
        Element styledContent = getStyledContent(htmlPart);
        Assert.assertTrue("Expected one node", !styledContent.childNodes().isEmpty());

        // Test formatted text
        Node elementNode = styledContent.childNodes().get(0);
        assertStyledElement(elementNode);

        assertAttributes(elementNode, expected);
    }

    protected static Element getStyledContent(String htmlPart) {
        Element elementContent = Jsoup.parse(htmlPart).select("body").get(0);
        Assert.assertTrue("No nodes found in test html source", !elementContent.childNodes().isEmpty());
        String styledText = JasperReportStyledAdapter.makeJasperCompatibleStyled(elementContent.html());
        Element styledContent = Jsoup.parse(styledText).select("head").first(); // Because styled text tag, jsoup inserts in head instead of body

        return styledContent;
    }

    protected static void assertStyledElement(Node node) {
        Assert.assertTrue("Expected element node", node instanceof Element);
        Element element = (Element) node;
        Assert.assertTrue("Expected to be <style> tag element", element.tagName().equalsIgnoreCase("style"));
    }

    protected static void assertAttributes(Node node, Attribute... expected) {
        Element element = (Element) node;
        Attributes expectedAttributes = createAttributes(expected);
        Assert.assertTrue(
                SimpleMessageFormat.format("Expected attributes do not match. Expected \"{0}\" but found \"{1}\"", expectedAttributes, element.attributes()),
                JasperReportStyledUtilsTest.areSameAttributes(element.attributes(), expectedAttributes));
    }

    protected static Attribute createStyledAttribute(String key, String value) {
        return new Attribute(key, value);
    }

    protected static Attributes createAttributes(Attribute... attribs) {
        Attributes attributes = new Attributes();
        if (attribs != null) {
            for (Attribute attribute : attribs) {
                attributes.put(attribute);
            }
        }
        return attributes;
    }

    protected static Attribute createDefaultBooleanAttribute(String key) {
        return createStyledAttribute(key, String.valueOf(true));
    }

    protected static boolean hasAttributesSetToTrue(Node node, boolean onlyTheseAttributes, String... attributeName) {
        Attributes attributes = node.attributes();

        for (Attribute attribute : attributes) {
            if (containsEqualIgnoreCase(attribute.getKey(), attributeName) && attribute.getValue().equalsIgnoreCase("false")) {
                return false;
            } else if (containsEqualIgnoreCase(attribute.getKey(), attributeName) && attribute.getValue().equalsIgnoreCase("true")) {
                continue;
            } else if (!containsEqualIgnoreCase(attribute.getKey(), attributeName) && onlyTheseAttributes) {
                return false;
            }
        }

        return true;
    }

    private static boolean containsEqualIgnoreCase(String value, String... values) {
        for (String v : values) {
            if (v.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    protected Attribute createFontSizeAttribute(String value) {
        return createStyledAttribute("size", JasperReportStyledUtils.getTagFontSize(value));
    }

    protected Attribute createCssStyleSizeAttribute(String value) {
        return createStyledAttribute("size", JasperReportStyledUtils.getCssFontSize(value));
    }

    protected Attribute createStyledColorAttribute(String value) {
        return createStyledAttribute("forecolor", JasperReportStyledUtils.getCssColor(value));
    }

    protected Attribute createStyledBackgroundColorAttribute(String value) {
        return createStyledAttribute("backcolor", JasperReportStyledUtils.getCssColor(value));
    }

}
