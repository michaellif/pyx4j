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
 * Created on Jun 10, 2011
 * @author michaellif
 */
package com.pyx4j.site.client.activity;

import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.ResettableEventBus;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceChangeRequestEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

public class AppActivityManager implements PlaceChangeEvent.Handler, PlaceChangeRequestEvent.Handler {

    private final static Logger log = LoggerFactory.getLogger(AppActivityManager.class);

    private static final Activity NULL_ACTIVITY = new AbstractActivity() {
        @Override
        public void start(AcceptsOneWidget panel, com.google.gwt.event.shared.EventBus eventBus) {
        }
    };

    // This will not be much use in obfuscated mode. The change shuld nto be commited.
    private final boolean debugActivityCalls = false;

    private final AppActivityMapper mapper;

    private final EventBus eventBus;

    private final ResettableEventBus stopperedEventBus;

    private Activity currentActivity = NULL_ACTIVITY;

    private AcceptsOneWidget display;

    private boolean startingNext = false;

    private HandlerRegistration handlerRegistration;

    /**
     * Create an ActivityManager. Next call {@link #setDisplay}.
     *
     * @param mapper
     *            finds the {@link Activity} for a given {@link com.google.gwt.place.shared.Place}
     * @param eventBus
     *            source of {@link PlaceChangeEvent} and {@link PlaceChangeRequestEvent} events.
     */
    public AppActivityManager(AppActivityMapper mapper, EventBus eventBus) {
        this.mapper = mapper;
        this.eventBus = eventBus;
        this.stopperedEventBus = new ResettableEventBus(eventBus);

    }

    /**
     * Deactivate the current activity, find the next one from our ActivityMapper,
     * and start it.
     * <p>
     * The current activity's widget will be hidden immediately, which can cause flicker if the next activity provides its widget asynchronously. That can be
     * minimized by decent caching. Perenially slow activities might mitigate this by providing a widget immediately, with some kind of "loading" treatment.
     *
     * @see com.google.gwt.place.shared.PlaceChangeEvent.Handler#onPlaceChange(PlaceChangeEvent)
     */
    @Override
    public void onPlaceChange(PlaceChangeEvent event) {
        obtainNextActivity(event, new DefaultAsyncCallback<Activity>() {

            @Override
            public void onSuccess(Activity nextActivity) {
                Throwable caughtOnStop = null;
                Throwable caughtOnStart = null;

                if (nextActivity == null) {
                    nextActivity = NULL_ACTIVITY;
                }

                if (currentActivity.equals(nextActivity)) {
                    return;
                }

                if (startingNext) {
                    if (debugActivityCalls) {
                        log.debug("{}.onCancel()", currentActivity.getClass().getName());
                    }
                    // The place changed again before the new current activity showed its widget
                    currentActivity.onCancel();
                    currentActivity = NULL_ACTIVITY;
                    startingNext = false;
                } else if (!currentActivity.equals(NULL_ACTIVITY)) {
                    showWidget(null);

                    /*
                     * Kill off the activity's handlers, so it doesn't have to worry about
                     * them accidentally firing as a side effect of its tear down
                     */
                    stopperedEventBus.removeHandlers();

                    if (debugActivityCalls) {
                        log.debug("{}.onStop()", currentActivity.getClass().getName());
                    }

                    try {
                        currentActivity.onStop();
                    } catch (Throwable t) {
                        caughtOnStop = t;
                    } finally {
                        /*
                         * And kill them off again in case it was naughty and added new ones
                         * during onstop
                         */
                        stopperedEventBus.removeHandlers();
                    }
                }

                currentActivity = nextActivity;

                if (currentActivity.equals(NULL_ACTIVITY)) {
                    showWidget(null);
                    return;
                }

                startingNext = true;

                /*
                 * Now start the thing. Wrap the actual display with a per-call instance
                 * that protects the display from canceled or stopped activities, and which
                 * maintains our startingNext state.
                 */
                if (debugActivityCalls) {
                    log.debug("{}.start()", currentActivity.getClass().getName());
                }
                try {
                    currentActivity.start(new ProtectedDisplay(currentActivity), stopperedEventBus);
                } catch (Throwable t) {
                    caughtOnStart = t;
                }

                if (caughtOnStart != null || caughtOnStop != null) {
                    Set<Throwable> causes = new LinkedHashSet<Throwable>();
                    if (caughtOnStop != null) {
                        causes.add(caughtOnStop);
                    }
                    if (caughtOnStart != null) {
                        causes.add(caughtOnStart);
                    }

                    throw new UmbrellaException(causes);
                }
            }
        });

    }

    /**
     * Reject the place change if the current activity is not willing to stop.
     *
     * @see com.google.gwt.place.shared.PlaceChangeRequestEvent.Handler#onPlaceChangeRequest(PlaceChangeRequestEvent)
     */
    @Override
    public void onPlaceChangeRequest(PlaceChangeRequestEvent event) {
        if (!currentActivity.equals(NULL_ACTIVITY)) {
            event.setWarning(currentActivity.mayStop());
        }
    }

    /**
     * Sets the display for the receiver, and has the side effect of starting or
     * stopping its monitoring the event bus for place change events.
     * <p>
     * If you are disposing of an ActivityManager, it is important to call setDisplay(null) to get it to deregister from the event bus, so that it can be
     * garbage collected.
     *
     * @param display
     *            an instance of AcceptsOneWidget
     */
    public void setDisplay(AcceptsOneWidget display) {
        boolean wasActive = (null != this.display);
        boolean willBeActive = (null != display);
        this.display = display;
        if (wasActive != willBeActive) {
            updateHandlers(willBeActive);
        }
    }

    private void obtainNextActivity(PlaceChangeEvent event, AsyncCallback<Activity> callback) {
        if (display == null) {
            /*
             * Display may have been nulled during PlaceChangeEvent dispatch. Don't
             * bother the mapper, just return a null to ensure we shut down the
             * current activity
             */
            callback.onSuccess(null);
        }
        mapper.obtainActivity((AppPlace) event.getNewPlace(), callback);
    }

    private void showWidget(IsWidget view) {
        if (display != null) {
            display.setWidget(view);
        }
    }

    private void updateHandlers(boolean activate) {
        if (activate) {
            final HandlerRegistration placeReg = eventBus.addHandler(PlaceChangeEvent.TYPE, this);
            final HandlerRegistration placeRequestReg = eventBus.addHandler(PlaceChangeRequestEvent.TYPE, this);

            this.handlerRegistration = new HandlerRegistration() {
                @Override
                public void removeHandler() {
                    placeReg.removeHandler();
                    placeRequestReg.removeHandler();
                }
            };
        } else {
            if (handlerRegistration != null) {
                handlerRegistration.removeHandler();
                handlerRegistration = null;
            }
        }
    }

    /**
     * Wraps our real display to prevent an Activity from taking it over if it is
     * not the currentActivity.
     */
    private class ProtectedDisplay implements AcceptsOneWidget {
        private final Activity activity;

        ProtectedDisplay(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void setWidget(IsWidget view) {
            if (this.activity == AppActivityManager.this.currentActivity) {
                startingNext = false;
                showWidget(view);
            }
        }
    }
}
