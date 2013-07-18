/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-28
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.proc;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.pyx4j.commons.Key;

import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.server.jobs.PmcProcess;
import com.propertyvista.server.jobs.PmcProcessContext;

public class PmcProcessMonitor {

    private static final Object monitor = new Object();

    private static int runningExecutionsCount = 0;

    private static boolean shuttingDown = false;

    public final static int SHUTDOWN_TIMEOUT = 30 * 1000;

    private static Map<Key, ProcessMonitorContext> runningExecutions = new HashMap<Key, ProcessMonitorContext>();

    private static Set<Key> pendingTerminationExecutions = Collections.synchronizedSet(new HashSet<Key>());

    private static class ProcessMonitorContext {

        PmcProcess pmcProcess;

        PmcProcessContext context;
    }

    public static boolean isShuttingDown() {
        return shuttingDown;
    }

    public static void onExecutionStart(Run executionId) {
        synchronized (monitor) {
            runningExecutionsCount++;
            monitor.notifyAll();
            runningExecutions.put(executionId.getPrimaryKey(), new ProcessMonitorContext());
            pendingTerminationExecutions.remove(executionId.getPrimaryKey());
        }
    }

    public static void setContext(Run executionId, PmcProcess pmcProcess, PmcProcessContext context) {
        ProcessMonitorContext mc = runningExecutions.get(executionId.getPrimaryKey());
        mc.context = context;
        mc.pmcProcess = pmcProcess;
    }

    public static void onExecutionEnds(Run executionId) {
        synchronized (monitor) {
            runningExecutionsCount--;
            monitor.notifyAll();
            runningExecutions.remove(executionId.getPrimaryKey());
        }
        pendingTerminationExecutions.remove(executionId.getPrimaryKey());
    }

    public static boolean isRunning(Run executionId) {
        return runningExecutions.containsKey(executionId.getPrimaryKey());
    }

    public static void requestExecutionTermination(Run executionId) {
        pendingTerminationExecutions.add(executionId.getPrimaryKey());
        ProcessMonitorContext mc = runningExecutions.get(executionId.getPrimaryKey());
        if ((mc != null) && (mc.context != null)) {
            mc.context.getExecutionMonitor().requestTermination();
        }
    }

    public static boolean isPendingTermination(Run executionId) {
        if (isShuttingDown() || pendingTerminationExecutions.contains(executionId.getPrimaryKey())) {
            return true;
        } else {
            ProcessMonitorContext mc = runningExecutions.get(executionId.getPrimaryKey());
            if ((mc != null) && (mc.context != null)) {
                return mc.context.getExecutionMonitor().isTerminationRequested();
            } else {
                return false;
            }
        }
    }

    public static void shutdown() {
        shuttingDown = true;
        long waitEnds = System.currentTimeMillis() + SHUTDOWN_TIMEOUT;
        while ((runningExecutionsCount > 0) && (waitEnds < System.currentTimeMillis())) {
            synchronized (monitor) {
                try {
                    monitor.wait(SHUTDOWN_TIMEOUT);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

}
