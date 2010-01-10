/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * Created on 14-Sep-06
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

public class Consts {

    public final static int SEC2MSEC = 1000;

    public final static int SEC2MILLISECONDS = SEC2MSEC;

    public final static int SEC2MICROSECONDS = SEC2MILLISECONDS * 1000;

    public final static int MIN2SEC = 60;

    public final static long MIN2MSEC = MIN2SEC * SEC2MSEC;

    public final static int HOURS2MIN = 60;

    public final static int HOURS2SEC = HOURS2MIN * MIN2SEC;

    public final static long HOURS2MSEC = HOURS2MIN * MIN2MSEC;

    public final static int DAY2HOURS = 24;

    public final static long DAY2MSEC = DAY2HOURS * HOURS2MSEC;

    public final static int MSEC2NANO = 1000 * 1000;

    public final static int SEC2NANO = SEC2MICROSECONDS * 1000;
}
