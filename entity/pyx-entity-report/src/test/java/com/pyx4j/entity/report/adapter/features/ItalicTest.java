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
 * Created on Apr 7, 2016
 * @author ernestog
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.pyx4j.entity.report.adapter.features;

import java.io.IOException;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.Assert;
import org.junit.Test;

public class ItalicTest extends StyledFeaturesBase {

    @Test
    public void testItalicCases() throws IOException {

        final String simple_italic_tag = "<i>This is a bold text</i>";
        testStyledAttributes(simple_italic_tag, createDefaultBooleanAttribute("isItalic"));

        final String simple_italic_css = "<span style=\"font-style:italic;\">This is a italic text</span>";
        testStyledAttributes(simple_italic_css, createDefaultBooleanAttribute("isItalic"));

        final String complex_italic_tag = "regular text 1<i>This is a italic text</i> regular text 2";
        testComplexItalic(complex_italic_tag);

        final String complex_italic_css = "regular text 1 <span style=\"font-style:italic;\">This is a italic text</span> regular text 2";
        testComplexItalic(complex_italic_css);
    }

    public void testComplexItalic(String htmlPart) throws IOException {
        Element styledContent = getStyledContent(htmlPart);
        Assert.assertTrue("Expected at lease node", !styledContent.childNodes().isEmpty());

        // Test regular text
        Node elementTextNode1 = styledContent.childNodes().get(0);
        assertStyledElement(elementTextNode1);
        assertAttributes(elementTextNode1, (Attribute[]) null);

        // Test formatted text
        Node elementNode = styledContent.childNodes().get(1);
        assertStyledElement(elementNode);
        assertAttributes(elementNode, createDefaultBooleanAttribute("isItalic"));

        // Test regular text
        Node elementTextNode2 = styledContent.childNodes().get(2);
        assertStyledElement(elementTextNode2);
        assertAttributes(elementTextNode2, (Attribute[]) null);
    }

}
