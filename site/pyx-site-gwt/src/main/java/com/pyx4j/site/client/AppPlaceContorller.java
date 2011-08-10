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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceChangeRequestEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;

import com.pyx4j.site.rpc.AppPlace;

public class AppPlaceContorller extends PlaceController {

    private static final Logger log = LoggerFactory.getLogger(AppPlaceContorller.class);

    private final EventBus eventBus;

    private Place where = Place.NOWHERE;

    private Place forwardedFrom = Place.NOWHERE;

    private final AppPlaceDispatcher dispatcher;

    public AppPlaceContorller(EventBus eventBus, AppPlaceDispatcher dispatcher) {
        super(eventBus);
        this.eventBus = eventBus;
        this.dispatcher = dispatcher;
        Window.addWindowClosingHandler(new ClosingHandler() {
            @Override
            public void onWindowClosing(ClosingEvent event) {
                String warning = maybeGoTo(Place.NOWHERE);
                if (warning != null) {
                    event.setMessage(warning);
                }
            }
        });
    }

    @Override
    public Place getWhere() {
        return where;
    }

    public Place getForwardedFrom() {
        return forwardedFrom;
    }

    public boolean confirm(String message) {
        return Window.confirm(message);
    }

    @Override
    public void goTo(Place newPlace) {
        log.debug("goTo: " + newPlace);

        if (getWhere().equals(newPlace)) {
            log.debug("Asked to return to the same place: " + newPlace);
            return;
        }
        where = dispatcher.forwardTo((AppPlace) newPlace);

        if (where == null) {
            where = Place.NOWHERE;
            return;
        }

        String warning = maybeGoTo(newPlace);
        if (warning == null || confirm(warning)) {
            if (where == newPlace) {
                forwardedFrom = Place.NOWHERE;
            } else {
                forwardedFrom = newPlace;
            }
            eventBus.fireEvent(new PlaceChangeEvent(newPlace));
        }
    }

    private String maybeGoTo(Place newPlace) {
        PlaceChangeRequestEvent willChange = new PlaceChangeRequestEvent(newPlace);
        eventBus.fireEvent(willChange);
        String warning = willChange.getWarning();
        return warning;
    }
}
