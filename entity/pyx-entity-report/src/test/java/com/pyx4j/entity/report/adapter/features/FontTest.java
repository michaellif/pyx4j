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
import org.junit.Test;

public class FontTest extends StyledFeaturesBase {

    @Test
    public void testFontCases() throws IOException {

        final String font_tag = "<font>This is a bold text</font>";
        testStyledAttributes(font_tag);

        final String font_tag_empty_size = "<font size=\"\">This is a bold text</font>";
        testStyledAttributes(font_tag_empty_size);

        final String font_tag_size = "<font size=\"8\">This is a bold text</font>";
        testStyledAttributes(font_tag_size, createFontSizeAttribute("8"));

        final String font_tag_color = "<font color=\"green\">This is a bold text</font>";
        testStyledAttributes(font_tag_color, createStyledColorAttribute("green"));

        final String font_tag_size_and_color = "<font size=\"1\" color=\"blue\">This is a bold text</font>";
        testStyledAttributes(font_tag_size_and_color, new Attribute[] { createFontSizeAttribute("1"), createStyledColorAttribute("blue") });
    }

}