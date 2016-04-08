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

import org.jsoup.nodes.Attribute;
import org.junit.Test;

public class FontTest extends StyledFeaturesBase {

    private static final String SIMPLE_FONT_TAG = "<font>This is a bold text</font>";

    private static final String SIMPLE_FONT_TAG_SIZE = "<font size=\"8\">This is a bold text</font>";

    private static final String SIMPLE_FONT_TAG_COLOR = "<font color=\"green\">This is a bold text</font>"; // TODO Add other colors formats

    private static final String SIMPLE_FONT_TAG_SIZE_AND_COLOR = "<font size=\"1\" color=\"blue\">This is a bold text</font>";

    @Test
    public void testFontCases() throws IOException {

        testStyledAttributes(SIMPLE_FONT_TAG);

        testStyledAttributes(SIMPLE_FONT_TAG_SIZE, createFontSizeAttribute("8"));

        testStyledAttributes(SIMPLE_FONT_TAG_COLOR, createStyledColorAttribute("green"));

        testStyledAttributes(SIMPLE_FONT_TAG_SIZE_AND_COLOR, new Attribute[] { createFontSizeAttribute("1"), createStyledColorAttribute("blue") });
    }

}