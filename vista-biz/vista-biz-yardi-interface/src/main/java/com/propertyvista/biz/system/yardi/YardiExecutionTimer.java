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
import java.util.concurrent.atomic.AtomicReference;

public class YardiExecutionTimer {
    private static final ThreadLocal<AtomicLong> timerHolder = new ThreadLocal<>();

    private static final ThreadLocal<AtomicLong> maxRequestTime = new ThreadLocal<>();

    public static void start() {
        timerHolder.set(new AtomicLong());
        maxRequestTime.set(new AtomicLong());
    }

    public static void add(long interval) {
        if (timerHolder.get() != null) {
            timerHolder.get().addAndGet(interval);
        }
    }

    public static void requestCompleted(long interval) {
        AtomicLong maxTime = maxRequestTime.get();
        if (maxTime != null) {
            maxTime.set(Math.max(maxTime.get(), interval));
        }
    }

    public static long stop() {
        return stop(null);
    }

    public static long stop(AtomicReference<Long> maxTimeResult) {
        long time = timerHolder.get() == null ? 0 : timerHolder.get().get();
        if (maxTimeResult != null) {
            maxTimeResult.set(maxRequestTime.get().get());
        }
        timerHolder.remove();
        maxRequestTime.remove();
        return time;
    }
}
