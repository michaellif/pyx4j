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
package com.pyx4j.entity.report.test.styled;

import java.io.IOException;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.Assert;
import org.junit.Test;

public class TextDecorationTest extends StyledFeaturesBase {

    // ********************************** UNDERLINE ********************************
    private static final String SIMPLE_UNDERLINE_TAG = "<u>This is a underlined text</u>";

    private static final String SIMPLE_UNDERLINE_CSS = "<span style=\"text-decoration:underline;\">This is a underlined text</span>";

    // ********************************** STRIKETHROUGH ********************************
    private static final String SIMPLE_STRIKETROUGH_TAG_S = "<s>This is a strikethrough text</s>";

    private static final String SIMPLE_STRIKETROUGH_TAG_DEL = "<del>This is a strikethrough text</del>";

    private static final String SIMPLE_STRIKETROUGH_TAG_STRIKE = "<strike>This is a strikethrough text</strike>";

    private static final String SIMPLE_STRIKETROUGH_CSS = "<span style=\"text-decoration:line-through;\">This is a strikethrough text</span>";

    @Test
    public void testTextDecorationCases() throws IOException {

        testSimpleUnderline(SIMPLE_UNDERLINE_TAG, true, "isUnderline");

        testSimpleUnderline(SIMPLE_UNDERLINE_CSS, false, "isUnderline");

        testStyledAttributes(SIMPLE_STRIKETROUGH_TAG_S, createDefaultBooleanAttribute("isStrikeThrough"));

        testStyledAttributes(SIMPLE_STRIKETROUGH_TAG_DEL, createDefaultBooleanAttribute("isStrikeThrough"));

        testStyledAttributes(SIMPLE_STRIKETROUGH_TAG_STRIKE, createDefaultBooleanAttribute("isStrikeThrough"));

        testSimpleUnderline(SIMPLE_STRIKETROUGH_CSS, false, "isStrikeThrough");
    }

    public void testSimpleUnderline(String htmlPart, boolean onlyTheseAttributes, String styledAttributeSet) throws IOException {

        Element styledContent = getStyledContent(htmlPart);
        Assert.assertTrue("Expected one node", !styledContent.childNodes().isEmpty());

        // Test formatted text
        Node elementNode = styledContent.childNodes().get(0);
        assertStyledElement(elementNode);

        Assert.assertTrue("Expected underlined attribute", hasAttributesSetToTrue(elementNode, onlyTheseAttributes, styledAttributeSet));

    }

}
