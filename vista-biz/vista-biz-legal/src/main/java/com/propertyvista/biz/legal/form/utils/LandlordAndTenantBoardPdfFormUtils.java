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
 * Created on 2013-11-19
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal.form.utils;

import java.util.ArrayList;
import java.util.List;

public class LandlordAndTenantBoardPdfFormUtils {

    public static List<String> split(String fieldName, int... partLengthVector) {
        List<String> fieldNames = new ArrayList<String>(partLengthVector.length);
        for (int i = 0; i < partLengthVector.length; ++i) {
            fieldNames.add(fieldName + "." + i + "{" + partLengthVector[i] + "}");
        }
        return fieldNames;
    }
}
