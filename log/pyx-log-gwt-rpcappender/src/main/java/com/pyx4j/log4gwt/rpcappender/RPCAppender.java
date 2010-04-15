/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jan 9, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.log4gwt.rpcappender;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Consts;
import com.pyx4j.log4gwt.client.Appender;
import com.pyx4j.log4gwt.client.LogFormatter;
import com.pyx4j.log4gwt.rpc.LogServices;
import com.pyx4j.log4gwt.shared.Level;
import com.pyx4j.log4gwt.shared.LogEvent;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.shared.VoidSerializable;

public class RPCAppender implements Appender {

    private static final Logger log = LoggerFactory.getLogger(RPCAppender.class);

    private static final int BUFFER_MAX = 100;

    private Level level;

    private Vector<LogEvent> buffer = new Vector<LogEvent>();

    private Timer timer;

    private boolean suspendErrorMessages = false;

    private int deliveryErrorCount = 0;

    private long lastDeliveryAttemptTime;

    private static final long deliveryErrorDelayMillis = 4 * Consts.MIN2MSEC;

    public RPCAppender() {
        this(Level.DEBUG);
    }

    public RPCAppender(Level level) {
        this.level = level;
        autoFlush(10 * Consts.SEC2MILLISECONDS);
    }

    @Override
    public String getAppenderName() {
        return "RPC";
    }

    public void autoFlush(int periodMillis) {
        timer = new Timer() {
            @Override
            public void run() {
                flush();
            }
        };
        timer.scheduleRepeating(periodMillis);
    }

    public void close() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    public void doAppend(LogEvent event) {
        if (event.getLevel().ordinal() > this.level.ordinal()) {
            //log.debug("ignore log {} event", event.getLevel());
            return;
        }
        if (buffer.size() > BUFFER_MAX) {
            buffer.remove(0);
        }
        buffer.add(event);
        if (event.getLevel() == Level.ERROR) {
            flush();
        }
    }

    private void flush() {
        try {
            // Do not change timer rate, simply wait more when errors are happening  
            if ((deliveryErrorCount > 3) && ((lastDeliveryAttemptTime + deliveryErrorDelayMillis) < System.currentTimeMillis())) {
                return;
            }
            if (buffer.size() > 0) {
                //log.debug("send log events to server");
                Vector<LogEvent> sendBuffer = buffer;
                buffer = new Vector<LogEvent>();

                for (LogEvent event : sendBuffer) {
                    if (event.getFormatedMessage() == null) {
                        // Create Formated  Serializable Message
                        LogFormatter.format(event, LogFormatter.FormatStyle.LINE);
                    }
                }

                final AsyncCallback<VoidSerializable> callback = new AsyncCallback<VoidSerializable>() {
                    public void onSuccess(VoidSerializable result) {
                        suspendErrorMessages = false;
                        deliveryErrorCount = 0;
                    }

                    public void onFailure(Throwable caught) {
                        GWT.log("Execution of LogServices failed", caught);
                        deliveryErrorCount++;
                        if (!suspendErrorMessages) {
                            log.error("Execution of LogServices failed", caught);
                            suspendErrorMessages = true;
                        }
                    }
                };
                lastDeliveryAttemptTime = System.currentTimeMillis();
                RPCManager.executeBackground(LogServices.Log.class, sendBuffer, callback);
            }
        } catch (Throwable t) {
            GWT.log("Execution of LogServices failed", t);
            log.error("Execution of LogServices failed", t);
        }
    }

}
