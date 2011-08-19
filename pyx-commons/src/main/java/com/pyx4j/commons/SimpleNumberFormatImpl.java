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
 * Created on Aug 19, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

import java.text.DecimalFormat;

class SimpleNumberFormatImpl {

    public static String format(String pattern, Number number) {
        DecimalFormat fmt;
        if (pattern == null) {
            fmt = new DecimalFormat();
        } else {
            fmt = new DecimalFormat(pattern);
        }
        return fmt.format(number);
    }

}
