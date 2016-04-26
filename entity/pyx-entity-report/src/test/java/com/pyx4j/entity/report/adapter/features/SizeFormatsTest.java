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

public class SizeFormatsTest extends StyledFeaturesBase {

    @Test
    public void testSizeFormatCases() throws IOException {

        final String units_pt = "<span style=\"font-size:10pt;\">10 pixels sized html text</span>";
        testStyledAttributes(units_pt, createCssStyleSizeAttribute("10pt"));

        final String units_empty = "<span style=\"font-size:10;\">10 no units sized html text</span>";
        testStyledAttributes(units_empty, createCssStyleSizeAttribute("10"));

        final String name_1 = "<span style=\"font-size:x-small;\">x-small sized html text</span>";
        testStyledAttributes(name_1, createCssStyleSizeAttribute("x-small"));

        final String name_2 = "<span style=\"font-size:x-large;\">x-large sized html text</span>";
        testStyledAttributes(name_2, createCssStyleSizeAttribute("x-large"));

        final String name_wrong = "<span style=\"font-size:invented-size;\">x-large sized html text</span>";
        testStyledAttributes(name_wrong);

    }

}