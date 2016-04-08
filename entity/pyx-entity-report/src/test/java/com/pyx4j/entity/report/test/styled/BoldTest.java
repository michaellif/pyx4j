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
package com.pyx4j.entity.report.test.styled;

import java.io.IOException;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.junit.Assert;
import org.junit.Test;

public class BoldTest extends StyledFeaturesBase {

    private static final String SIMPLE_BOLD_TAG = "<b>This is a bold text</b>";

    private static final String SIMPLE_BOLD_CSS = "<span style=\"font-weight:bold;\">This is a bold text</span>";

    private static final String COMPLEX_BOLD_TAG = "regular text 1<b>This is a bold text</b> regular text 2";

    private static final String COMPLEX_BOLD_CSS = "regular text 1 <span style=\"font-weight:bold;\">This is a bold text</span> regular text 2 ";

    @Test
    public void testBoldCases() throws IOException {

        testStyledAttributes(SIMPLE_BOLD_TAG, createDefaultBooleanAttribute("isBold"));

        testStyledAttributes(SIMPLE_BOLD_CSS, createDefaultBooleanAttribute("isBold"));

        testComplexBold(COMPLEX_BOLD_TAG);

        testComplexBold(COMPLEX_BOLD_CSS);
    }

    public void testComplexBold(String htmlPart) throws IOException {
        Element styledContent = getStyledContent(htmlPart);
        Assert.assertTrue("Expected at lease node", !styledContent.childNodes().isEmpty());

        // Test regular text
        Node elementTextNode = styledContent.childNodes().get(0);
        assertStyledElement(elementTextNode);
        assertAttributes(elementTextNode); //, (Attribute[]) null);

        // Test formatted text
        Node elementNode = styledContent.childNodes().get(1);
        assertStyledElement(elementNode);
        assertAttributes(elementNode, createDefaultBooleanAttribute("isBold"));
    }

}
