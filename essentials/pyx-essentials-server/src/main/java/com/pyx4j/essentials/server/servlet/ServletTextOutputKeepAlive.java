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
 * Created on Sep 13, 2015
 * @author vlads
 */
package com.pyx4j.essentials.server.servlet;

import java.io.Closeable;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.pyx4j.commons.Consts;
import com.pyx4j.commons.Timeout;

/**
 * pure man solution for long running CI Servlet.
 */
public class ServletTextOutputKeepAlive implements Closeable {

    private ServletTextOutput out;

    private ProcessTimeOutMonitor monitorTimerTask;

    private long period;

    private String printHtml;

    public ServletTextOutputKeepAlive(ServletTextOutput out, int periodSeconds, String printHtml) {
        this.out = out;
        period = Consts.SEC2MILLISECONDS * periodSeconds;
        this.printHtml = printHtml;
    }

    private class ProcessTimeOutMonitor extends TimerTask {

        private Timeout until;

        public ProcessTimeOutMonitor(int maxDurationMinutes) {
            until = new Timeout(maxDurationMinutes * Consts.MIN2MSEC);
        }

        @Override
        public void run() {
            if (until.timeout()) {
                cancel();
            } else {
                try {
                    out.html(printHtml);
                } catch (IOException e) {
                    cancel();
                }
            }
        }

    }

    public void start(int maxDurationMinutes) {
        stop();
        Timer monitorTimer = new Timer("ServletTextOutputKeepAlive");
        monitorTimer.schedule(monitorTimerTask = new ProcessTimeOutMonitor(maxDurationMinutes), period, period);
    }

    public void stop() {
        if (monitorTimerTask != null) {
            monitorTimerTask.cancel();
            monitorTimerTask = null;
        }
    }

    @Override
    public void close() {
        stop();
    }

}
