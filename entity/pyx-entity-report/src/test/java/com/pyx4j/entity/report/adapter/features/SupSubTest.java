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
 * Created on Apr 8, 2016
 * @author ernestog
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.pyx4j.entity.report.adapter.features;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.Assert;
import org.junit.Test;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.report.adpater.JasperReportStyledAdapter;

public class SupSubTest extends StyledFeaturesBase {

    @Test
    public void testSupSubCases() throws IOException {

        final String simple_SUP = "<sup>This is sup text</sup>";
        testSpecialTagSimple(simple_SUP, "sup");

        final String simple_SUB = "<sub>This is sub text</sup>";
        testSpecialTagSimple(simple_SUB, "sub");

        final String complex_SUP = "regular text<sup><span style=\"color:red;\">sup text red</span>sup black text</sup>";
        testSpecialTagComplex(complex_SUP, "sub");
    }

    private void testSpecialTagSimple(String htmlPart, String tagName) {
        Element elementContent = Jsoup.parse(htmlPart).select("body").get(0);
        Assert.assertTrue("No nodes found in test html source", !elementContent.childNodes().isEmpty());

        String styledText = new JasperReportStyledAdapter().makeJasperCompatibleStyled(elementContent.html());

        Element styledContent = Jsoup.parse(styledText).select("head").first(); // Because styled text tag, jsoup inserts in head instead of body

        Node elementNode = styledContent.childNodes().get(0);
        Node supNode = elementNode.childNodes().get(0);

        if (supNode instanceof Element) {
        } else if (supNode instanceof DataNode) {
            DataNode dataNode = (DataNode) supNode;
            if (!dataNode.getWholeData().startsWith("<" + tagName + ">")) {
                Assert.fail(SimpleMessageFormat.format("Node text should start with \"{0}\" but found \"{1}\"", tagName, dataNode.getWholeData()));
            }
        }
    }

    private void testSpecialTagComplex(String htmlPart, String tagName) {
        Element elementContent = Jsoup.parse(htmlPart).select("body").get(0);
        Assert.assertTrue("No nodes found in test html source", !elementContent.childNodes().isEmpty());

        String styledText = new JasperReportStyledAdapter().makeJasperCompatibleStyled(elementContent.html());

        Element styledContent = Jsoup.parse(styledText).select("head").first(); // Because styled text tag, jsoup inserts in head instead of body

        // Test sup part 1
        Node elementNode1 = styledContent.childNodes().get(0);
        assertAttributes(elementNode1);

        // Test sup part 1
        Node elementNode2 = styledContent.childNodes().get(1);
        assertAttributes(elementNode2, createStyledColorAttribute("red"));
        testSpecialTagSimple(elementNode2.childNodes().get(0).outerHtml(), "sup");

        // Test sup part 1
        Node elementNode3 = styledContent.childNodes().get(2);
        assertAttributes(elementNode3);
        testSpecialTagSimple(elementNode3.childNodes().get(0).outerHtml(), "sup");

    }

}
