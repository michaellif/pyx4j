/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
