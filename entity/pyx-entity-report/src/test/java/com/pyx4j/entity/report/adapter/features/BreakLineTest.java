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
 * Created on Apr 12, 2016
 * @author ernestog
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.pyx4j.entity.report.adapter.features;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.Assert;
import org.junit.Test;

import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.report.adpater.JasperReportStyledAdapter;

public class BreakLineTest extends StyledFeaturesBase {

    private static final String STYLED_BREAK_LINE = "<style>\n</style>";

    @Test
    public void testBreakLineCases() throws IOException {

        // No breaklines -> tag no breakliner
        {
            final String htmlPart = "<b>This is a bold text</b>";
            String styledText = new JasperReportStyledAdapter().makeJasperCompatibleStyled(htmlPart);
            assertNoBreakLineAtBeginning(styledText);
            assertNoBreakLineAtEnd(styledText);
        }

        // Empty breakliner tag
        {
            final String htmlPart = "<p></p>";
            String styledText = new JasperReportStyledAdapter().makeJasperCompatibleStyled(htmlPart);
            assertNoBreakLineAtBeginning(styledText);
            assertNoBreakLineAtEnd(styledText);
        }

        //  Breakliner tag with some text
        {
            final String htmlPart = "<p>hello</p>";
            String styledText = new JasperReportStyledAdapter().makeJasperCompatibleStyled(htmlPart);
            assertNoBreakLineAtBeginning(styledText);
            assertNoBreakLineAtEnd(styledText);
        }

        //  Breakliner tag with <br> at start
        {
            final String htmlPart = "<br><p>hello</p>";
            String styledText = new JasperReportStyledAdapter().makeJasperCompatibleStyled(htmlPart);
            assertBreakLineAtBeginning(styledText);
            assertNoBreakLineAtEnd(styledText);
        }

        //  Breakliner tag with <br> at end
        {
            final String htmlPart = "<p>hello</p><br>";
            String styledText = new JasperReportStyledAdapter().makeJasperCompatibleStyled(htmlPart);
            assertNoBreakLineAtBeginning(styledText);
            assertBreakLineAtEnd(styledText);
        }

        //  Breakliner chained tags with some text
        {
            final String htmlPart = "<p></p><p></p><div>hello</div><p></p><p></p>";
            String styledText = new JasperReportStyledAdapter().makeJasperCompatibleStyled(htmlPart);
            assertNoBreakLineAtBeginning(styledText);
            assertNoBreakLineAtEnd(styledText);
            System.out.println("");
        }

        //  Breakliner chained tags with some text
        {
            final String htmlPart = "<div>hi there <p> p1</p> <div> p2 <p>p3</p></div></div>";
            String styledText = new JasperReportStyledAdapter().makeJasperCompatibleStyled(htmlPart);
            assertNoDoubleBreakLines(styledText);
        }

        //  Breakliner chained tags with some text
        {
            final String htmlPart = "<div>hi there<p>p1</p><br><div>p2<p>p4</p><br>bla</div></div>";
            String styledText = new JasperReportStyledAdapter().makeJasperCompatibleStyled(htmlPart);
            Element styledContent = Jsoup.parse(styledText).select("head").first(); // Because styled text tag, jsoup inserts in head instead of body

            // Verify double breakline because of <br>
            Node elementNode3 = styledContent.childNodes().get(3);
            assertIsBreakLineNode(elementNode3);
            Node elementNode4 = styledContent.childNodes().get(4);
            assertIsBreakLineNode(elementNode4);
            System.out.println("");
            // Verify next node is not breakline
            Node elementNode5 = styledContent.childNodes().get(5);
            assertIsNotBreakLineNode(elementNode5);

            // Verify double breakline because of <br>
            Node elementNode8 = styledContent.childNodes().get(8);
            assertIsBreakLineNode(elementNode8);
            Node elementNode9 = styledContent.childNodes().get(9);
            assertIsBreakLineNode(elementNode9);
            System.out.println("");
            // Verify next node is not breakline
            Node elementNode10 = styledContent.childNodes().get(10);
            assertIsNotBreakLineNode(elementNode10);

        }

    }

    private void assertIsNotBreakLineNode(Node node) {
        String nodeHtml = node.outerHtml();
        Assert.assertTrue(SimpleMessageFormat.format("Expected no-breakline node but found \"{0}\"", nodeHtml), !nodeHtml.equalsIgnoreCase(STYLED_BREAK_LINE));
    }

    private void assertIsBreakLineNode(Node node) {
        String nodeHtml = node.outerHtml();
        Assert.assertTrue(SimpleMessageFormat.format("Expected breakline node but found \"{0}\"", nodeHtml), nodeHtml.equalsIgnoreCase(STYLED_BREAK_LINE));
    }

    private void assertNoDoubleBreakLines(String styledText) {
        Assert.assertTrue(SimpleMessageFormat.format("Expected no double breakline but found \"{0}\"", styledText),
                !styledText.contains(STYLED_BREAK_LINE + STYLED_BREAK_LINE));
    }

    private void assertNoBreakLineAtBeginning(String styledText) {
        Assert.assertTrue(SimpleMessageFormat.format("No breakline was expected at beginning but found \"{0}\"", styledText),
                !styledText.startsWith(STYLED_BREAK_LINE));
    }

    private void assertBreakLineAtBeginning(String styledText) {
        Assert.assertTrue(SimpleMessageFormat.format("No breakline was expected at beginning but found \"{0}\"", styledText),
                styledText.startsWith(STYLED_BREAK_LINE));
    }

    private void assertNoBreakLineAtEnd(String styledText) {
        Assert.assertTrue(SimpleMessageFormat.format("No breakline was expected at the end but found \"{0}\"", styledText),
                !styledText.endsWith(STYLED_BREAK_LINE));
    }

    private void assertBreakLineAtEnd(String styledText) {
        Assert.assertTrue(SimpleMessageFormat.format("No breakline was expected at the end but found \"{0}\"", styledText),
                styledText.endsWith(STYLED_BREAK_LINE));
    }

}
