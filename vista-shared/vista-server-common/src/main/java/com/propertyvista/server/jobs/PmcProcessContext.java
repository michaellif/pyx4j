/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.jobs;

import com.propertyvista.admin.domain.scheduler.RunStats;

public class PmcProcessContext {

    private static class ProcessContextData {

        RunStats runStats;

    }

    private static final ThreadLocal<ProcessContextData> requestLocal = new ThreadLocal<ProcessContextData>();

    public static RunStats getRunStats() {
        return requestLocal.get().runStats;
    }

    public static void setRunStats(RunStats stats) {
        if (requestLocal.get() == null) {
            requestLocal.set(new ProcessContextData());
        }
        requestLocal.get().runStats = stats;
        if (requestLocal.get().runStats != null) {
            requestLocal.get().runStats.updateTime().setValue(System.currentTimeMillis());
        }
    }

    public static void remove() {
        requestLocal.remove();
    }

}
