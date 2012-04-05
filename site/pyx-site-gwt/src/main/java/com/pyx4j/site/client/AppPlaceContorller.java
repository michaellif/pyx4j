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
 * Created on Aug 9, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceChangeRequestEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

public final class AppPlaceContorller extends PlaceController {

    private static final Logger log = LoggerFactory.getLogger(AppPlaceContorller.class);

    private final EventBus eventBus;

    private AppPlace where = AppPlace.NOWHERE;

    private AppPlace forwardedFrom = AppPlace.NOWHERE;

    private final AppPlaceDispatcher dispatcher;

    public AppPlaceContorller(EventBus eventBus, AppPlaceDispatcher dispatcher) {
        super(eventBus);
        this.eventBus = eventBus;
        this.dispatcher = dispatcher;
        Window.addWindowClosingHandler(new ClosingHandler() {
            @Override
            public void onWindowClosing(ClosingEvent event) {
                String warning = confirmGoTo(AppPlace.NOWHERE);
                if (warning != null) {
                    event.setMessage(warning);
                }
            }
        });
    }

    @Override
    public AppPlace getWhere() {
        return where;
    }

    public Place getForwardedFrom() {
        return forwardedFrom;
    }

    public void goTo(final AppPlace newPlace) {
        maybeGoTo(newPlace);
    }

    /**
     * Shouldn't be called by application. Use goTo(AppPlace) instead
     */
    @Override
    public void goTo(final Place newPlace) {
        //TODO external request - handle unstable tokens. If stable counterpart exists - execute, otherwise go to NOWHERE

        maybeGoTo(newPlace);
    }

    private String confirmGoTo(Place newPlace) {
        PlaceChangeRequestEvent willChange = new PlaceChangeRequestEvent(newPlace);
        eventBus.fireEvent(willChange);
        String warning = willChange.getWarning();
        return warning;
    }

    private void maybeGoTo(final Place newPlace) {
        log.debug("requested to go to: " + newPlace);

        AsyncCallback<AppPlace> callback = new DefaultAsyncCallback<AppPlace>() {

            @Override
            public void onSuccess(AppPlace result) {
                if (result == null) {
                    where = AppPlace.NOWHERE;
                    return;
                } else {
                    where = result;
                }

                String warning = confirmGoTo(result);
                if (warning != null) {
                    dispatcher.confirm(warning, new Command() {

                        @Override
                        public void execute() {
                            sureGoTo(newPlace);
                        }
                    });
                } else {
                    sureGoTo(newPlace);
                }
            }

        };

        dispatcher.forwardTo((AppPlace) newPlace, callback);
    }

    private void sureGoTo(Place newPlace) {
        if (where.equals(newPlace)) {
            forwardedFrom = AppPlace.NOWHERE;
            log.debug("go to: " + where);
        } else {
            forwardedFrom = (AppPlace) newPlace;
            log.debug("forwarded to: {} from {}", where, forwardedFrom);
        }
        eventBus.fireEvent(new PlaceChangeEvent(where));
    }
}
