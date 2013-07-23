/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Feb 22, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.site.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.user.client.Timer;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.RPCStatusChangeEvent;
import com.pyx4j.rpc.client.RPCStatusChangeHandler;

class AppDevStartTimeMonitor {

    private static final Logger log = LoggerFactory.getLogger(AppDevStartTimeMonitor.class);

    private static AppDevStartTimeMonitor instance;

    private final HandlerRegistration placeChangeHandlerRegistration;

    private final HandlerRegistration rpcHandlerRegistration;

    private int placeChanging;

    private long lasEventTime;

    private boolean lasEventRpcIdle;

    private final Timer idleDetectionTimer;

    public static void start() {
        if (instance == null) {
            instance = new AppDevStartTimeMonitor();
        }
    }

    AppDevStartTimeMonitor() {
        placeChangeHandlerRegistration = AppSite.getEventBus().addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
            @Override
            public void onPlaceChange(PlaceChangeEvent event) {
                placeChanging++;
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        placeChanging--;
                        lasEventTime = System.currentTimeMillis();
                    }

                });
            }
        });

        rpcHandlerRegistration = RPCManager.addRPCStatusChangeHandler(new RPCStatusChangeHandler() {

            @Override
            public void onRPCStatusChange(RPCStatusChangeEvent event) {
                lasEventRpcIdle = event.isRpcIdle();
                lasEventTime = System.currentTimeMillis();
            }
        });

        idleDetectionTimer = new Timer() {
            @Override
            public void run() {
                idleDetection();
            }
        };
        idleDetectionTimer.scheduleRepeating(2000);
    }

    private void idleDetection() {
        if ((!lasEventRpcIdle) || (placeChanging > 0)) {
            return;
        }
        if ((System.currentTimeMillis() - lasEventTime) < 2000) {
            return;
        }
        long duration = lasEventTime - AppSite.instance().applicationStartTime;
        log.info("Application initialized in {}", TimeUtils.durationFormat(duration));
        idleDetectionTimer.cancel();
        placeChangeHandlerRegistration.removeHandler();
        rpcHandlerRegistration.removeHandler();
    }
}
