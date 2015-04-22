/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Apr 16, 2015
 * @author vlads
 */
package com.pyx4j.essentials.server.dev;

import java.io.PrintWriter;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class StackTraceView {

    public void printSystemStackTrace(PrintWriter out) {
        out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        out.println("\n");

        Map<Long, Thread> threadsById = new HashMap<>();
        for (Map.Entry<Thread, StackTraceElement[]> me : Thread.getAllStackTraces().entrySet()) {
            threadsById.put(me.getKey().getId(), me.getKey());
        }

        TreeMap<String, String> threadsInfoSorted = new TreeMap<>();

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        for (ThreadInfo threadInfo : threadMXBean.dumpAllThreads(true, false)) {
            String key = "";
            StringBuilder info = new StringBuilder();
            Thread thread = threadsById.get(threadInfo.getThreadId());
            if (thread != null) {
                key = thread.getThreadGroup().getName();
                info.append(thread.getThreadGroup().getName()).append(" ");
            }
            key += "." + threadInfo.getThreadName();

            info.append(toStringFull(threadInfo));

            threadsInfoSorted.put(key, info.toString());
        }

        for (String info : threadsInfoSorted.values()) {
            out.print(info);
        }
    }

    protected String toStringFull(ThreadInfo t) {
        StringBuilder sb = new StringBuilder("\"" + t.getThreadName() + "\"" + " Id=" + t.getThreadId() + " " + t.getThreadState());
        if (t.getLockName() != null) {
            sb.append(" on " + t.getLockName());
        }
        if (t.getLockOwnerName() != null) {
            sb.append(" owned by \"" + t.getLockOwnerName() + "\" Id=" + t.getLockOwnerId());
        }
        if (t.isSuspended()) {
            sb.append(" (suspended)");
        }
        if (t.isInNative()) {
            sb.append(" (in native)");
        }
        sb.append('\n');

        for (int i = 0; i < t.getStackTrace().length; i++) {
            StackTraceElement ste = t.getStackTrace()[i];
            sb.append("\tat " + ste.toString());
            sb.append('\n');
            if (i == 0 && t.getLockInfo() != null) {
                Thread.State ts = t.getThreadState();
                switch (ts) {
                case BLOCKED:
                    sb.append("\t-  blocked on " + t.getLockInfo());
                    sb.append('\n');
                    break;
                case WAITING:
                    sb.append("\t-  waiting on " + t.getLockInfo());
                    sb.append('\n');
                    break;
                case TIMED_WAITING:
                    sb.append("\t-  waiting on " + t.getLockInfo());
                    sb.append('\n');
                    break;
                default:
                }
            }

            for (MonitorInfo mi : t.getLockedMonitors()) {
                if (mi.getLockedStackDepth() == i) {
                    sb.append("\t-  locked " + mi);
                    sb.append('\n');
                }
            }
        }

        LockInfo[] locks = t.getLockedSynchronizers();
        if (locks.length > 0) {
            sb.append("\n\tNumber of locked synchronizers = " + locks.length);
            sb.append('\n');
            for (LockInfo li : locks) {
                sb.append("\t- " + li);
                sb.append('\n');
            }
        }

        sb.append('\n');
        return sb.toString();
    }
}
