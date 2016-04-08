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

public class SizeFormatsTest extends StyledFeaturesBase {

    private static final String SIZE_CSS_UNITS_PT = "<span style=\"font-size:10pt;\">10 pixels sized html text</span>";

    private static final String SIZE_CSS_UNITS_EMPTY = "<span style=\"font-size:10;\">10 no units sized html text</span>";

    private static final String SIZE_CSS_NAME_1 = "<span style=\"font-size:x-small;\">x-small sized html text</span>";

    private static final String SIZE_CSS_NAME_2 = "<span style=\"font-size:x-large;\">x-large sized html text</span>";

    private static final String SIZE_CSS_NAME_WRONG = "<span style=\"font-size:invented-size;\">x-large sized html text</span>";

    @Test
    public void testBoldCases() throws IOException {

        testStyledAttributes(SIZE_CSS_UNITS_PT, createCssStyleSizeAttribute("10pt"));

        testStyledAttributes(SIZE_CSS_UNITS_EMPTY, createCssStyleSizeAttribute("10"));

        testStyledAttributes(SIZE_CSS_NAME_1, createCssStyleSizeAttribute("x-small"));

        testStyledAttributes(SIZE_CSS_NAME_2, createCssStyleSizeAttribute("x-large"));

        testStyledAttributes(SIZE_CSS_NAME_WRONG, createCssStyleSizeAttribute("medium")); // Wrong value, return default size

    }

}