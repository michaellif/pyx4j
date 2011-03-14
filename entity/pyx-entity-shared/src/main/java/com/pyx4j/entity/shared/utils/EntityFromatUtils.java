/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2011-03-14
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.shared.utils;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.shared.IPrimitive;

public class EntityFromatUtils {

    public static String nvl_concat(String sep, IPrimitive<String>... values) {
        StringBuilder b = new StringBuilder();
        for (IPrimitive<String> s : values) {
            if (CommonsStringUtils.isEmpty(s.getValue())) {
                continue;
            }
            if (b.length() > 0) {
                b.append(sep);
            }
            b.append(s.getValue());
        }
        return b.toString();
    }
}
