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

public class StyleInheritanceTest extends StyledFeaturesBase {

    @Test
    public void testInheritanceCases() throws IOException {

        // *********** INHERITANCE WITH TAGS ***********
        String tags_1 = "<b><i>This is a bold + italic text</i></b>";
        testSimple(tags_1, createDefaultBooleanAttribute("isBold"), createDefaultBooleanAttribute("isItalic"));

        String tags_2 = "<b>This is a bold but <i> this is bold + italic </i> and this is bold again</b>";
        testComplex(tags_2);

        // *********** INHERITANCE WITH CSS ***********
        // NOTE: Use span tag because div and p insert breaklines and each one is treated as other text node
        String css_1 = "<span style=\"background-color:red;\"><span style=\"font-size:25;\">this is big blue text</span></span>";
        testSimple(css_1, createStyledBackgroundColorAttribute("red"), createCssStyleSizeAttribute("25"));

        String css_2 = "<span style=\"font-weight:bold;\">This is a bold but<span style=\"font-style:italic;\"> this is bold + italic </span> and this is bold again</span>";
        testComplex(css_2);

    }

    private void testSimple(String html, Attribute... attributes) throws IOException {
        testStyledAttributes(html, attributes);
    }

    private void testComplex(String htmlPart) throws IOException {
        Element styledContent = getStyledContent(htmlPart);
        Assert.assertTrue("Expected one node", !styledContent.childNodes().isEmpty());

        // Test first part
        Node firstNode = styledContent.childNodes().get(0);
        assertAttributes(firstNode, createDefaultBooleanAttribute("isBold"));

        // Test inheritance part
        Node inheritanceNode = styledContent.childNodes().get(1);
        assertAttributes(inheritanceNode, new Attribute[] { createDefaultBooleanAttribute("isBold"), createDefaultBooleanAttribute("isItalic") });

        // Test last part
        Node lastNode = styledContent.childNodes().get(2);
        assertAttributes(lastNode, createDefaultBooleanAttribute("isBold"));
    }

}