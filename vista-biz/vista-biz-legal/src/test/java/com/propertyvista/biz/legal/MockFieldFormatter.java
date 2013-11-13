/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2013-11-13
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import com.propertyvista.domain.legal.utils.PdfFormFieldFormatter;

/**
 * separates letters by <code>!</code>, i.e <code>abcde</code> will be turned into <code>a!b!c!d!e!</code>
 */
public class MockFieldFormatter implements PdfFormFieldFormatter {

    @Override
    public String format(Object object) {
        String value = object.toString();
        String formattedValue = "";
        for (int i = 0; i < value.length(); ++i) {
            formattedValue += value.charAt(i);
            formattedValue += "!";
        }
        return formattedValue;
    }
}
