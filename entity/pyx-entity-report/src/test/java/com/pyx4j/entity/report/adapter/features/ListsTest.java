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

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;
import org.junit.Assert;
import org.junit.Test;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.report.adpater.JasperReportStyledAdapter;
import com.pyx4j.entity.report.adpater.JasperReportStyledUtils;
import com.pyx4j.gwt.server.IOUtils;

public class ListsTest extends StyledFeaturesBase {

    @Test
    public void testListsCases() throws IOException {

        String htmlPart = IOUtils.getTextResource("list.html", this.getClass());

        Element elementContent = Jsoup.parse(htmlPart).select("body").get(0);
        Assert.assertTrue("No nodes found in test html source", !elementContent.childNodes().isEmpty());

        String styledText = JasperReportStyledAdapter.makeJasperCompatibleStyled(elementContent.html());
        Element styledContent = Jsoup.parse(styledText).select("head").first(); // Because styled text tag, jsoup inserts in head instead of body

        // Assert 1st <li> in 1st <ol> element
        {
            Node elementNode = styledContent.childNodes().get(1);
            DataNode dataNode = (DataNode) elementNode.childNodes().get(0);
            String text = dataNode.getWholeData();
            assertLiElement(text, Tag.valueOf("ol"), 1, 1);
        }

        // Assert 1st <li> in 2nd <ol> element
        {
            Node elementNode = styledContent.childNodes().get(6);
            DataNode dataNode = (DataNode) elementNode.childNodes().get(0);
            String text = dataNode.getWholeData();
            assertLiElement(text, Tag.valueOf("ol"), 2, 1);
        }

        // Assert 1st <li> in 1st <ul> inside 2 <ol> element
        {
            Node elementNode = styledContent.childNodes().get(11);
            DataNode dataNode = (DataNode) elementNode.childNodes().get(0);
            String text = dataNode.getWholeData();
            assertLiElement(text, Tag.valueOf("ul"), 3, 1);
        }

        // Assert 2st <li> in 1st <ul> inside 2 <ol> element
        {
            Node elementNode = styledContent.childNodes().get(14);
            DataNode dataNode = (DataNode) elementNode.childNodes().get(0);
            String text = dataNode.getWholeData();
            assertLiElement(text, Tag.valueOf("ul"), 3, 2);
        }

        // Assert 2nd <li> in 2nd <ol> inside 1 <ol> element
        {
            Node elementNode = styledContent.childNodes().get(18);
            DataNode dataNode = (DataNode) elementNode.childNodes().get(0);
            String text = dataNode.getWholeData();
            assertLiElement(text, Tag.valueOf("ol"), 2, 2);
        }

        // Assert 1st <li> in 3nd <ol> inside no list element
        {
            Node elementNode = styledContent.childNodes().get(32);
            DataNode dataNode = (DataNode) elementNode.childNodes().get(0);
            String text = dataNode.getWholeData();
            assertLiElement(text, Tag.valueOf("ol"), 1, 1);
        }

    }

    private static void assertLiElement(String text, Tag htmlTagListParent, int indentLevel, int liIndexInList) {

        assertIndentLevel(text, indentLevel);

        assertLiFirstChar(text, htmlTagListParent, liIndexInList);

    }

    private static void assertLiFirstChar(String text, Tag htmlTagListParent, int indexInList) {
        switch (htmlTagListParent.getName()) {
        case "ul":
            Assert.assertTrue(SimpleMessageFormat.format("Expected to star with {0} but text is \"{1}\"", "\u2022", text),
                    text.trim().startsWith(String.valueOf(JasperReportStyledUtils.UL_LI_STARTER)));
            break;
        case "ol":
            Assert.assertTrue(SimpleMessageFormat.format("Expected to star with {0} but text is \"{1}\"", String.valueOf(indexInList), text),
                    text.trim().startsWith(String.valueOf(indexInList)));
            break;
        default:
        }
    }

    private static void assertIndentLevel(String text, int indentLevel) {
        boolean fail = false;

        if (text.startsWith("\n")) {
            text = text.substring(1);
        }

        int expectedSpaces = indentLevel * JasperReportStyledUtils.DEFAULT_TAB_SIZE.length();

        if (expectedSpaces > text.length()) {
            fail = true;
        } else {
            for (int i = 0; i < expectedSpaces; i++) {
                if (new Character(text.charAt(i)).compareTo(' ') != 0) {
                    fail = true;
                }
            }
        }

        if (fail) {
            Assert.fail(SimpleMessageFormat.format("Wrong indent. Expected {0} x TAB but received this text \"{1}\"", indentLevel, text));
        }
    }

}
