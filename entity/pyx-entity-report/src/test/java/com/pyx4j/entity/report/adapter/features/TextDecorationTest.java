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

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.Assert;
import org.junit.Test;

import com.pyx4j.commons.SimpleMessageFormat;

public class TextDecorationTest extends StyledFeaturesBase {

    @Test
    public void testTextDecorationCases() throws IOException {

        // ********************************** UNDERLINE ********************************
        final String underline_tag = "<u>This is a underlined text</u>";
        testSimple(underline_tag, "isUnderline");

        final String underline_css = "<span style=\"text-decoration:underline;\">This is a underlined text</span>";
        testSimple(underline_css, "isUnderline");

        // ********************************** STRIKETHROUGH ********************************
        final String striketrough_tag_s = "<s>This is a strikethrough text</s>";
        testSimple(striketrough_tag_s, "isStrikeThrough");

        final String striketrough_tag_del = "<del>This is a strikethrough text</del>";
        testSimple(striketrough_tag_del, "isStrikeThrough");

        final String striketrough_tag_strike = "<strike>This is a strikethrough text</strike>";
        testSimple(striketrough_tag_strike, "isStrikeThrough");

        final String striketrough_tag_css = "<span style=\"text-decoration:line-through;\">This is a strikethrough text</span>";
        testSimple(striketrough_tag_css, "isStrikeThrough");
    }

    public void testSimple(String htmlPart, String styledAttributeSet) throws IOException {

        Element styledContent = getStyledContent(htmlPart);
        Assert.assertTrue("Expected one node", !styledContent.childNodes().isEmpty());

        // Test formatted text
        Node elementNode = styledContent.childNodes().get(0);
        assertStyledElement(elementNode);

        Assert.assertTrue(SimpleMessageFormat.format("Expected \"{0}\" attributes but \"{1} \"", styledAttributeSet, elementNode.attributes()),
                hasAttributesSetToTrue(elementNode, false, styledAttributeSet));

    }

}
