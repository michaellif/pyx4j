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
 * Created on Apr 16, 2011
 * @author dmitry
 * @version $Id$
 */
package com.pyx4j.entity.server;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeUtils extends com.pyx4j.commons.TimeUtils {

    public static Date getRoundedNow() {
        Calendar c = calRoundedNow();
        return c.getTime();
    }

    public static Calendar calRoundedNow() {
        Calendar c = new GregorianCalendar();
        // DB does not store Milliseconds
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }
}
