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
 * Created on 2013-11-15
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import com.propertyvista.biz.legal.forms.framework.mapping.Partitioner;

public class MockFieldPartitioner implements Partitioner {

    @Override
    public String getPart(String value, int partIndex) {

        switch (partIndex) {
        case 0:
            return value.length() > 7 ? value.substring(0, value.length() - 7) : "";
        case 1:
            if (value.length() > 4) {
                value = value.substring(0, value.length() - 4);
                value = value.substring(Math.max(value.length() - 3, 0));
            } else {
                return "";
            }
        case 2:
            return value.substring(Math.max(value.length() - 4, 0));
        default:
            return "";
        }
    }

}
