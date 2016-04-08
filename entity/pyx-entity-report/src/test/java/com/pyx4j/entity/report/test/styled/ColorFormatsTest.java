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

import org.junit.Test;

public class ColorFormatsTest extends StyledFeaturesBase {

    private static final String COLOR_CSS_NAME = "<p style=\"color:red;\">red color text</p>";

    private static final String COLOR_CSS_NAME_WRONG = "<p style=\"color:invented-color;\">invented color (should be black color text)</p>";

    private static final String COLOR_CSS_RGB = "<p style=\"color:rgb(0,255,0);\">green color text</p>";

    private static final String COLOR_CSS_RGB_WRONG = "<p style=\"color:rgb(0,255,255,0);\">wrong color (should be black color text)</p>";

    private static final String COLOR_CSS_HEX = "<p style=\"color:#0000FF;\">blue color text</p>";

    private static final String COLOR_CSS_HEX_WRONG = "<p style=\"color:#00PPFFGG;\">wrong color (should be black color text)</p>";

    @Test
    public void testBoldCases() throws IOException {

        testStyledAttributes(COLOR_CSS_NAME, createStyledColorAttribute("red"));

        testStyledAttributes(COLOR_CSS_NAME_WRONG, createStyledColorAttribute("black"));

        testStyledAttributes(COLOR_CSS_RGB, createStyledColorAttribute("green"));

        testStyledAttributes(COLOR_CSS_RGB_WRONG, createStyledColorAttribute("black"));

        testStyledAttributes(COLOR_CSS_HEX, createStyledColorAttribute("blue"));

        testStyledAttributes(COLOR_CSS_HEX_WRONG, createStyledColorAttribute("black"));

    }

}
