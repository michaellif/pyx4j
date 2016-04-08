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

public class BackgroundColorTest extends StyledFeaturesBase {

    private static final String SIMPLE_BACKGROUND_CSS_1 = "<p style=\"background-color:red;\">black color text over red background color</p>";

    private static final String SIMPLE_BACKGROUND_CSS_2 = "<p style=\"color:green; background-color:yellow;\">black color text over red background color</p>";

    @Test
    public void testBoldCases() throws IOException {

        testStyledAttributes(SIMPLE_BACKGROUND_CSS_1, createStyledBackgroundColorAttribute("red"));

        testStyledAttributes(SIMPLE_BACKGROUND_CSS_2, new Attribute[] { createStyledColorAttribute("green"), createStyledBackgroundColorAttribute("yellow") });

    }

}
