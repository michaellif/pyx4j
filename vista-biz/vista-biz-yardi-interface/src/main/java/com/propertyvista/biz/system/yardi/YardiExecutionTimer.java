/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 7, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.system.yardi;

import java.util.concurrent.atomic.AtomicLong;

public class YardiExecutionTimer {
    private static final ThreadLocal<AtomicLong> timerHolder = new ThreadLocal<>();

    public static void start() {
        timerHolder.set(new AtomicLong());
    }

    public static void add(long interval) {
        if (timerHolder.get() != null) {
            timerHolder.get().addAndGet(interval);
        }
    }

    public static long stop() {
        long time = timerHolder.get() == null ? 0 : timerHolder.get().get();
        timerHolder.remove();
        return time;
    }
}
