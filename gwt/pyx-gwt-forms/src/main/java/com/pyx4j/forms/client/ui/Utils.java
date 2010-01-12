/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.util.Date;

public class Utils {

    static boolean equals(Object a, Object b) {
        return ((a == b) || ((a != null) && a.equals(b)));
    }

    @SuppressWarnings("deprecation")
    public static boolean isSameDay(Date first, Date second) {
        if (first == null && second == null) {
            return true;
        } else if (first == null ^ second == null) {
            return false;
        }
        if (first.getYear() != second.getYear()) {
            return false;
        }
        if (first.getMonth() != second.getMonth()) {
            return false;
        }
        if (first.getDate() != second.getDate()) {
            return false;
        }

        return true;
    }

}
