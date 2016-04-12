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

import org.junit.Test;

public class ColorFormatsTest extends StyledFeaturesBase {

    @Test
    public void testBoldCases() throws IOException {

        final String color_css_name = "<p style=\"color:red;\">red color text</p>";
        testStyledAttributes(color_css_name, createStyledColorAttribute("red"));

        final String color_css_name_wrong = "<p style=\"color:invented-color;\">invented color (should be black color text)</p>";
        testStyledAttributes(color_css_name_wrong);

        final String color_css_rgb = "<p style=\"color:rgb(0,255,0);\">green color text</p>";
        testStyledAttributes(color_css_rgb, createStyledColorAttribute("green"));

        final String color_css_rgb_wrong = "<p style=\"color:rgb(0,255,255,0);\">wrong color (should be black color text)</p>";
        testStyledAttributes(color_css_rgb_wrong);

        final String color_css_hex = "<p style=\"color:#0000FF;\">blue color text</p>";
        testStyledAttributes(color_css_hex, createStyledColorAttribute("blue"));

        final String color_css_hex_wrong = "<p style=\"color:#00PPFFGG;\">wrong color (should be black color text)</p>";
        testStyledAttributes(color_css_hex_wrong);

    }

}
