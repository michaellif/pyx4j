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

    private static final ThreadLocal<AtomicLong> requestTimeTotal = new ThreadLocal<>();

    private static final ThreadLocal<AtomicLong> requestTimeMax = new ThreadLocal<>();

    public static void start() {
        requestTimeTotal.set(new AtomicLong());
        requestTimeMax.set(new AtomicLong());
    }

    public static void add(long interval) {
        if (requestTimeTotal.get() != null) {
            requestTimeTotal.get().addAndGet(interval);
        }
    }

    public static void requestCompleted(long interval) {
        AtomicLong maxTime = requestTimeMax.get();
        if (maxTime != null) {
            maxTime.set(Math.max(maxTime.get(), interval));
        }
    }

    public static void stop(AtomicReference<Long> yardiTimeTotal, AtomicReference<Long> maxTimeResult) {
        if (requestTimeTotal.get() != null) {
            yardiTimeTotal.set(requestTimeTotal.get().get());
        }
        if ((maxTimeResult != null) && (requestTimeMax.get() != null)) {
            maxTimeResult.set(requestTimeMax.get().get());
        }
        requestTimeTotal.remove();
        requestTimeMax.remove();
    }
}
